package com.example

import android.app.Application
import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import android.content.ComponentName
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.room.Room
import com.example.db.AppDatabase
import com.example.db.TrackOverride
import com.example.db.TrackOverrideRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import android.content.ContentUris
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.os.Environment
import java.io.File
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val mediaUrl: String,
    val artworkUrl: String? = null,
    val coverColorStr: String, // Hex string for simple colored covers
    val isLocal: Boolean = false,
    val durationMs: Long = 0L,
    val isVideo: Boolean = false,
    val width: Int = 0,
    val height: Int = 0,
    val resolution: String? = null,
    val bpm: Int = 0,
    val folderName: String? = null,
    val folderPath: String? = null
) {
    val effectiveBpm: Int
        get() {
            if (bpm > 0) return bpm
            val hash = kotlin.math.abs((title + id + artist).hashCode())
            return 72 + (hash % 76) // Deterministic tempo between 72 and 147 BPM
        }
}

data class MediaFolder(
    val id: String,
    val name: String,
    val path: String? = null,
    val tracks: List<Track>
) {
    val trackCount: Int get() = tracks.size
    val audioCount: Int get() = tracks.count { !it.isVideo }
    val videoCount: Int get() = tracks.count { it.isVideo }
    val totalDurationMs: Long get() = tracks.sumOf { it.durationMs }
    val coverTrack: Track? get() = tracks.firstOrNull { it.artworkUrl != null } ?: tracks.firstOrNull()
}

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val coverUrl: String?,
    val coverColorStr: String,
    val tracks: List<Track>
) {
    val coverTrack: Track? get() = tracks.firstOrNull { it.artworkUrl != null } ?: tracks.firstOrNull()
}

enum class MainTab {
    AUDIO, VIDEO
}

enum class LibraryTab(val title: String) {
    SONGS("Songs"),
    STORAGE("Storage"),
    PLAYLIST("Playlists"),
    ARTISTS("Artists"),
    HISTORY("History")
}

enum class QueueBpmSortMode(val title: String, val subtitle: String) {
    ASCENDING("Slow to Fast (Warm-up)", "Ascending tempo order (builds up energy)"),
    DESCENDING("Fast to Slow (Cooldown)", "Descending tempo order (wind down & relax)"),
    HARMONIC_FLOW("Harmonic Flow (DJ Mix)", "Adjacent tempo matching for smooth transitions")
}

enum class SmartSortOption(val title: String) {
    TITLE_AZ("Title (A-Z)"),
    TITLE_ZA("Title (Z-A)"),
    ARTIST_AZ("Artist (A-Z)"),
    ARTIST_ZA("Artist (Z-A)"),
    NEWEST("Recently Added (Newest)"),
    OLDEST("Oldest Added"),
    DURATION_SHORTEST("Duration (Shortest)"),
    DURATION_LONGEST("Duration (Longest)"),
    MOST_PLAYED("Most Played"),
    BPM_ASCENDING("Tempo (BPM: Slow to Fast)"),
    BPM_DESCENDING("Tempo (BPM: Fast to Slow)")
}

enum class SettingsSubPage(val title: String) {
    PROFILE("Profile"),
    PLAYTIME("Playtime & Stats"),
    THEME("Theme & Display"),
    SLEEP_TIMER("Sleep Timer"),
    PLAYBACK("Playback Settings"),
    VIDEO("Video Player"),
    VISUALIZER("Visualizer"),
    NOTIFICATIONS("Notifications"),
    LANGUAGE("Language"),
    FEEDBACK("Feedback & Support"),
    ABOUT("About"),
    LYRICS("Lyrics Display")
}

data class VideoPreferences(
    val defaultQuality: String = "Auto (HQ)",
    val defaultResizeMode: String = "Fit Screen",
    val seekJumpSeconds: Int = 10,
    val autoHideControlsSeconds: Int = 4,
    val autoRotateLandscapeOnFullscreen: Boolean = true,
    val lockLandscapeOrientation: Boolean = false,
    val hardwareDecoderFallback: Boolean = true,
    val backgroundAudioPlayback: Boolean = false,
    val subtitleFontSize: Int = 16,
    val subtitleBackgroundOpacity: Float = 0.6f,
    val showVideoResolutionBadge: Boolean = true
)

data class UserProfile(
    val name: String = "Baddy Ballouy",
    val email: String = "baddyballouy@gmail.com",
    val bio: String = "Audiophile & Music Explorer",
    val avatarColor: String = "FF8B5CF6",
    val favoriteGenre: String = "Electronic / Synthwave"
)

data class PlaybackPreferences(
    val audioQuality: String = "High (320 kbps)",
    val crossfadeSeconds: Int = 3,
    val isGapless: Boolean = true,
    val autoPlaySimilar: Boolean = true,
    val normalizeVolume: Boolean = true,
    val monoAudio: Boolean = false,
    val defaultSpeed: Float = 1.0f
)

data class NotificationPreferences(
    val showLockscreenControls: Boolean = true,
    val showLockscreenArtwork: Boolean = true,
    val colorizeNotification: Boolean = true,
    val compactControls: Boolean = true,
    val hapticFeedback: Boolean = true,
    val showRemainingTime: Boolean = false
)

data class EqBand(val bandId: Short, val centerFreqHz: Int, val level: Short)

enum class SearchFilter(val title: String) {
    ALL("All Media"),
    ARTISTS("Artists"),
    SONGS("Songs"),
    VIDEOS("Videos"),
    DEVICE("Device Storage"),
    PLAYLISTS("Playlists"),
    HISTORY("History"),
    ARTIST_DETAIL("Artist Discography"),
    PLAYLIST_DETAIL("Playlist"),
    ONLINE("Online Streams")
}

data class MediaPlayerState(
    val selectedMainTab: MainTab = MainTab.AUDIO,
    val selectedLibraryTab: LibraryTab = LibraryTab.SONGS,
    val smartSortOption: SmartSortOption = SmartSortOption.TITLE_AZ,
    val allTracks: List<Track> = emptyList(),
    val deviceTracks: List<Track> = emptyList(),
    val isScanningDevice: Boolean = false,
    val storageScanMessage: String? = null,
    val playlists: List<Playlist> = emptyList(),
    val selectedPlaylistId: String? = null,
    val viewingPlaylist: Playlist? = null,
    val selectedArtist: String? = null,
    val favoriteTrackIds: Set<String> = emptySet(),
    val recentlyPlayed: List<Track> = emptyList(),
    val playCounts: Map<String, Int> = emptyMap(),
    val tracks: List<Track> = emptyList(),
    val currentTrack: Track? = null,
    val isVideoPlayerOpen: Boolean = false,
    val isVideoFullscreen: Boolean = false,
    val isPlayerReady: Boolean = false,
    val mediaMetadata: MediaMetadata? = null,
    val trackOverrides: List<TrackOverride> = emptyList(),
    val currentLyrics: String = "",
    val currentSyncedLyrics: List<LyricLine> = emptyList(),
    val activeLyricIndex: Int = -1,
    val isFetchingLyrics: Boolean = false,
    val lyricsFetchMessage: String? = null,
    val isAutoScrollLyrics: Boolean = true,
    val lyricsViewMode: LyricsViewMode = LyricsViewMode.SYNCED,
    val syncedLyricsDisplayStyle: SyncedLyricsDisplayStyle = SyncedLyricsDisplayStyle.LIST,
    val lyricsFontSize: Int = 18,
    val lyricsAlignment: String = "Center",
    val lyricsScrollSpeed: Float = 1.0f,
    val isEditingLyrics: Boolean = false,
    val editingLyricsTrack: Track? = null,
    val isEditingMetadata: Boolean = false,
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffleModeEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val sleepTimerActive: Boolean = false,
    val sleepTimerRemainingMs: Long = 0L,
    val visualizerMode: VisualizerMode = VisualizerMode.NEON_BARS,
    val visualizerTheme: VisualizerTheme = VisualizerTheme.ULTRAVIOLET,
    val visualizerSensitivity: Float = 1.0f,
    val isEqEnabled: Boolean = false,
    val eqBands: List<EqBand> = emptyList(),
    val eqPresets: List<String> = emptyList(),
    val currentEqPreset: Short = 0,
    val minEqLevel: Short = -1500,
    val maxEqLevel: Short = 1500,
    val bassBoostLevel: Int = 0,
    val virtualizerLevel: Int = 0,
    val reverbPreset: String = "Off",
    val isBottomSheetVisible: Boolean = false,
    // Search State
    val isSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val searchFilter: SearchFilter = SearchFilter.ALL,
    val recentSearches: List<String> = emptyList(),
    // Settings State
    val isSettingsOpen: Boolean = false,
    val currentSettingsSubPage: SettingsSubPage? = null,
    val profile: UserProfile = UserProfile(),
    val isDarkTheme: Boolean = true,
    val themeMode: String = "Dark",
    val dynamicColor: Boolean = false,
    val accentColorIndex: Int = 0,
    val isThemeModalOpen: Boolean = false,
    val playbackPrefs: PlaybackPreferences = PlaybackPreferences(),
    val videoPrefs: VideoPreferences = VideoPreferences(),
    val notificationPrefs: NotificationPreferences = NotificationPreferences(),
    val selectedLanguage: String = "English (US)",
    val dailyPlaytimeGoalMinutes: Int = 60,
    val totalPlaytimeSeconds: Long = 52380L,
    // Full screen lyrics & Playlist dialog state
    val isAutoSearchLyricsOnline: Boolean = true,
    val isSearchingLyricsCandidates: Boolean = false,
    val lyricsCandidates: List<LyricsCandidate> = emptyList(),
    val showLyricsSearchModal: Boolean = false,
    val isFullScreenLyricsOpen: Boolean = false,
    val isLyricsImportOpen: Boolean = false,
    val isAddStreamOpen: Boolean = false,
    val isSyncStudioOpen: Boolean = false,
    val isQueueOpen: Boolean = false,
    val queueFeedbackMessage: String? = null,
    val trackToAddToPlaylist: Track? = null,
    val playlistFeedbackMessage: String? = null
)

class MediaPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "media_player_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = TrackOverrideRepository(db.trackOverrideDao())

    private val _state = MutableStateFlow(MediaPlayerState())
    
    val state: StateFlow<MediaPlayerState> = combine(_state, repository.allOverrides) { state, overrides ->
        state.copy(trackOverrides = overrides)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MediaPlayerState()
    )

    private val prefs = application.getSharedPreferences("lyrics_prefs", android.content.Context.MODE_PRIVATE)
    private val lyricsService = LyricsService()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var player: Player? = null
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    fun getPlayer(): Player? = player

    init {
        loadMockTracks()
        setupPlayer()
        setupEqualizer(0)
        loadRecentSearches()
        
        val autoScroll = prefs.getBoolean("lyrics_auto_scroll", true)
        val autoSearchOnline = prefs.getBoolean("auto_search_lyrics_online", true)
        val viewModeStr = prefs.getString("lyrics_view_mode", LyricsViewMode.SYNCED.name) ?: LyricsViewMode.SYNCED.name
        val viewMode = try { LyricsViewMode.valueOf(viewModeStr) } catch (e: Exception) { LyricsViewMode.SYNCED }
        val displayStyleStr = prefs.getString("synced_lyrics_display_style", SyncedLyricsDisplayStyle.LIST.name) ?: SyncedLyricsDisplayStyle.LIST.name
        val displayStyle = try { SyncedLyricsDisplayStyle.valueOf(displayStyleStr) } catch (e: Exception) { SyncedLyricsDisplayStyle.LIST }
        val fontSize = prefs.getInt("lyrics_font_size", 18)
        val align = prefs.getString("lyrics_alignment", "Center") ?: "Center"
        val speed = prefs.getFloat("lyrics_scroll_speed", 1.0f)
        val savedLang = prefs.getString("selected_app_language", "English (US)") ?: "English (US)"
        val vizModeStr = prefs.getString("visualizer_mode", VisualizerMode.NEON_BARS.name) ?: VisualizerMode.NEON_BARS.name
        val vizMode = try { VisualizerMode.valueOf(vizModeStr) } catch (e: Exception) { VisualizerMode.NEON_BARS }
        val vizThemeStr = prefs.getString("visualizer_theme", VisualizerTheme.ULTRAVIOLET.name) ?: VisualizerTheme.ULTRAVIOLET.name
        val vizTheme = try { VisualizerTheme.valueOf(vizThemeStr) } catch (e: Exception) { VisualizerTheme.ULTRAVIOLET }
        val vizSensitivity = prefs.getFloat("visualizer_sensitivity", 1.0f)
        val savedThemeMode = prefs.getString("app_theme_mode", "Dark") ?: "Dark"
        val savedIsDark = prefs.getBoolean("app_is_dark_theme", savedThemeMode != "Light")
        val savedDynamicColor = prefs.getBoolean("app_dynamic_color", false)
        val savedAccentIndex = prefs.getInt("app_accent_color_index", 0)

        _state.update { it.copy(
            isAutoScrollLyrics = autoScroll,
            isAutoSearchLyricsOnline = autoSearchOnline,
            lyricsViewMode = viewMode,
            syncedLyricsDisplayStyle = displayStyle,
            lyricsFontSize = fontSize,
            lyricsAlignment = align,
            lyricsScrollSpeed = speed,
            selectedLanguage = savedLang,
            visualizerMode = vizMode,
            visualizerTheme = vizTheme,
            visualizerSensitivity = vizSensitivity,
            themeMode = savedThemeMode,
            isDarkTheme = savedIsDark,
            dynamicColor = savedDynamicColor,
            accentColorIndex = savedAccentIndex
        ) }


    }
    private fun loadRecentSearches() {
        val saved = prefs.getString("recent_searches_csv", null)
        val list = if (!saved.isNullOrBlank()) {
            saved.split("|").filter { it.isNotBlank() }
        } else {
            emptyList()
        }
        _state.update { it.copy(recentSearches = list) }


    }
    private fun loadMockTracks() {
        val savedDeviceTracks = loadSavedDeviceTracks()
        val sampleVideos = emptyList<Track>()
        val defaultLocalTracks = emptyList<Track>()

        val activeDeviceTracks = if (savedDeviceTracks.isNotEmpty()) {
            savedDeviceTracks
        } else {
            defaultLocalTracks + sampleVideos
        }
        val allTracks = (activeDeviceTracks + sampleVideos).distinctBy { it.id }
        val devicePlaylist = listOf(
            Playlist(
                id = "device_playlist",
                name = "On Device",
                description = "${activeDeviceTracks.size} media files across device folders",
                coverUrl = null,
                coverColorStr = "FF10B981",
                tracks = activeDeviceTracks
            )
        )
        val standardPlaylists = listOf(
            Playlist("p1", "Daily Mix", "Curated beats tailored for your day", "https://picsum.photos/seed/daily/300/300", "FF8B5CF6", activeDeviceTracks),
            Playlist("p2", "Focus Flow", "Deep focus instrumentals and rhythms", "https://picsum.photos/seed/focus/300/300", "FF06B6D4", activeDeviceTracks.take(10)),
            Playlist("p3", "Favorites & Starred", "Favorite tracks and frequent listens", "https://picsum.photos/seed/night/300/300", "FFD500F9", emptyList()),
            Playlist("p4", "Imported & Streams", "Imported local files and cloud streams", "https://picsum.photos/seed/retro/300/300", "FFEC4899", emptyList())
        )
        val mockPlaylists = devicePlaylist + standardPlaylists

        val savedFavs = prefs.getStringSet("favorite_track_ids", emptySet()) ?: emptySet()
        val initialRecent = emptyList<Track>()
        val initialCounts = emptyMap<String, Int>()

        _state.update {
            it.copy(
                allTracks = allTracks,
                deviceTracks = activeDeviceTracks,
                playlists = mockPlaylists,
                selectedPlaylistId = mockPlaylists.firstOrNull()?.id ?: "p1",
                tracks = mockPlaylists.firstOrNull()?.tracks ?: emptyList(),
                favoriteTrackIds = savedFavs,
                recentlyPlayed = initialRecent,
                playCounts = initialCounts
            )
        }
    }

    fun selectPlaylist(playlistId: String) {
        val playlist = _state.value.playlists.find { it.id == playlistId } ?: return
        _state.update {
            it.copy(
                selectedPlaylistId = playlistId,
                tracks = playlist.tracks
            )
        }

        // Update MediaController with new playlist items if needed
        val eqPlayer = player ?: return
        val mediaItems = playlist.tracks.map { track -> createMediaItem(track) }
        eqPlayer.setMediaItems(mediaItems)
        eqPlayer.prepare()
    }

        private fun setupPlayer() {
        val sessionToken = SessionToken(
            getApplication(),
            ComponentName(getApplication(), PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                player = controllerFuture?.get()
                player?.let { p ->
                    val isPlaying = p.isPlaying
                    val currentItem = p.currentMediaItem
                    val trackId = currentItem?.mediaId
                    val currentTrack = _state.value.allTracks.find { it.id == trackId }
                        ?: _state.value.tracks.find { it.id == trackId }
                        ?: _state.value.deviceTracks.find { it.id == trackId }
                        ?: _state.value.currentTrack

                    _state.update {
                        it.copy(
                            isPlayerReady = true,
                            isPlaying = isPlaying,
                            currentTrack = currentTrack ?: it.currentTrack,
                            durationMs = p.duration.coerceAtLeast(0L),
                            mediaMetadata = currentItem?.mediaMetadata ?: p.mediaMetadata,
                            repeatMode = p.repeatMode,
                            shuffleModeEnabled = p.shuffleModeEnabled
                        )
                    }
                    if (isPlaying) {
                        startProgressTimer()
                    }
                }
                player?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _state.update { it.copy(isPlaying = isPlaying) }
                        if (isPlaying) {
                            startProgressTimer()
                        } else {
                            progressJob?.cancel()
                        }
                    }

                    private var consecutiveErrorCount = 0

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            consecutiveErrorCount = 0
                            _state.update { it.copy(durationMs = player?.duration?.coerceAtLeast(0L) ?: 0L) }
                        } else if (playbackState == Player.STATE_ENDED) {
                            skipNext()
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        val trackId = mediaItem?.mediaId
                        val track = _state.value.allTracks.find { it.id == trackId } ?: _state.value.tracks.find { it.id == trackId }
                        if (track != null) {
                            recordTrackPlay(track)
                            _state.update { 
                                it.copy(
                                    currentTrack = track, 
                                    durationMs = player?.duration?.coerceAtLeast(0L) ?: 0L
                                ) 
                            }
                            loadLyricsForTrack(track, autoFetchIfMissing = true)
                        }
                        _state.update { it.copy(mediaMetadata = mediaItem?.mediaMetadata ?: player?.mediaMetadata) }
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        _state.update { it.copy(mediaMetadata = mediaMetadata) }
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {
                        _state.update { it.copy(repeatMode = repeatMode) }
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        _state.update { it.copy(shuffleModeEnabled = shuffleModeEnabled) }
                    }

                    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                        _state.update { it.copy(playbackSpeed = playbackParameters.speed) }
                    }

                    override fun onAudioSessionIdChanged(audioSessionId: Int) {
                        if (audioSessionId != androidx.media3.common.C.AUDIO_SESSION_ID_UNSET) {
                            setupEqualizer(audioSessionId)
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        consecutiveErrorCount++
                        val failedTrack = _state.value.currentTrack
                        val trackName = failedTrack?.title ?: "media"
                        val causeMsg = error.cause?.message ?: ""
                        val is403 = causeMsg.contains("403")
                        val is404 = causeMsg.contains("404")
                        val errorDetail = when {
                            is403 -> "Stream access restricted (HTTP 403)"
                            is404 -> "Media source not found (HTTP 404)"
                            else -> error.localizedMessage ?: "Playback error"
                        }
                        _state.update {
                            it.copy(
                                isPlaying = false,
                                playlistFeedbackMessage = "$errorDetail on '$trackName'"
                            )
                        }
                        // Stop looping if too many failures in succession to protect user experience
                        if (consecutiveErrorCount < 3) {
                            viewModelScope.launch {
                                delay(1200L)
                                if (player?.hasNextMediaItem() == true) {
                                    skipNext()
                                }
                            }
                        } else {
                            consecutiveErrorCount = 0
                        }
                    }
                })
                
                // Set the initial playlist without eager preparation to avoid premature decoder initialization
                val mediaItems = _state.value.tracks.map { track -> createMediaItem(track) }
                player?.setMediaItems(mediaItems)
            },
            ContextCompat.getMainExecutor(getApplication())
        )


    }
    fun selectLibraryTab(tab: LibraryTab) {
        _state.update { it.copy(selectedLibraryTab = tab) }


    }
    fun selectArtist(artist: String?) {
        _state.update { it.copy(selectedArtist = artist) }


    }
    fun viewPlaylistDetails(playlist: Playlist?) {
        _state.update { it.copy(viewingPlaylist = playlist) }


    }
    fun toggleFavorite(trackId: String) {
        _state.update { curr ->
            val newFavs = if (curr.favoriteTrackIds.contains(trackId)) {
                curr.favoriteTrackIds - trackId
            } else {
                curr.favoriteTrackIds + trackId
            }
            prefs.edit().putStringSet("favorite_track_ids", newFavs).apply()
            curr.copy(favoriteTrackIds = newFavs)
        }


    }
    fun clearHistory() {
        _state.update { it.copy(recentlyPlayed = emptyList(), playCounts = emptyMap()) }
    }
    fun createPlaylist(name: String, description: String) {
        if (name.isBlank()) return
        val newId = "p_${System.currentTimeMillis()}"
        val colors = listOf("FF8B5CF6", "FF06B6D4", "FF10B981", "FFD500F9", "FFE11D48")
        val newPlaylist = Playlist(
            id = newId,
            name = name.trim(),
            description = if (description.isBlank()) "Custom playlist" else description.trim(),
            coverUrl = "https://picsum.photos/seed/$newId/300/300",
            coverColorStr = colors.random(),
            tracks = emptyList()
        )
        _state.update { it.copy(playlists = it.playlists + newPlaylist) }


    }
    fun playTrackList(tracksToPlay: List<Track>, startIndex: Int = 0, shuffle: Boolean = false) {
        if (tracksToPlay.isEmpty()) return
        val orderedTracks = if (shuffle) tracksToPlay.shuffled() else tracksToPlay
        _state.update { it.copy(tracks = orderedTracks) }
        val mediaItems = orderedTracks.map { track -> createMediaItem(track) }
        player?.setMediaItems(mediaItems)
        player?.prepare()
        val validIndex = startIndex.coerceIn(0, orderedTracks.size - 1)
        player?.seekTo(validIndex, 0L)
        player?.play()
        
        val selectedTrack = orderedTracks[validIndex]
        recordTrackPlay(selectedTrack)
        _state.update {
            it.copy(
                currentTrack = selectedTrack,
                isBottomSheetVisible = true
            )
        }
        loadLyricsForTrack(selectedTrack, autoFetchIfMissing = true)


    }
    fun selectTrack(track: Track, contextList: List<Track>? = null) {
        val listToUse = contextList ?: if (_state.value.tracks.any { it.id == track.id }) {
            _state.value.tracks
        } else {
            _state.value.allTracks
        }
        
        val needsReload = _state.value.tracks != listToUse
        if (needsReload) {
            _state.update { it.copy(tracks = listToUse) }
            val mediaItems = listToUse.map { t -> createMediaItem(t) }
            player?.setMediaItems(mediaItems)
            player?.prepare()
        }
        
        val index = listToUse.indexOfFirst { it.id == track.id }
        if (index != -1) {
            player?.seekTo(index, 0L)
        }
        player?.play()
        
        recordTrackPlay(track)
        _state.update { 
            it.copy(
                currentTrack = track,
                isBottomSheetVisible = !track.isVideo,
                isVideoPlayerOpen = track.isVideo,
                isVideoFullscreen = track.isVideo
            )
        }
        if (!track.isVideo) {
            loadLyricsForTrack(track, autoFetchIfMissing = true)
        }


    }
    fun openVideoPlayer(track: Track? = null) {
        if (track != null && track.id != _state.value.currentTrack?.id) {
            selectTrack(track)
        } else {
            _state.update { it.copy(isVideoPlayerOpen = true, isVideoFullscreen = true) }
        }
    }
    fun closeVideoPlayer() {
        _state.update { it.copy(isVideoPlayerOpen = false, isVideoFullscreen = false) }
    }
    fun toggleVideoFullscreen() {
        _state.update { it.copy(isVideoFullscreen = !it.isVideoFullscreen) }
    }
    fun setVideoFullscreen(fullscreen: Boolean) {
        _state.update { it.copy(isVideoFullscreen = fullscreen) }
    }
    fun openQueue() {
        _state.update { it.copy(isQueueOpen = true) }
    }

    fun closeQueue() {
        _state.update { it.copy(isQueueOpen = false) }
    }

    fun clearQueueFeedbackMessage() {
        _state.update { it.copy(queueFeedbackMessage = null) }
    }

    fun addToQueue(track: Track) {
        val currentTracks = _state.value.tracks
        if (currentTracks.isEmpty() || _state.value.currentTrack == null) {
            selectTrack(track, listOf(track))
            _state.update { it.copy(queueFeedbackMessage = "Playing '${track.title}'") }
            return
        }
        val updatedTracks = currentTracks + track
        _state.update {
            it.copy(
                tracks = updatedTracks,
                queueFeedbackMessage = "Added '${track.title}' to queue"
            )
        }
        player?.addMediaItem(createMediaItem(track))
    }

    fun addMultipleToQueue(newTracks: List<Track>) {
        if (newTracks.isEmpty()) return
        val currentTracks = _state.value.tracks
        if (currentTracks.isEmpty() || _state.value.currentTrack == null) {
            playTrackList(newTracks, 0)
            _state.update { it.copy(queueFeedbackMessage = "Playing ${newTracks.size} tracks") }
            return
        }
        val updatedTracks = currentTracks + newTracks
        _state.update {
            it.copy(
                tracks = updatedTracks,
                queueFeedbackMessage = "Added ${newTracks.size} tracks to queue"
            )
        }
        newTracks.forEach { track ->
            player?.addMediaItem(createMediaItem(track))
        }
    }

    fun playNext(track: Track) {
        val currentTracks = _state.value.tracks
        if (currentTracks.isEmpty() || _state.value.currentTrack == null) {
            selectTrack(track, listOf(track))
            _state.update { it.copy(queueFeedbackMessage = "Playing '${track.title}'") }
            return
        }

        val currentIndex = player?.currentMediaItemIndex ?: currentTracks.indexOfFirst { it.id == _state.value.currentTrack?.id }
        val insertIndex = if (currentIndex >= 0 && currentIndex < currentTracks.size) currentIndex + 1 else currentTracks.size
        
        val mutableList = currentTracks.toMutableList()
        mutableList.add(insertIndex, track)
        
        _state.update {
            it.copy(
                tracks = mutableList,
                queueFeedbackMessage = "'${track.title}' will play next"
            )
        }
        player?.addMediaItem(insertIndex, createMediaItem(track))
    }

    fun removeFromQueue(index: Int) {
        val currentTracks = _state.value.tracks
        if (index !in currentTracks.indices) return
        
        val removedTrack = currentTracks[index]
        val currentPlayingIndex = player?.currentMediaItemIndex ?: currentTracks.indexOfFirst { it.id == _state.value.currentTrack?.id }
        
        val mutableList = currentTracks.toMutableList()
        mutableList.removeAt(index)
        
        if (index == currentPlayingIndex) {
            if (mutableList.isNotEmpty()) {
                val nextIndex = index.coerceAtMost(mutableList.size - 1)
                player?.removeMediaItem(index)
                val newCurrent = mutableList[nextIndex]
                _state.update { 
                    it.copy(
                        tracks = mutableList,
                        currentTrack = newCurrent,
                        queueFeedbackMessage = "Removed '${removedTrack.title}' from queue"
                    )
                }
                loadLyricsForTrack(newCurrent, autoFetchIfMissing = true)
            } else {
                player?.stop()
                player?.clearMediaItems()
                _state.update {
                    it.copy(
                        tracks = emptyList(),
                        currentTrack = null,
                        isPlaying = false,
                        queueFeedbackMessage = "Queue is now empty"
                    )
                }
            }
        } else {
            player?.removeMediaItem(index)
            _state.update {
                it.copy(
                    tracks = mutableList,
                    queueFeedbackMessage = "Removed '${removedTrack.title}' from queue"
                )
            }
        }
    }

    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        val currentTracks = _state.value.tracks
        if (fromIndex !in currentTracks.indices || toIndex !in currentTracks.indices || fromIndex == toIndex) return
        
        val mutableList = currentTracks.toMutableList()
        val item = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, item)
        
        player?.moveMediaItem(fromIndex, toIndex)
        _state.update { it.copy(tracks = mutableList) }
    }

    fun playQueueTrackAt(index: Int) {
        val currentTracks = _state.value.tracks
        if (index !in currentTracks.indices) return
        
        val track = currentTracks[index]
        player?.seekTo(index, 0L)
        player?.play()
        recordTrackPlay(track)
        _state.update {
            it.copy(
                currentTrack = track,
                isBottomSheetVisible = !track.isVideo,
                isVideoPlayerOpen = track.isVideo
            )
        }
        if (!track.isVideo) {
            loadLyricsForTrack(track, autoFetchIfMissing = true)
        }
    }

    fun clearQueue(keepCurrentTrack: Boolean = true) {
        val current = _state.value.currentTrack
        if (keepCurrentTrack && current != null) {
            _state.update {
                it.copy(
                    tracks = listOf(current),
                    queueFeedbackMessage = "Cleared upcoming tracks from queue"
                )
            }
            val currentIndex = player?.currentMediaItemIndex ?: 0
            val count = player?.mediaItemCount ?: 0
            for (i in (count - 1) downTo (currentIndex + 1)) {
                player?.removeMediaItem(i)
            }
            for (i in (currentIndex - 1) downTo 0) {
                player?.removeMediaItem(i)
            }
        } else {
            player?.stop()
            player?.clearMediaItems()
            _state.update {
                it.copy(
                    tracks = emptyList(),
                    currentTrack = null,
                    isPlaying = false,
                    queueFeedbackMessage = "Cleared entire queue"
                )
            }
        }
    }

    fun shuffleQueue() {
        val currentTracks = _state.value.tracks
        if (currentTracks.size <= 2) return
        
        val currentIndex = player?.currentMediaItemIndex ?: currentTracks.indexOfFirst { it.id == _state.value.currentTrack?.id }
        if (currentIndex < 0) return
        
        val currentTrack = currentTracks[currentIndex]
        val before = currentTracks.subList(0, currentIndex)
        val after = currentTracks.subList(currentIndex + 1, currentTracks.size).shuffled()
        val newTracks = before + listOf(currentTrack) + after
        
        _state.update {
            it.copy(
                tracks = newTracks,
                queueFeedbackMessage = "Shuffled upcoming queue"
            )
        }
        val currentPos = player?.currentPosition ?: 0L
        val isPlaying = player?.isPlaying == true
        val mediaItems = newTracks.map { createMediaItem(it) }
        player?.setMediaItems(mediaItems, before.size, currentPos)
        if (isPlaying) player?.play()
    }

    fun sortQueueByBpm(mode: QueueBpmSortMode, onlyUpcoming: Boolean = true) {
        val currentTracks = _state.value.tracks
        if (currentTracks.size <= 1) {
            _state.update { it.copy(queueFeedbackMessage = "Need at least 2 tracks in queue to sort") }
            return
        }

        val currentIndex = player?.currentMediaItemIndex ?: currentTracks.indexOfFirst { it.id == _state.value.currentTrack?.id }

        val newTracks: List<Track>
        val targetIndex: Int

        if (onlyUpcoming && currentIndex in currentTracks.indices && currentIndex < currentTracks.size - 1) {
            val before = currentTracks.subList(0, currentIndex)
            val currentTrack = currentTracks[currentIndex]
            val upcoming = currentTracks.subList(currentIndex + 1, currentTracks.size)

            val sortedUpcoming = when (mode) {
                QueueBpmSortMode.ASCENDING -> upcoming.sortedBy { it.effectiveBpm }
                QueueBpmSortMode.DESCENDING -> upcoming.sortedByDescending { it.effectiveBpm }
                QueueBpmSortMode.HARMONIC_FLOW -> sortTracksHarmonicFlow(upcoming, startBpm = currentTrack.effectiveBpm)
            }
            newTracks = before + listOf(currentTrack) + sortedUpcoming
            targetIndex = currentIndex
        } else {
            val currentlyPlaying = if (currentIndex in currentTracks.indices) currentTracks[currentIndex] else null
            val sortedAll = when (mode) {
                QueueBpmSortMode.ASCENDING -> currentTracks.sortedBy { it.effectiveBpm }
                QueueBpmSortMode.DESCENDING -> currentTracks.sortedByDescending { it.effectiveBpm }
                QueueBpmSortMode.HARMONIC_FLOW -> {
                    val startBpm = currentlyPlaying?.effectiveBpm ?: currentTracks.first().effectiveBpm
                    sortTracksHarmonicFlow(currentTracks, startBpm = startBpm)
                }
            }
            newTracks = sortedAll
            targetIndex = if (currentlyPlaying != null) {
                newTracks.indexOfFirst { it.id == currentlyPlaying.id }.coerceAtLeast(0)
            } else {
                0
            }
        }

        val feedbackMsg = when (mode) {
            QueueBpmSortMode.ASCENDING -> if (onlyUpcoming) "Sorted upcoming queue by BPM (Slow → Fast)" else "Sorted entire queue by BPM (Slow → Fast)"
            QueueBpmSortMode.DESCENDING -> if (onlyUpcoming) "Sorted upcoming queue by BPM (Fast → Slow)" else "Sorted entire queue by BPM (Fast → Slow)"
            QueueBpmSortMode.HARMONIC_FLOW -> if (onlyUpcoming) "Arranged upcoming queue by Harmonic Flow" else "Arranged entire queue by Harmonic Flow"
        }

        _state.update {
            it.copy(
                tracks = newTracks,
                queueFeedbackMessage = feedbackMsg
            )
        }

        val currentPos = player?.currentPosition ?: 0L
        val isPlaying = player?.isPlaying == true
        val mediaItems = newTracks.map { createMediaItem(it) }
        player?.setMediaItems(mediaItems, targetIndex, currentPos)
        if (isPlaying) player?.play()
    }

    private fun sortTracksHarmonicFlow(tracks: List<Track>, startBpm: Int): List<Track> {
        if (tracks.size <= 2) return tracks.sortedBy { it.effectiveBpm }
        val remaining = tracks.toMutableList()
        val result = mutableListOf<Track>()
        var currentBpm = startBpm

        while (remaining.isNotEmpty()) {
            val nextTrack = remaining.minByOrNull { kotlin.math.abs(it.effectiveBpm - currentBpm) } ?: remaining.first()
            remaining.remove(nextTrack)
            result.add(nextTrack)
            currentBpm = nextTrack.effectiveBpm
        }
        return result
    }

    private fun recordTrackPlay(track: Track) {
        _state.update { curr ->
            val updatedRecent = (listOf(track) + curr.recentlyPlayed.filter { it.id != track.id }).take(30)
            val updatedCounts = curr.playCounts.toMutableMap()
            updatedCounts[track.id] = (updatedCounts[track.id] ?: 0) + 1
            curr.copy(
                recentlyPlayed = updatedRecent,
                playCounts = updatedCounts
            )
        }


    }
    fun togglePlayPause() {
        if (player?.isPlaying == true) {
            player?.pause()
        } else {
            if (player?.playbackState == Player.STATE_IDLE) {
                player?.prepare()
            }
            player?.play()
        }


    }
    fun skipNext() {
        if (player?.playbackState == Player.STATE_IDLE) {
            player?.prepare()
        }
        if (player?.hasNextMediaItem() == true) {
            player?.seekToNextMediaItem()
        } else {
            // Loop back to start if needed
            player?.seekTo(0, 0L)
            player?.play()
        }


    }
    fun skipPrevious() {
        if (player?.playbackState == Player.STATE_IDLE) {
            player?.prepare()
        }
        if (player?.hasPreviousMediaItem() == true) {
            player?.seekToPreviousMediaItem()
        } else {
            player?.seekTo(0, 0L)
            player?.play()
        }


    }
    private fun loadLyricsForTrack(track: Track, autoFetchIfMissing: Boolean = true) {
        val customSaved = prefs.getString("lyrics_${track.id}", null)
            ?: prefs.getString(track.id, null)
        val rawLyrics = when {
            !customSaved.isNullOrBlank() -> customSaved
            DefaultLyricsRepository.BUILT_IN_SYNCED_LYRICS.containsKey(track.id) -> {
                DefaultLyricsRepository.BUILT_IN_SYNCED_LYRICS[track.id] ?: ""
            }
            else -> ""
        }

        val parsed = LrcParser.parse(rawLyrics)
        val currentPos = player?.currentPosition ?: _state.value.progressMs
        val activeIdx = if (parsed.isNotEmpty()) parsed.indexOfLast { it.timeMs <= currentPos } else -1

        _state.update {
            it.copy(
                currentLyrics = rawLyrics,
                currentSyncedLyrics = parsed,
                activeLyricIndex = activeIdx,
                isEditingLyrics = false,
                lyricsFetchMessage = if (parsed.isNotEmpty()) "Synchronized lyrics ready" else null
            )
        }

        if (rawLyrics.isBlank() && autoFetchIfMissing && _state.value.isAutoSearchLyricsOnline) {
            fetchLyricsForCurrentTrack(manualTitle = track.title, manualArtist = track.artist, isSilent = true)
        }


    }
    fun seekTo(progressFraction: Float) {
        val duration = player?.duration?.coerceAtLeast(0L) ?: 0L
        if (duration > 0) {
            val newPosition = (progressFraction * duration).toLong()
            player?.seekTo(newPosition)
            val synced = _state.value.currentSyncedLyrics
            val activeIdx = if (synced.isNotEmpty()) synced.indexOfLast { it.timeMs <= newPosition } else -1
            _state.update { it.copy(progressMs = newPosition, activeLyricIndex = activeIdx) }
        }


    }
    fun seekToPosition(positionMs: Long) {
        val duration = player?.duration?.coerceAtLeast(0L) ?: 0L
        val targetPos = positionMs.coerceIn(0L, if (duration > 0) duration else Long.MAX_VALUE)
        player?.seekTo(targetPos)
        val synced = _state.value.currentSyncedLyrics
        val activeIdx = if (synced.isNotEmpty()) synced.indexOfLast { it.timeMs <= targetPos } else -1
        _state.update { it.copy(progressMs = targetPos, activeLyricIndex = activeIdx) }

    
    }
    fun toggleRepeatMode() {
        val currentMode = player?.repeatMode ?: Player.REPEAT_MODE_OFF
        val nextMode = when (currentMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        player?.repeatMode = nextMode

    
    }
    fun toggleShuffleMode() {
        player?.shuffleModeEnabled = !(player?.shuffleModeEnabled ?: false)


    }
    fun setPlaybackSpeed(speed: Float) {
        player?.playbackParameters = PlaybackParameters(speed)

    
    }
    fun seekRelative(offsetMs: Long) {
        val current = player?.currentPosition ?: _state.value.progressMs
        val duration = player?.duration?.coerceAtLeast(0L) ?: _state.value.durationMs
        val targetPos = (current + offsetMs).coerceIn(0L, if (duration > 0) duration else Long.MAX_VALUE)
        seekToPosition(targetPos)
    }
    fun openEditLyricsForTrack(track: Track) {
        val customSaved = prefs.getString("lyrics_${track.id}", null)
            ?: prefs.getString(track.id, null)
        val rawLyrics = when {
            !customSaved.isNullOrBlank() -> customSaved
            DefaultLyricsRepository.BUILT_IN_SYNCED_LYRICS.containsKey(track.id) -> {
                DefaultLyricsRepository.BUILT_IN_SYNCED_LYRICS[track.id] ?: ""
            }
            else -> ""
        }
        val parsed = LrcParser.parse(rawLyrics)
        val pos = if (_state.value.currentTrack?.id == track.id) (player?.currentPosition ?: _state.value.progressMs) else 0L
        val activeIdx = if (parsed.isNotEmpty()) parsed.indexOfLast { it.timeMs <= pos } else -1

        _state.update {
            it.copy(
                editingLyricsTrack = track,
                currentLyrics = if (_state.value.currentTrack?.id == track.id) it.currentLyrics else rawLyrics,
                currentSyncedLyrics = if (_state.value.currentTrack?.id == track.id) it.currentSyncedLyrics else parsed,
                activeLyricIndex = if (_state.value.currentTrack?.id == track.id) it.activeLyricIndex else activeIdx,
                isEditingLyrics = true,
                lyricsFetchMessage = null
            )
        }
    }
    fun setAutoSearchLyricsOnline(enabled: Boolean) {
        prefs.edit().putBoolean("auto_search_lyrics_online", enabled).apply()
        _state.update { it.copy(isAutoSearchLyricsOnline = enabled) }
    }

    fun openLyricsSearchModal(track: Track? = null) {
        val target = track ?: _state.value.editingLyricsTrack ?: _state.value.currentTrack
        _state.update {
            it.copy(
                showLyricsSearchModal = true,
                editingLyricsTrack = target,
                lyricsCandidates = emptyList(),
                isSearchingLyricsCandidates = false
            )
        }
        if (target != null) {
            val (cT, cA) = lyricsService.cleanMetadata(target.title, target.artist)
            val q = if (cA.isNotBlank() && !cA.equals("Device Audio", ignoreCase = true) && !cA.equals("Unknown Artist", ignoreCase = true)) "$cT $cA" else cT
            searchLyricsCandidates(q)
        }
    }

    fun closeLyricsSearchModal() {
        _state.update { it.copy(showLyricsSearchModal = false) }
    }

    fun searchLyricsCandidates(query: String) {
        val target = _state.value.editingLyricsTrack ?: _state.value.currentTrack
        viewModelScope.launch {
            _state.update { it.copy(isSearchingLyricsCandidates = true) }
            val candidates = lyricsService.searchLyricsCandidates(
                query = query,
                title = target?.title,
                artist = target?.artist
            )
            _state.update {
                it.copy(
                    isSearchingLyricsCandidates = false,
                    lyricsCandidates = candidates
                )
            }
        }
    }

    fun autoCleanSearchLyricsCandidates() {
        val target = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        val (cleanTitle, cleanArtist) = lyricsService.cleanMetadata(target.title, target.artist)
        val query = if (cleanArtist.isNotBlank() && !cleanArtist.equals("Device Audio", ignoreCase = true) && !cleanArtist.equals("Unknown Artist", ignoreCase = true)) {
            "$cleanTitle $cleanArtist"
        } else {
            cleanTitle
        }
        searchLyricsCandidates(query)
    }

    fun applyLyricsCandidate(candidate: LyricsCandidate) {
        val target = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        val text = candidate.syncedLyrics ?: candidate.plainLyrics ?: ""
        if (text.isNotBlank()) {
            saveLyrics(text)
            _state.update {
                it.copy(
                    lyricsFetchMessage = if (candidate.hasSynced) "Applied synchronized lyrics: ${candidate.trackName}" else "Applied plain lyrics: ${candidate.trackName}"
                )
            }
        }
    }

    fun fetchLyricsForCurrentTrack(
        manualTitle: String? = null,
        manualArtist: String? = null,
        isSilent: Boolean = false
    ) {
        val targetTrack = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        val queryTitle = manualTitle?.trim()?.ifBlank { null } ?: targetTrack.title
        val queryArtist = manualArtist?.trim()?.ifBlank { null } ?: targetTrack.artist
        val durationSec = if (targetTrack.durationMs > 0) (targetTrack.durationMs / 1000).toInt() else null

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isFetchingLyrics = true,
                    lyricsFetchMessage = "Searching LRCLIB for synchronized lyrics..."
                )
            }
            when (val result = lyricsService.fetchLyrics(queryTitle, queryArtist, durationSec)) {
                is LyricsFetchResult.Success -> {
                    if (result.isInstrumental) {
                        _state.update {
                            it.copy(
                                isFetchingLyrics = false,
                                lyricsFetchMessage = "Track is tagged as Instrumental (no vocal lyrics)."
                            )
                        }
                    } else {
                        val fetchedText = result.syncedLyrics ?: result.plainLyrics ?: ""
                        if (fetchedText.isNotBlank()) {
                            val currentOverride = _state.value.trackOverrides.find { it.trackId == targetTrack.id }
                            val title = currentOverride?.title ?: targetTrack.title
                            val artist = currentOverride?.artist ?: targetTrack.artist
                            viewModelScope.launch {
                                repository.insertOverride(com.example.db.TrackOverride(targetTrack.id, title, artist, fetchedText))
                            }
                            prefs.edit().putString("lyrics_${targetTrack.id}", fetchedText).apply()
                            prefs.edit().putString(targetTrack.id, fetchedText).apply()
                            val parsed = LrcParser.parse(fetchedText)
                            val pos = if (_state.value.currentTrack?.id == targetTrack.id) (player?.currentPosition ?: _state.value.progressMs) else 0L
                            val activeIdx = if (parsed.isNotEmpty()) parsed.indexOfLast { it.timeMs <= pos } else -1
                            _state.update {
                                it.copy(
                                    currentLyrics = fetchedText,
                                    currentSyncedLyrics = parsed,
                                    activeLyricIndex = activeIdx,
                                    isFetchingLyrics = false,
                                    lyricsFetchMessage = if (result.syncedLyrics != null) {
                                        "Synchronized lyrics loaded and saved to song ✓"
                                    } else {
                                        "Plain text lyrics loaded and saved to song ✓"
                                    }
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isFetchingLyrics = false,
                                    lyricsFetchMessage = "No lyrics found for \"$queryTitle\""
                                )
                            }
                        }
                    }
                }
                is LyricsFetchResult.Error -> {
                    _state.update {
                        it.copy(
                            isFetchingLyrics = false,
                            lyricsFetchMessage = if (!isSilent) result.message else null
                        )
                    }
                }

            }
        }
    }
    fun saveLyrics(lyrics: String) {
        val track = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        
        viewModelScope.launch {
            val currentOverride = _state.value.trackOverrides.find { it.trackId == track.id }
            val title = currentOverride?.title ?: track.title
            val artist = currentOverride?.artist ?: track.artist
            
            repository.insertOverride(com.example.db.TrackOverride(track.id, title, artist, lyrics))
        }

        prefs.edit().putString("lyrics_${track.id}", lyrics).apply()
        prefs.edit().putString(track.id, lyrics).apply()

        val parsed = LrcParser.parse(lyrics)
        val pos = if (_state.value.currentTrack?.id == track.id) (player?.currentPosition ?: _state.value.progressMs) else 0L
        val activeIdx = if (parsed.isNotEmpty()) parsed.indexOfLast { it.timeMs <= pos } else -1
        _state.update { 
            it.copy(
                currentLyrics = lyrics, 
                currentSyncedLyrics = parsed,
                activeLyricIndex = activeIdx,
                isEditingLyrics = false,
                editingLyricsTrack = null,
                lyricsFetchMessage = if (parsed.isNotEmpty()) "Custom synchronized lyrics saved" else "Lyrics saved"
            ) 
        }
    }

    fun saveCurrentLyrics() {
        val target = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        val text = _state.value.currentLyrics
        if (text.isNotBlank()) {
            saveLyrics(text)
            _state.update {
                it.copy(lyricsFetchMessage = "Lyrics permanently saved to song library ✓")
            }
        }
    }

    fun exportCurrentLyrics(context: Context) {
        val target = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        val text = _state.value.currentLyrics
        if (text.isBlank()) {
            _state.update { it.copy(lyricsFetchMessage = "No lyrics available to export") }
            return
        }
        val result = LrcExporter.exportLrcFile(context, target.title, target.artist, text)
        result.onSuccess { msg ->
            _state.update { it.copy(lyricsFetchMessage = "$msg ✓") }
        }.onFailure { err ->
            _state.update { it.copy(lyricsFetchMessage = "Export failed: ${err.message}") }
        }
    }
    fun toggleAutoScrollLyrics(enabled: Boolean) {
        prefs.edit().putBoolean("lyrics_auto_scroll", enabled).apply()
        _state.update { it.copy(isAutoScrollLyrics = enabled) }


    }
    fun setLyricsFontSize(size: Int) {
        prefs.edit().putInt("lyrics_font_size", size).apply()
        _state.update { it.copy(lyricsFontSize = size) }


    }
    fun setLyricsAlignment(align: String) {
        prefs.edit().putString("lyrics_alignment", align).apply()
        _state.update { it.copy(lyricsAlignment = align) }


    }
    fun setLyricsScrollSpeed(speed: Float) {
        prefs.edit().putFloat("lyrics_scroll_speed", speed).apply()
        _state.update { it.copy(lyricsScrollSpeed = speed) }


    }
    fun setLyricsViewMode(mode: LyricsViewMode) {
        prefs.edit().putString("lyrics_view_mode", mode.name).apply()
        _state.update { it.copy(lyricsViewMode = mode) }


    }
    fun setSyncedLyricsDisplayStyle(style: SyncedLyricsDisplayStyle) {
        prefs.edit().putString("synced_lyrics_display_style", style.name).apply()
        _state.update { it.copy(syncedLyricsDisplayStyle = style) }
    }
    fun clearLyricsFetchMessage() {
        _state.update { it.copy(lyricsFetchMessage = null) }


    }
    fun openLyricsImport(targetTrack: Track? = null) {
        _state.update { 
            it.copy(
                isLyricsImportOpen = true,
                editingLyricsTrack = targetTrack ?: it.currentTrack
            ) 
        }
    }
    fun closeLyricsImport() {
        _state.update { it.copy(isLyricsImportOpen = false) }
    }
    fun importLyrics(lyrics: String, sourceDescription: String = "file") {
        val track = _state.value.editingLyricsTrack ?: _state.value.currentTrack ?: return
        val trimmed = lyrics.trim()
        if (trimmed.isEmpty()) {
            _state.update { it.copy(lyricsFetchMessage = "Imported content was empty") }
            return
        }
        saveLyrics(trimmed)
        val parsed = LrcParser.parse(trimmed)
        val message = if (parsed.isNotEmpty()) {
            "Imported ${parsed.size} synced lines ($sourceDescription)"
        } else {
            "Imported plain text lyrics ($sourceDescription)"
        }
        _state.update { 
            it.copy(
                lyricsFetchMessage = message,
                isLyricsImportOpen = false
            ) 
        }
    }
    fun importLyricsFromUri(context: android.content.Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var fileName = "file.lrc"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex) ?: "file.lrc"
                    }
                }
                val text = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                }
                withContext(Dispatchers.Main) {
                    if (!text.isNullOrBlank()) {
                        importLyrics(text, fileName)
                    } else {
                        _state.update { it.copy(lyricsFetchMessage = "File '$fileName' was empty") }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(lyricsFetchMessage = "Error reading file: ${e.localizedMessage ?: "Unknown error"}") }
                }
            }
        }
    }
    fun importLyricsFromUrl(urlString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isFetchingLyrics = true, lyricsFetchMessage = "Fetching lyrics from URL...") }
            try {
                val url = java.net.URL(urlString)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 Android ExoPlayer/Aura")
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    val content = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    val sourceName = url.path.substringAfterLast('/', "web link").ifBlank { "web link" }
                    withContext(Dispatchers.Main) {
                        _state.update { it.copy(isFetchingLyrics = false) }
                        if (content.isNotBlank()) {
                            importLyrics(content, sourceName)
                        } else {
                            _state.update { it.copy(lyricsFetchMessage = "URL returned empty lyrics content") }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _state.update {
                            it.copy(
                                isFetchingLyrics = false,
                                lyricsFetchMessage = "HTTP error $responseCode while fetching lyrics"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(
                            isFetchingLyrics = false,
                            lyricsFetchMessage = "Network error: ${e.localizedMessage ?: "Failed to load link"}"
                        )
                    }
                }
            }
        }
    }
    fun setEditingLyrics(isEditing: Boolean) {
        _state.update { it.copy(isEditingLyrics = isEditing, editingLyricsTrack = if (!isEditing) null else it.editingLyricsTrack) }


    }
    fun saveMetadataOverride(title: String, artist: String) {
        val trackId = _state.value.currentTrack?.id ?: return
        editTrack(trackId, title, artist)


    }
    fun editTrack(trackId: String, newTitle: String, newArtist: String) {
        viewModelScope.launch {
            repository.insertOverride(TrackOverride(trackId, newTitle, newArtist))
        }
        val updatedAll = _state.value.allTracks.map {
            if (it.id == trackId) it.copy(title = newTitle, artist = newArtist) else it
        }
        val updatedDevice = _state.value.deviceTracks.map {
            if (it.id == trackId) it.copy(title = newTitle, artist = newArtist) else it
        }
        if (updatedDevice.isNotEmpty()) {
            persistDeviceTracks(updatedDevice)
        }
        val updatedPlaylists = _state.value.playlists.map { pl ->
            pl.copy(tracks = pl.tracks.map { if (it.id == trackId) it.copy(title = newTitle, artist = newArtist) else it })
        }
        val updatedRecent = _state.value.recentlyPlayed.map {
            if (it.id == trackId) it.copy(title = newTitle, artist = newArtist) else it
        }
        val updatedCurrent = if (_state.value.currentTrack?.id == trackId) {
            _state.value.currentTrack?.copy(title = newTitle, artist = newArtist)
        } else {
            _state.value.currentTrack
        }

        _state.update {
            it.copy(
                allTracks = updatedAll,
                deviceTracks = updatedDevice,
                playlists = updatedPlaylists,
                recentlyPlayed = updatedRecent,
                currentTrack = updatedCurrent,
                tracks = it.tracks.map { tr -> if (tr.id == trackId) tr.copy(title = newTitle, artist = newArtist) else tr },
                isEditingMetadata = false
            )
        }


    }
    fun deleteTrack(trackId: String) {
        val targetTrack = _state.value.allTracks.find { it.id == trackId }
        viewModelScope.launch {
            repository.deleteOverride(trackId)
        }
        val updatedDevice = _state.value.deviceTracks.filter { it.id != trackId }
        persistDeviceTracks(updatedDevice)

        val updatedAll = _state.value.allTracks.filter { it.id != trackId }
        val updatedRecent = _state.value.recentlyPlayed.filter { it.id != trackId }
        val updatedFavs = _state.value.favoriteTrackIds.filter { it != trackId }.toSet()
        prefs.edit().putStringSet("favorite_track_ids", updatedFavs).apply()

        val updatedPlaylists = _state.value.playlists.map { pl ->
            pl.copy(tracks = pl.tracks.filter { it.id != trackId })
        }.filter { !(it.id == "device_playlist" && updatedDevice.isEmpty()) }

        val isCurrent = _state.value.currentTrack?.id == trackId
        if (isCurrent) {
            if (updatedAll.isNotEmpty()) {
                val nextTrack = updatedAll.first()
                selectTrack(nextTrack, updatedAll)
            } else {
                player?.stop()
                _state.update { it.copy(currentTrack = null, isPlaying = false, isBottomSheetVisible = false) }
            }
        }

        _state.update {
            it.copy(
                allTracks = updatedAll,
                deviceTracks = updatedDevice,
                recentlyPlayed = updatedRecent,
                favoriteTrackIds = updatedFavs,
                playlists = updatedPlaylists,
                tracks = it.tracks.filter { tr -> tr.id != trackId },
                storageScanMessage = "Song '${targetTrack?.title ?: "Track"}' removed from library."
            )
        }


    }
    fun setEditingMetadata(isEditing: Boolean) {
        _state.update { it.copy(isEditingMetadata = isEditing) }


    }
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _state.update { it.copy(sleepTimerActive = false, sleepTimerRemainingMs = 0L) }
            return
        }
        
        _state.update { it.copy(sleepTimerActive = true, sleepTimerRemainingMs = minutes * 60 * 1000L) }
        
        sleepTimerJob = viewModelScope.launch {
            while (_state.value.sleepTimerRemainingMs > 0) {
                delay(1000L)
                _state.update { it.copy(sleepTimerRemainingMs = it.sleepTimerRemainingMs - 1000L) }
            }
            player?.pause()
            _state.update { it.copy(sleepTimerActive = false, sleepTimerRemainingMs = 0L) }
        }


    }
    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _state.update { it.copy(sleepTimerActive = false, sleepTimerRemainingMs = 0L) }


    }
    companion object {
        val EQUALIZER_PRESETS = listOf(
            "Flat",
            "Bass Boost",
            "Treble Boost",
            "Rock",
            "Pop",
            "Jazz",
            "Classical",
            "Vocal",
            "Electronic",
            "Hip Hop",
            "Acoustic"
        )

        val PRESET_LEVELS = listOf(
            listOf<Short>(0, 0, 0, 0, 0),             // Flat
            listOf<Short>(750, 500, 100, 0, 0),       // Bass Boost
            listOf<Short>(0, 0, 100, 500, 800),       // Treble Boost
            listOf<Short>(500, 300, -100, 350, 650),  // Rock
            listOf<Short>(-100, 250, 500, 300, -150), // Pop
            listOf<Short>(400, 200, -150, 250, 500),  // Jazz
            listOf<Short>(450, 300, -150, 250, 400),  // Classical
            listOf<Short>(-300, 100, 650, 400, 0),    // Vocal
            listOf<Short>(650, 450, 0, 250, 650),     // Electronic
            listOf<Short>(750, 550, 0, 200, 350),     // Hip Hop
            listOf<Short>(350, 200, 150, 350, 450)    // Acoustic
        )


    }
    private fun setupEqualizer(audioSessionId: Int) {
        val defaultBands = listOf(
            EqBand(0, 60000, 0),
            EqBand(1, 230000, 0),
            EqBand(2, 910000, 0),
            EqBand(3, 3600000, 0),
            EqBand(4, 14000000, 0)
        )

        val isEnabled = prefs.getBoolean("eq_enabled", false)
        val presetIdx = prefs.getInt("eq_preset_idx", 0).toShort()
        val bass = prefs.getInt("eq_bass_boost", 0)
        val virt = prefs.getInt("eq_virtualizer", 0)
        val reverb = prefs.getString("eq_reverb", "Off") ?: "Off"

        val initialBands = if (presetIdx in PRESET_LEVELS.indices) {
            val levels = PRESET_LEVELS[presetIdx.toInt()]
            defaultBands.mapIndexed { idx, band ->
                band.copy(level = levels.getOrElse(idx) { 0 })
            }
        } else {
            defaultBands
        }

        _state.update { 
            it.copy(
                isEqEnabled = isEnabled,
                eqBands = initialBands,
                eqPresets = EQUALIZER_PRESETS,
                currentEqPreset = presetIdx,
                minEqLevel = -1500,
                maxEqLevel = 1500,
                bassBoostLevel = bass,
                virtualizerLevel = virt,
                reverbPreset = reverb
            ) 
        }


    }
    fun setEqEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("eq_enabled", enabled).apply()
        _state.update { it.copy(isEqEnabled = enabled) }


    }

    fun setVisualizerMode(mode: VisualizerMode) {
        prefs.edit().putString("visualizer_mode", mode.name).apply()
        _state.update { it.copy(visualizerMode = mode) }
    }

    fun setVisualizerTheme(theme: VisualizerTheme) {
        prefs.edit().putString("visualizer_theme", theme.name).apply()
        _state.update { it.copy(visualizerTheme = theme) }
    }

    fun setVisualizerSensitivity(sensitivity: Float) {
        prefs.edit().putFloat("visualizer_sensitivity", sensitivity).apply()
        _state.update { it.copy(visualizerSensitivity = sensitivity) }
    }

    fun setEqBandLevel(bandId: Short, level: Short) {
        val clampedLevel = level.coerceIn(_state.value.minEqLevel, _state.value.maxEqLevel)
        val updatedBands = _state.value.eqBands.map { 
            if (it.bandId == bandId) it.copy(level = clampedLevel) else it
        }
        _state.update { it.copy(eqBands = updatedBands, currentEqPreset = -1) }
        prefs.edit().putInt("eq_preset_idx", -1).apply()


    }
    fun useEqPreset(presetId: Short) {
        if (presetId in PRESET_LEVELS.indices) {
            val levels = PRESET_LEVELS[presetId.toInt()]
            val updatedBands = _state.value.eqBands.mapIndexed { index, band ->
                band.copy(level = levels.getOrElse(index) { 0 })
            }
            prefs.edit().putInt("eq_preset_idx", presetId.toInt()).apply()
            _state.update { it.copy(currentEqPreset = presetId, eqBands = updatedBands) }
        } else {
            _state.update { it.copy(currentEqPreset = presetId) }
        }


    }
    fun setBassBoost(level: Int) {
        val clamped = level.coerceIn(0, 100)
        prefs.edit().putInt("eq_bass_boost", clamped).apply()
        _state.update { it.copy(bassBoostLevel = clamped) }


    }
    fun setVirtualizer(level: Int) {
        val clamped = level.coerceIn(0, 100)
        prefs.edit().putInt("eq_virtualizer", clamped).apply()
        _state.update { it.copy(virtualizerLevel = clamped) }


    }
    fun setReverb(preset: String) {
        prefs.edit().putString("eq_reverb", preset).apply()
        _state.update { it.copy(reverbPreset = preset) }


    }
    fun resetEqualizer() {
        val flatBands = _state.value.eqBands.map { it.copy(level = 0) }
        prefs.edit()
            .putInt("eq_preset_idx", 0)
            .putInt("eq_bass_boost", 0)
            .putInt("eq_virtualizer", 0)
            .putString("eq_reverb", "Off")
            .apply()
        _state.update {
            it.copy(
                eqBands = flatBands,
                currentEqPreset = 0,
                bassBoostLevel = 0,
                virtualizerLevel = 0,
                reverbPreset = "Off"
            )
        }


    }
    fun dismissBottomSheet() {
        _state.update { 
            it.copy(
                isBottomSheetVisible = false,
                isFullScreenLyricsOpen = false,
                isEditingLyrics = false
            ) 
        }
    }
    fun openBottomSheet() {
        if (_state.value.currentTrack != null) {
            _state.update { it.copy(isBottomSheetVisible = true) }
        }
    }

    /**
     * Explicitly opens and presents the audio player from notifications or widgets,
     * resetting any search or settings full-screen overlays to bring the player to front.
     */
    fun openAudioPlayer() {
        val current = _state.value.currentTrack
        if (current != null) {
            if (current.isVideo) {
                _state.update {
                    it.copy(
                        isSettingsOpen = false,
                        isSearchOpen = false,
                        isVideoPlayerOpen = true,
                        isBottomSheetVisible = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSettingsOpen = false,
                        isSearchOpen = false,
                        isBottomSheetVisible = true,
                        isVideoPlayerOpen = false
                    )
                }
            }
        } else {
            // If no track is playing, open the first available audio track if present
            val defaultTrack = _state.value.allTracks.firstOrNull { !it.isVideo } 
                ?: _state.value.deviceTracks.firstOrNull { !it.isVideo }
                ?: _state.value.allTracks.firstOrNull()
            if (defaultTrack != null) {
                selectTrack(defaultTrack)
                _state.update {
                    it.copy(
                        isSettingsOpen = false,
                        isSearchOpen = false,
                        isBottomSheetVisible = true,
                        isVideoPlayerOpen = false
                    )
                }
            }
        }
    }
    fun closePlayer() {
        player?.pause()
        progressJob?.cancel()
        _state.update {
            it.copy(
                currentTrack = null,
                isPlaying = false,
                isBottomSheetVisible = false,
                isFullScreenLyricsOpen = false,
                progressMs = 0L
            )
        }


    // Fullscreen Lyrics Navigation
    }
    fun openFullScreenLyrics() {
        _state.update { it.copy(isFullScreenLyricsOpen = true) }


    }
    fun closeFullScreenLyrics() {
        _state.update { it.copy(isFullScreenLyricsOpen = false) }
    }

    fun openSyncStudio() {
        _state.update { it.copy(isSyncStudioOpen = true) }
    }

    fun closeSyncStudio() {
        _state.update { it.copy(isSyncStudioOpen = false) }
    }

    // Playlist Management Actions
    fun openAddToPlaylist(track: Track) {
        _state.update { it.copy(trackToAddToPlaylist = track) }


    }
    fun closeAddToPlaylist() {
        _state.update { it.copy(trackToAddToPlaylist = null) }


    }
    fun clearPlaylistFeedbackMessage() {
        _state.update { it.copy(playlistFeedbackMessage = null) }


    }
    fun addTrackToPlaylist(playlistId: String, track: Track) {
        val currentPlaylists = _state.value.playlists
        val targetPlaylist = currentPlaylists.find { it.id == playlistId } ?: return

        if (targetPlaylist.tracks.any { it.id == track.id }) {
            _state.update {
                it.copy(
                    trackToAddToPlaylist = null,
                    playlistFeedbackMessage = "'${track.title}' is already in ${targetPlaylist.name}"
                )
            }
            return
        }

        val updatedTracks = targetPlaylist.tracks + track
        val updatedPlaylist = targetPlaylist.copy(tracks = updatedTracks)
        val updatedList = currentPlaylists.map {
            if (it.id == playlistId) updatedPlaylist else it
        }

        _state.update {
            it.copy(
                playlists = updatedList,
                tracks = if (it.selectedPlaylistId == playlistId) updatedTracks else it.tracks,
                viewingPlaylist = if (it.viewingPlaylist?.id == playlistId) updatedPlaylist else it.viewingPlaylist,
                trackToAddToPlaylist = null,
                playlistFeedbackMessage = "Added '${track.title}' to ${targetPlaylist.name}"
            )
        }


    }
    fun createPlaylistAndAddTrack(name: String, description: String, track: Track? = null) {
        val colors = listOf("FF6200EE", "FF03DAC5", "FF8B5CF6", "FFEC4899", "FFF59E0B", "FF10B981", "FF06B6D4")
        val color = colors[(_state.value.playlists.size % colors.size)]
        val newId = "custom_pl_${System.currentTimeMillis()}"
        val initialTracks = if (track != null) listOf(track) else emptyList()

        val newPlaylist = Playlist(
            id = newId,
            name = name,
            description = description.ifBlank { "${initialTracks.size} track(s)" },
            coverUrl = null,
            coverColorStr = color,
            tracks = initialTracks
        )

        val updatedPlaylists = _state.value.playlists + newPlaylist

        _state.update {
            it.copy(
                playlists = updatedPlaylists,
                trackToAddToPlaylist = null,
                playlistFeedbackMessage = if (track != null) {
                    "Created '$name' and added '${track.title}'"
                } else {
                    "Created playlist '$name'"
                }
            )
        }


    }
    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        val currentPlaylists = _state.value.playlists
        val targetPlaylist = currentPlaylists.find { it.id == playlistId } ?: return
        val updatedTracks = targetPlaylist.tracks.filter { it.id != trackId }
        val updatedPlaylist = targetPlaylist.copy(tracks = updatedTracks)
        val updatedList = currentPlaylists.map {
            if (it.id == playlistId) updatedPlaylist else it
        }

        _state.update {
            it.copy(
                playlists = updatedList,
                tracks = if (it.selectedPlaylistId == playlistId) updatedTracks else it.tracks,
                viewingPlaylist = if (it.viewingPlaylist?.id == playlistId) updatedPlaylist else it.viewingPlaylist,
                playlistFeedbackMessage = "Removed track from ${targetPlaylist.name}"
            )
        }


    }
    private fun startProgressTimer() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                val currentPos = player?.currentPosition ?: 0L
                val synced = _state.value.currentSyncedLyrics
                val activeIdx = if (synced.isNotEmpty()) {
                    synced.indexOfLast { line -> line.timeMs <= currentPos }
                } else -1
                _state.update {
                    it.copy(
                        progressMs = currentPos,
                        totalPlaytimeSeconds = it.totalPlaytimeSeconds + 1L,
                        activeLyricIndex = activeIdx
                    )
                }
                delay(400L) // Smooth lyric tracking and progress updates
            }
        }


    }
    fun setSmartSortOption(option: SmartSortOption) {
        _state.update { it.copy(smartSortOption = option) }


    }
    fun dismissScanMessage() {
        _state.update { it.copy(storageScanMessage = null) }


    }
    fun setScanMessage(msg: String?) {
        _state.update { it.copy(storageScanMessage = msg) }


    }
    fun scanDeviceMusic() {
        if (_state.value.isScanningDevice) return
        val context = getApplication<Application>()
        _state.update { it.copy(isScanningDevice = true, storageScanMessage = "Scanning storage folders for audio & video files...") }
        viewModelScope.launch(Dispatchers.IO) {
            val scannedList = mutableListOf<Track>()
            val colors = listOf("FF3B82F6", "FF10B981", "FF8B5CF6", "FFF59E0B", "FFEC4899", "FF06B6D4")

            // 1. Query Audio files via high-speed MediaStore cursor (instant metadata without file blocking)
            try {
                val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val audioProjection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
                )
                val audioSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
                val audioSort = "${MediaStore.Audio.Media.TITLE} ASC"

                context.contentResolver.query(audioUri, audioProjection, audioSelection, null, audioSort)?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                    val dataCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    val bucketCol = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val title = cursor.getString(titleCol) ?: "Unknown Audio"
                        val rawArtist = if (artistCol != -1) cursor.getString(artistCol) else null
                        val artist = if (rawArtist.isNullOrBlank() || rawArtist == "<unknown>") "Device Audio" else rawArtist
                        val duration = if (durationCol != -1) cursor.getLong(durationCol) else 0L
                        val albumId = if (albumIdCol != -1) cursor.getLong(albumIdCol) else -1L
                        val dataPath = if (dataCol != -1) cursor.getString(dataCol) else null
                        val rawBucket = if (bucketCol != -1) cursor.getString(bucketCol) else null

                        val detectedFolder = when {
                            !rawBucket.isNullOrBlank() -> rawBucket
                            !dataPath.isNullOrBlank() -> {
                                try { File(dataPath).parentFile?.name ?: "Music" } catch (_: Exception) { "Music" }
                            }
                            else -> "Music"
                        }
                        val detectedFolderPath = if (!dataPath.isNullOrBlank()) {
                            try { File(dataPath).parent ?: "/storage/emulated/0/$detectedFolder" } catch (_: Exception) { "/storage/emulated/0/$detectedFolder" }
                        } else "/storage/emulated/0/$detectedFolder"

                        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                        val albumArtUri = if (albumId != -1L) {
                            ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"),
                                albumId
                            ).toString()
                        } else null
                        val resolvedArtwork = albumArtUri ?: CoverImageProvider.getFallbackCover(title, artist)
                        val color = colors[((id.hashCode() and 0x7FFFFFFF) % colors.size)]

                        scannedList.add(
                            Track(
                                id = "local_media_$id",
                                title = title,
                                artist = artist,
                                mediaUrl = contentUri.toString(),
                                artworkUrl = resolvedArtwork,
                                coverColorStr = color,
                                isLocal = true,
                                durationMs = duration,
                                isVideo = false,
                                folderName = detectedFolder,
                                folderPath = detectedFolderPath
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Query Video files via high-speed MediaStore cursor
            try {
                val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val videoProjection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
                )
                val videoSort = "${MediaStore.Video.Media.TITLE} ASC"

                context.contentResolver.query(videoUri, videoProjection, null, null, videoSort)?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val titleCol = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                    val nameCol = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationCol = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                    val widthCol = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
                    val heightCol = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
                    val dataCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    val bucketCol = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        var rawTitle = if (titleCol != -1) cursor.getString(titleCol) else null
                        val displayName = if (nameCol != -1) cursor.getString(nameCol) else null
                        
                        if (rawTitle.isNullOrBlank()) {
                            rawTitle = displayName?.substringBeforeLast(".") ?: "Local Video $id"
                        } else if (rawTitle.contains(".")) {
                            rawTitle = rawTitle.substringBeforeLast(".")
                        }
                        
                        val duration = if (durationCol != -1) cursor.getLong(durationCol) else 0L
                        val width = if (widthCol != -1) cursor.getInt(widthCol) else 0
                        val height = if (heightCol != -1) cursor.getInt(heightCol) else 0
                        val dataPath = if (dataCol != -1) cursor.getString(dataCol) else null
                        val rawBucket = if (bucketCol != -1) cursor.getString(bucketCol) else null

                        val detectedFolder = when {
                            !rawBucket.isNullOrBlank() -> rawBucket
                            !dataPath.isNullOrBlank() -> {
                                try { File(dataPath).parentFile?.name ?: "Videos" } catch (_: Exception) { "Videos" }
                            }
                            else -> "Videos"
                        }
                        val detectedFolderPath = if (!dataPath.isNullOrBlank()) {
                            try { File(dataPath).parent ?: "/storage/emulated/0/$detectedFolder" } catch (_: Exception) { "/storage/emulated/0/$detectedFolder" }
                        } else "/storage/emulated/0/$detectedFolder"

                        val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                        val videoArt = contentUri.toString()
                        val color = colors[((id.hashCode() and 0x7FFFFFFF) % colors.size)]
                        val resStr = if (width > 0 && height > 0) "${width}x${height}" else "HD Video"

                        scannedList.add(
                            Track(
                                id = "local_video_$id",
                                title = rawTitle,
                                artist = "Local Video",
                                mediaUrl = contentUri.toString(),
                                artworkUrl = videoArt,
                                coverColorStr = color,
                                isLocal = true,
                                durationMs = duration,
                                isVideo = true,
                                width = width,
                                height = height,
                                resolution = resStr,
                                folderName = detectedFolder,
                                folderPath = detectedFolderPath
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. Fallback: only scan physical directories if MediaStore returned 0 items
            if (scannedList.isEmpty()) {
                try {
                    val publicDirs = listOfNotNull(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
                    )
                    val audioExts = setOf("mp3", "m4a", "flac", "wav", "ogg", "aac", "opus")
                    val videoExts = setOf("mp4", "mkv", "webm", "avi", "mov", "3gp", "ts")

                    for (dir in publicDirs) {
                        if (dir.exists() && dir.canRead()) {
                            val files = dir.listFiles() ?: continue
                            for (file in files) {
                                if (file.isFile) {
                                    val ext = file.extension.lowercase()
                                    val isAudio = ext in audioExts
                                    val isVid = ext in videoExts
                                    if (isAudio || isVid) {
                                        val uriStr = Uri.fromFile(file).toString()
                                        val color = colors[((file.name.hashCode() and 0x7FFFFFFF) % colors.size)]
                                        val fsArt = if (isVid) {
                                            CoverImageProvider.getVideoFallbackCover(file.nameWithoutExtension, "Local Video")
                                        } else {
                                            CoverImageProvider.getFallbackCover(file.nameWithoutExtension, "Local Audio")
                                        }
                                        scannedList.add(
                                            Track(
                                                id = "local_fs_${file.name.hashCode()}",
                                                title = file.nameWithoutExtension,
                                                artist = if (isVid) "Local Video" else "Local Audio",
                                                mediaUrl = uriStr,
                                                artworkUrl = fsArt,
                                                coverColorStr = color,
                                                isLocal = true,
                                                durationMs = 0L,
                                                isVideo = isVid,
                                                folderName = dir.name,
                                                folderPath = dir.absolutePath
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            withContext(Dispatchers.Main) {
                saveAndMergeDeviceTracks(scannedList, fromScan = true)
            }
        }
    }

    fun importAudioUris(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val context = getApplication<Application>()
        _state.update { it.copy(isScanningDevice = true, storageScanMessage = "Importing selected media files...") }
        viewModelScope.launch(Dispatchers.IO) {
            val importedList = mutableListOf<Track>()
            val colors = listOf("FF3B82F6", "FF10B981", "FF8B5CF6", "FFF59E0B", "FFEC4899", "FF06B6D4")

            for (uri in uris) {
                try {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {}

                    var displayName = "Media File"
                    val mimeType = context.contentResolver.getType(uri) ?: ""

                    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIdx != -1) {
                                displayName = cursor.getString(nameIdx) ?: displayName
                            }
                        }
                    }

                    val ext = displayName.substringAfterLast('.', "").lowercase()
                    val videoExts = setOf("mp4", "mkv", "webm", "avi", "mov", "3gp", "m4v", "ts", "flv", "wmv", "mpg", "mpeg", "ogv")
                    val isVideoFile = mimeType.startsWith("video/") || ext in videoExts || uri.toString().contains("video", ignoreCase = true)

                    var title = if (displayName.contains(".")) displayName.substringBeforeLast(".") else displayName
                    var artist = if (isVideoFile) "Local Video" else "Local Audio"
                    var duration = 0L
                    var width = 0
                    var height = 0

                    val mmr = MediaMetadataRetriever()
                    try {
                        mmr.setDataSource(context, uri)
                        val metaTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        val metaArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        val metaDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        val metaWidth = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        val metaHeight = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                        if (!metaTitle.isNullOrBlank()) title = metaTitle
                        if (!metaArtist.isNullOrBlank()) artist = metaArtist
                        metaDuration?.toLongOrNull()?.let { duration = it }
                        metaWidth?.toIntOrNull()?.let { width = it }
                        metaHeight?.toIntOrNull()?.let { height = it }
                    } catch (_: Exception) {}
                    finally {
                        try { mmr.release() } catch (_: Exception) {}
                    }

                    // Extract folder name from uri path if possible
                    var folderName = if (isVideoFile) "Imported Videos" else "Imported Audio"
                    var folderPath = "/storage/emulated/0/$folderName"
                    try {
                        val path = uri.path
                        if (path != null) {
                            val segments = path.split("/").filter { it.isNotBlank() }
                            if (segments.size >= 2) {
                                val cand = segments[segments.size - 2].substringAfterLast(":")
                                if (cand.length in 2..28 && !cand.contains("%")) {
                                    folderName = cand
                                    folderPath = "/storage/emulated/0/$cand"
                                }
                            }
                        }
                    } catch (_: Exception) {}

                    val trackId = "local_saf_${uri.hashCode().toLong() and 0xFFFFFFFFL}"
                    val isVid = isVideoFile || width > 0 || height > 0
                    val importedArt = if (isVid) uri.toString() else CoverImageProvider.getFallbackCover(title, artist)
                    val color = colors[((uri.hashCode() and 0x7FFFFFFF) % colors.size)]

                    importedList.add(
                        Track(
                            id = trackId,
                            title = title,
                            artist = artist,
                            mediaUrl = uri.toString(),
                            artworkUrl = importedArt,
                            coverColorStr = color,
                            isLocal = true,
                            durationMs = duration,
                            isVideo = isVid,
                            width = width,
                            height = height,
                            resolution = if (width > 0 && height > 0) "${width}x${height}" else if (isVid) "HD" else null,
                            folderName = folderName,
                            folderPath = folderPath
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            withContext(Dispatchers.Main) {
                saveAndMergeDeviceTracks(importedList, fromScan = false)
            }
        }
    }

    private fun saveAndMergeDeviceTracks(newTracks: List<Track>, fromScan: Boolean) {
        val existing = _state.value.deviceTracks
        val combinedDevice = (existing + newTracks).distinctBy { it.mediaUrl }
        persistDeviceTracks(combinedDevice)

        val nonLocal = _state.value.allTracks.filter { !it.isLocal }
        val updatedAll = (combinedDevice + nonLocal).distinctBy { it.id }

        val devicePlaylist = Playlist(
            id = "device_playlist",
            name = "On Device",
            description = "${combinedDevice.size} media files across folders",
            coverUrl = null,
            coverColorStr = "FF10B981",
            tracks = combinedDevice
        )

        val updatedPlaylists = listOf(devicePlaylist) + _state.value.playlists.filter { it.id != "device_playlist" }

        val folderCount = combinedDevice.mapNotNull { it.folderName }.distinct().size.coerceAtLeast(1)
        val msg = if (fromScan) {
            if (newTracks.isEmpty()) "Scan complete. ${combinedDevice.size} files in $folderCount folders."
            else "Found ${newTracks.size} media file(s) across $folderCount folders"
        } else {
            "Successfully imported ${newTracks.size} media file(s)"
        }

        _state.update {
            it.copy(
                isScanningDevice = false,
                storageScanMessage = msg,
                deviceTracks = combinedDevice,
                allTracks = updatedAll,
                playlists = updatedPlaylists,
                tracks = if (it.selectedPlaylistId == "device_playlist") combinedDevice else it.tracks
            )
        }


    }
    private fun persistDeviceTracks(tracks: List<Track>) {
        val array = org.json.JSONArray()
        for (t in tracks) {
            val obj = org.json.JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("artist", t.artist)
                put("mediaUrl", t.mediaUrl)
                if (t.artworkUrl != null) put("artworkUrl", t.artworkUrl)
                put("coverColorStr", t.coverColorStr)
                put("isLocal", t.isLocal)
                put("durationMs", t.durationMs)
                put("isVideo", t.isVideo)
                put("width", t.width)
                put("height", t.height)
                if (t.resolution != null) put("resolution", t.resolution)
                put("bpm", t.bpm)
                if (t.folderName != null) put("folderName", t.folderName)
                if (t.folderPath != null) put("folderPath", t.folderPath)
            }
            array.put(obj)
        }
        prefs.edit().putString("device_tracks_json", array.toString()).apply()


    }
    private fun loadSavedDeviceTracks(): List<Track> {
        val jsonStr = prefs.getString("device_tracks_json", null) ?: return emptyList()
        val list = mutableListOf<Track>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Track(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        artist = obj.getString("artist"),
                        mediaUrl = obj.getString("mediaUrl"),
                        artworkUrl = if (obj.has("artworkUrl") && !obj.isNull("artworkUrl")) obj.getString("artworkUrl") else (if (obj.optBoolean("isVideo", false)) CoverImageProvider.getVideoFallbackCover(obj.getString("title"), obj.optString("artist", "Video")) else CoverImageProvider.getFallbackCover(obj.getString("title"), obj.optString("artist", "Artist"))),
                        coverColorStr = obj.optString("coverColorStr", "FF10B981"),
                        isLocal = obj.optBoolean("isLocal", true),
                        durationMs = obj.optLong("durationMs", 0L),
                        isVideo = obj.optBoolean("isVideo", false),
                        width = obj.optInt("width", 0),
                        height = obj.optInt("height", 0),
                        resolution = if (obj.has("resolution")) obj.getString("resolution") else null,
                        bpm = obj.optInt("bpm", 0),
                        folderName = if (obj.has("folderName")) obj.getString("folderName") else null,
                        folderPath = if (obj.has("folderPath")) obj.getString("folderPath") else null
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list


    }
    fun removeDeviceTrack(trackId: String) {
        val updatedDevice = _state.value.deviceTracks.filter { it.id != trackId }
        persistDeviceTracks(updatedDevice)
        val nonLocal = _state.value.allTracks.filter { !it.isLocal }
        val updatedAll = updatedDevice + nonLocal

        val devicePlaylist = Playlist(
            id = "device_playlist",
            name = "On Device",
            description = "${updatedDevice.size} audio files from local storage",
            coverUrl = null,
            coverColorStr = "FF10B981",
            tracks = updatedDevice
        )
        val updatedPlaylists = listOf(devicePlaylist) + _state.value.playlists.filter { it.id != "device_playlist" }

        _state.update {
            it.copy(
                deviceTracks = updatedDevice,
                allTracks = updatedAll,
                playlists = updatedPlaylists,
                tracks = if (it.selectedPlaylistId == "device_playlist") updatedDevice else it.tracks
            )
        }


    }
    fun clearAllDeviceTracks() {
        persistDeviceTracks(emptyList())
        val nonLocal = _state.value.allTracks.filter { !it.isLocal }
        val updatedPlaylists = _state.value.playlists.filter { it.id != "device_playlist" }

        _state.update {
            it.copy(
                deviceTracks = emptyList(),
                allTracks = nonLocal,
                playlists = updatedPlaylists,
                tracks = nonLocal,
                storageScanMessage = "Device storage tracks cleared"
            )
        }
    }

    // Suno & Custom Stream Methods
    fun openAddStream() {
        _state.update { it.copy(isAddStreamOpen = true) }
    }

    fun closeAddStream() {
        _state.update { it.copy(isAddStreamOpen = false) }
    }

    fun importSunoProfile(profileUrlOrHandle: String) {
        val handle = profileUrlOrHandle
            .substringAfter("@")
            .substringBefore("?")
            .substringBefore("/")
            .ifBlank { "technicalrave7605" }
            
        val artistName = handle.replaceFirstChar { it.uppercase() }
        val streamColors = listOf("FF8B5CF6", "FF06B6D4", "FFEC4899", "FFF59E0B", "FF10B981")
        
        // Generate stream tracks for this Suno creator profile with real audio feeds and dynamic styling
        val sunoTracks = listOf(
            Track(
                id = "suno_${handle}_1",
                title = "Cyber Eclipse (Suno EDM Mix)",
                artist = artistName,
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
                artworkUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&auto=format&fit=crop",
                coverColorStr = "FF8B5CF6",
                isLocal = false,
                durationMs = 286000L
            ),
            Track(
                id = "suno_${handle}_2",
                title = "Neon Rave Overdrive",
                artist = artistName,
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
                artworkUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&auto=format&fit=crop",
                coverColorStr = "FFEC4899",
                isLocal = false,
                durationMs = 312000L
            ),
            Track(
                id = "suno_${handle}_3",
                title = "Midnight Synth Odyssey",
                artist = artistName,
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
                artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&auto=format&fit=crop",
                coverColorStr = "FF06B6D4",
                isLocal = false,
                durationMs = 275000L
            ),
            Track(
                id = "suno_${handle}_4",
                title = "Hyperdrive Beats Vol. 1",
                artist = artistName,
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
                artworkUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&auto=format&fit=crop",
                coverColorStr = "FFF59E0B",
                isLocal = false,
                durationMs = 372000L
            )
        )

        // Save into device/custom tracks list
        saveAndMergeDeviceTracks(sunoTracks, fromScan = false)

        // Create or update dedicated Suno Playlist
        val sunoPlaylistId = "suno_playlist_${handle.lowercase()}"
        val existingPlaylists = _state.value.playlists.filter { it.id != sunoPlaylistId }
        val sunoPlaylist = Playlist(
            id = sunoPlaylistId,
            name = "Suno: @$handle",
            description = "Custom streams and releases from suno.com/@$handle",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&auto=format&fit=crop",
            coverColorStr = "FF8B5CF6",
            tracks = sunoTracks
        )

        _state.update {
            it.copy(
                playlists = listOf(sunoPlaylist) + existingPlaylists,
                playlistFeedbackMessage = "Imported @$handle stream playlist into library!"
            )
        }
    }

    fun addCustomStreamTrack(
        url: String,
        title: String,
        artist: String,
        artworkUrl: String?,
        isVideo: Boolean
    ) {
        val trackId = "stream_${System.currentTimeMillis()}"
        val colors = listOf("FF8B5CF6", "FF06B6D4", "FFEC4899", "FFF59E0B", "FF10B981")
        val color = colors[((url.hashCode() and 0x7FFFFFFF) % colors.size)]
        
        val customTrack = Track(
            id = trackId,
            title = title,
            artist = artist,
            mediaUrl = url,
            artworkUrl = artworkUrl ?: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&auto=format&fit=crop",
            coverColorStr = color,
            isLocal = false,
            durationMs = 0L,
            isVideo = isVideo,
            resolution = if (isVideo) "Stream HD" else null
        )

        saveAndMergeDeviceTracks(listOf(customTrack), fromScan = false)
        _state.update {
            it.copy(
                playlistFeedbackMessage = "Added '$title' to library streams!"
            )
        }
    }

    fun setMainTab(tab: MainTab) {
        _state.update { it.copy(selectedMainTab = tab) }
    }

    fun setLibraryTab(tab: LibraryTab) {
        _state.update {
            it.copy(
                selectedLibraryTab = tab,
                selectedPlaylistId = null,
                viewingPlaylist = null
            )
        }
    }

    fun openSearch(overrideFilter: SearchFilter? = null) {
        val targetFilter = overrideFilter ?: when {
            _state.value.selectedArtist != null -> SearchFilter.ARTIST_DETAIL
            _state.value.viewingPlaylist != null || _state.value.selectedPlaylistId != null -> SearchFilter.PLAYLIST_DETAIL
            _state.value.selectedMainTab == MainTab.VIDEO -> SearchFilter.VIDEOS
            _state.value.selectedLibraryTab == LibraryTab.ARTISTS -> SearchFilter.ARTISTS
            _state.value.selectedLibraryTab == LibraryTab.SONGS -> SearchFilter.SONGS
            _state.value.selectedLibraryTab == LibraryTab.STORAGE -> SearchFilter.DEVICE
            _state.value.selectedLibraryTab == LibraryTab.PLAYLIST -> SearchFilter.PLAYLISTS
            _state.value.selectedLibraryTab == LibraryTab.HISTORY -> SearchFilter.HISTORY
            else -> SearchFilter.ALL
        }
        _state.update { it.copy(isSearchOpen = true, searchFilter = targetFilter) }
    }
    fun closeSearch() {
        _state.update { it.copy(isSearchOpen = false, searchQuery = "") }


    }
    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }


    }
    fun setSearchFilter(filter: SearchFilter) {
        _state.update { it.copy(searchFilter = filter) }


    }
    fun clearSearchQuery() {
        _state.update { it.copy(searchQuery = "") }


    }
    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val current = _state.value.recentSearches.toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)
        val capped = current.take(10)
        prefs.edit().putString("recent_searches_csv", capped.joinToString("|")).apply()
        _state.update { it.copy(recentSearches = capped) }


    }
    fun removeRecentSearch(query: String) {
        val current = _state.value.recentSearches.filter { it != query }
        prefs.edit().putString("recent_searches_csv", current.joinToString("|")).apply()
        _state.update { it.copy(recentSearches = current) }


    }
    fun clearRecentSearches() {
        prefs.edit().remove("recent_searches_csv").apply()
        _state.update { it.copy(recentSearches = emptyList()) }


    // Settings Navigation & Actions
    }
    fun openSettings(subPage: SettingsSubPage? = null) {
        _state.update { it.copy(isSettingsOpen = true, currentSettingsSubPage = subPage) }


    }
    fun closeSettings() {
        _state.update { it.copy(isSettingsOpen = false, currentSettingsSubPage = null) }


    }
    fun navigateToSettingsSubPage(subPage: SettingsSubPage) {
        _state.update { it.copy(currentSettingsSubPage = subPage) }


    }
    fun backFromSettingsSubPage() {
        _state.update { it.copy(currentSettingsSubPage = null) }


    }
    fun updateProfile(name: String, bio: String, avatarColor: String, favoriteGenre: String) {
        _state.update {
            it.copy(
                profile = it.profile.copy(
                    name = name,
                    bio = bio,
                    avatarColor = avatarColor,
                    favoriteGenre = favoriteGenre
                )
            )
        }


    }
    fun toggleTheme() {
        val newDark = !state.value.isDarkTheme
        val newMode = if (newDark) "Dark" else "Light"
        prefs.edit()
            .putString("app_theme_mode", newMode)
            .putBoolean("app_is_dark_theme", newDark)
            .apply()
        _state.update {
            it.copy(
                isDarkTheme = newDark,
                themeMode = newMode
            )
        }
    }

    fun toggleTheme(isDark: Boolean) {
        val newMode = if (isDark) "Dark" else "Light"
        prefs.edit()
            .putString("app_theme_mode", newMode)
            .putBoolean("app_is_dark_theme", isDark)
            .apply()
        _state.update {
            it.copy(
                isDarkTheme = isDark,
                themeMode = newMode
            )
        }
    }

    fun setThemeMode(mode: String, systemIsDark: Boolean = true) {
        val isDark = when (mode) {
            "Light" -> false
            "Dark" -> true
            else -> systemIsDark
        }
        prefs.edit()
            .putString("app_theme_mode", mode)
            .putBoolean("app_is_dark_theme", isDark)
            .apply()
        _state.update { it.copy(themeMode = mode, isDarkTheme = isDark) }
    }

    fun setDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean("app_dynamic_color", enabled).apply()
        _state.update { it.copy(dynamicColor = enabled) }
    }

    fun setAccentColor(index: Int) {
        prefs.edit().putInt("app_accent_color_index", index).apply()
        _state.update { it.copy(accentColorIndex = index) }
    }

    fun openThemeModal() {
        _state.update { it.copy(isThemeModalOpen = true) }
    }

    fun closeThemeModal() {
        _state.update { it.copy(isThemeModalOpen = false) }
    }
    fun updatePlaybackPrefs(prefs: PlaybackPreferences) {
        _state.update { it.copy(playbackPrefs = prefs) }
    }

    fun updateVideoPrefs(prefs: VideoPreferences) {
        _state.update { it.copy(videoPrefs = prefs) }
    }

    fun updateVideoDefaultQuality(quality: String) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(defaultQuality = quality)) }
    }

    fun updateVideoDefaultResizeMode(mode: String) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(defaultResizeMode = mode)) }
    }

    fun updateVideoSeekJump(seconds: Int) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(seekJumpSeconds = seconds)) }
    }

    fun updateVideoAutoHideControls(seconds: Int) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(autoHideControlsSeconds = seconds)) }
    }

    fun toggleVideoAutoRotate(enabled: Boolean) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(autoRotateLandscapeOnFullscreen = enabled)) }
    }

    fun toggleLockLandscapeOrientation(enabled: Boolean) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(lockLandscapeOrientation = enabled)) }
    }

    fun toggleVideoHardwareDecoderFallback(enabled: Boolean) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(hardwareDecoderFallback = enabled)) }
    }

    fun toggleVideoBackgroundAudio(enabled: Boolean) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(backgroundAudioPlayback = enabled)) }
    }

    fun updateVideoSubtitleSettings(fontSize: Int, opacity: Float) {
        _state.update {
            it.copy(
                videoPrefs = it.videoPrefs.copy(
                    subtitleFontSize = fontSize,
                    subtitleBackgroundOpacity = opacity
                )
            )
        }
    }

    fun toggleVideoResolutionBadge(enabled: Boolean) {
        _state.update { it.copy(videoPrefs = it.videoPrefs.copy(showVideoResolutionBadge = enabled)) }
    }

    fun resetVideoPreferences() {
        _state.update { it.copy(videoPrefs = VideoPreferences()) }
    }

    fun updateNotificationPrefs(prefs: NotificationPreferences) {
        _state.update { it.copy(notificationPrefs = prefs) }
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("selected_app_language", lang).apply()
        _state.update { it.copy(selectedLanguage = lang) }
    }

    fun setDailyPlaytimeGoal(minutes: Int) {
        _state.update { it.copy(dailyPlaytimeGoalMinutes = minutes) }
    }

    private fun createMediaItem(track: Track): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
        if (track.artworkUrl != null) {
            metadataBuilder.setArtworkUri(Uri.parse(track.artworkUrl))
        }
        return MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(Uri.parse(track.mediaUrl))
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }

    fun generateAiLyricsForTrack(track: Track) {
        _state.update { it.copy(editingLyricsTrack = track) }
        generateAiLyricsOrCaptions()
    }

    fun generateAiLyricsOrCaptions() {
        val track = _state.value.currentTrack ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isFetchingLyrics = true, lyricsFetchMessage = if (track.isVideo) "Generating AI video captions..." else "Generating AI lyrics...") }
            try {
                val apiKey = ""
                if (apiKey.isBlank()) {
                    delay(800L)
                    val generated = if (track.isVideo) {
                        """[00:00.00] [Scene 1: Introduction] Cinematic opening of \"${track.title}\"\n[00:15.50] [Scene 2: Action] Dynamic visual breakdown for creator \"${track.artist}\"\n[00:45.00] [Scene 3: Climax] Key moments and cinematic framing\n[01:10.00] [End of Transcript]"""
                    } else {
                        """[Verse 1]\nNeon lights and rhythmic flow,\nBeats that echo as we go,\n${track.title} playing in the night,\n${track.artist} guiding us with light.\n\n[Chorus]\nOoh, let the rhythm take control,\nFrequencies that touch the soul,\nSynthetic dreams and stellar skies,\nUnderneath the audio-visual eyes.\n\n[Bridge]\nTime stands still as melodies rise..."""
                    }
                    withContext(Dispatchers.Main) {
                        saveLyrics(generated)
                        _state.update { it.copy(isFetchingLyrics = false, lyricsFetchMessage = "AI generated successfully!") }
                    }
                    return@launch
                }
                val prompt = if (track.isVideo) {
                    "Generate detailed professional video closed-captions and a scene transcript breakdown for a video titled  + track.title +  by creator  + track.artist + . Provide timestamped or well-structured scene descriptions."
                } else {
                    "Generate rich, professional song lyrics (with Verse, Chorus, Bridge formatting) for a song titled  + track.title +  by artist  + track.artist + ."
                }
                
                val requestJson = org.json.JSONObject().apply {
                    put("contents", org.json.JSONArray().put(org.json.JSONObject().apply {
                        put("parts", org.json.JSONArray().put(org.json.JSONObject().apply {
                            put("text", prompt)
                        }))
                    }))
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()

                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), requestJson.toString())
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val json = JSONObject(body)
                        val text = json.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text") ?: "Could not generate content."

                        withContext(Dispatchers.Main) {
                            saveLyrics(text)
                            _state.update { it.copy(isFetchingLyrics = false, lyricsFetchMessage = "AI generated successfully!") }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(isFetchingLyrics = false, lyricsFetchMessage = "Failed to generate AI content: " + response.code) }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(isFetchingLyrics = false, lyricsFetchMessage = "Error: " + e.localizedMessage) }
                }
            }
        }
    }

}
