package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.media3.common.Player
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaMetadata
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import android.content.Intent
import android.app.SearchManager
import kotlinx.coroutines.delay

import com.example.ui.FrostedGlassDefaults
import com.example.ui.FrostedGlassPill
import com.example.ui.FrostedGlassSurface
import com.example.ui.frostedGlass
import com.example.db.TrackOverride
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import android.os.Build
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material.icons.filled.Timer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerApp(
    viewModel: MediaPlayerViewModel = viewModel(),
    incomingUri: android.net.Uri? = null
) {
    val onGenerateAiContent: () -> Unit = { viewModel.generateAiLyricsOrCaptions() }
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val autoScanPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanDeviceMusic()
        }
    }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("media_player_prefs", Context.MODE_PRIVATE)
        val hasScanned = prefs.getBoolean("has_initial_auto_scanned", false)
        val hasPerm = ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED
        if (!hasScanned) {
            prefs.edit().putBoolean("has_initial_auto_scanned", true).apply()
            if (hasPerm) {
                viewModel.scanDeviceMusic()
            } else {
                autoScanPermissionLauncher.launch(storagePermission)
            }
        } else if (hasPerm && state.deviceTracks.isEmpty()) {
            viewModel.scanDeviceMusic()
        }
    }

    LaunchedEffect(incomingUri) {
        if (incomingUri != null) {
            viewModel.importAudioUris(listOf(incomingUri))
        }
    }

    val getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String> = { track, metadata ->
        val override = state.trackOverrides.find { it.trackId == track.id }
        val title = override?.title ?: metadata?.title?.toString() ?: track.title
        val artist = override?.artist ?: metadata?.artist?.toString() ?: track.artist
        title to artist
    }

    val strings = getAppStrings(state.selectedLanguage)

    CompositionLocalProvider(LocalAppStrings provides strings) {
        var showSplash by remember { mutableStateOf(true) }

        if (showSplash) {
            SplashScreen(onSplashFinished = { showSplash = false })
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isExpanded = maxWidth > 600.dp

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (!state.isVideoFullscreen && !state.isVideoPlayerOpen) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Mini player cleanly docked in Scaffold bottomBar
                                AnimatedVisibility(
                                    visible = state.currentTrack != null && !state.isBottomSheetVisible && !state.isVideoPlayerOpen && !isExpanded,
                                    enter = slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    ) + fadeIn(
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ) + scaleIn(
                                        initialScale = 0.95f,
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ),
                                    exit = slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(durationMillis = 220, easing = FastOutLinearInEasing)
                                    ) + fadeOut(
                                        animationSpec = tween(durationMillis = 180)
                                    ) + scaleOut(
                                        targetScale = 0.95f,
                                        animationSpec = tween(durationMillis = 200)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    state.currentTrack?.let { currentTrack ->
                                        val (title, artist) = getTitleArtist(currentTrack, state.mediaMetadata)
                                        MiniPlayer(
                                            track = currentTrack,
                                            displayTitle = title,
                                            displayArtist = artist,
                                            mediaMetadata = state.mediaMetadata,
                                            isPlaying = state.isPlaying,
                                            isFavorite = state.favoriteTrackIds.contains(currentTrack.id),
                                            progressMs = state.progressMs,
                                            durationMs = state.durationMs,
                                            onToggleFavorite = { viewModel.toggleFavorite(currentTrack.id) },
                                            onSkipPrevious = { viewModel.skipPrevious() },
                                            onPlayPauseClick = { viewModel.togglePlayPause() },
                                            onSkipNext = { viewModel.skipNext() },
                                            onCloseClick = { viewModel.closePlayer() },
                                            onClick = {
                                                if (currentTrack.isVideo) {
                                                    viewModel.openVideoPlayer()
                                                } else {
                                                    viewModel.openBottomSheet()
                                                }
                                            },
                                            isDarkTheme = state.isDarkTheme
                                        )
                                    }
                                 }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

            if (isExpanded) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left side: Search, Settings, or Library
                    if (state.isSearchOpen) {
                        SearchScreen(
                            state = state,
                            viewModel = viewModel,
                            getTitleArtist = getTitleArtist,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    } else if (state.isSettingsOpen) {
                        SettingsScreen(
                            state = state,
                            viewModel = viewModel,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    } else {
                        if (state.selectedMainTab == MainTab.AUDIO) {
                            LibraryContent(
                                state = state,
                                viewModel = viewModel,
                                getTitleArtist = getTitleArtist,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        } else {
                            VideoLibraryContent(
                                state = state,
                                viewModel = viewModel,
                                getTitleArtist = getTitleArtist,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                    }

                    // Right side: Player
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.currentTrack != null) {
                            val (title, artist) = getTitleArtist(state.currentTrack!!, state.mediaMetadata)
                            NowPlayingScreen(
                                track = state.currentTrack!!,
                                displayTitle = title,
                                displayArtist = artist,
                                mediaMetadata = state.mediaMetadata,
                                isPlaying = state.isPlaying,
                                progressMs = state.progressMs,
                                durationMs = state.durationMs,
                                repeatMode = state.repeatMode,
                                shuffleModeEnabled = state.shuffleModeEnabled,
                                playbackSpeed = state.playbackSpeed,
                                currentLyrics = state.currentLyrics,
                                syncedLyrics = state.currentSyncedLyrics,
                                activeLyricIndex = state.activeLyricIndex,
                                isFetchingLyrics = state.isFetchingLyrics,
                                lyricsFetchMessage = state.lyricsFetchMessage,
                                isAutoScrollLyrics = state.isAutoScrollLyrics,
                                lyricsViewMode = state.lyricsViewMode,
                                syncedLyricsDisplayStyle = state.syncedLyricsDisplayStyle,
                                onSetSyncedLyricsDisplayStyle = { viewModel.setSyncedLyricsDisplayStyle(it) },
                                isEditingLyrics = state.isEditingLyrics,
                                isEditingMetadata = state.isEditingMetadata,
                                sleepTimerActive = state.sleepTimerActive,
                                sleepTimerRemainingMs = state.sleepTimerRemainingMs,
                                isEqEnabled = state.isEqEnabled,
                                eqBands = state.eqBands,
                                eqPresets = state.eqPresets,
                                currentEqPreset = state.currentEqPreset,
                                minEqLevel = state.minEqLevel,
                                maxEqLevel = state.maxEqLevel,
                                visualizerMode = state.visualizerMode,
                                visualizerTheme = state.visualizerTheme,
                                visualizerSensitivity = state.visualizerSensitivity,
                                isDarkTheme = state.isDarkTheme,
                                onClose = { viewModel.closePlayer() },
                                onOpenFullScreenLyrics = { viewModel.openFullScreenLyrics() },
                                onAddToPlaylist = { viewModel.openAddToPlaylist(state.currentTrack!!) },
                                onSaveLyrics = { viewModel.saveLyrics(it) },
                                onSetEditingLyrics = { viewModel.setEditingLyrics(it) },
                                onSeekToPosition = { viewModel.seekToPosition(it) },
                                onFetchLyrics = { queryTitle, queryArtist -> viewModel.fetchLyricsForCurrentTrack(queryTitle, queryArtist) },
                                onToggleAutoScroll = { viewModel.toggleAutoScrollLyrics(it) },
                                onSetLyricsViewMode = { viewModel.setLyricsViewMode(it) },
                                onClearLyricsMessage = { viewModel.clearLyricsFetchMessage() },
                                onSaveMetadata = { newTitle, newArtist -> viewModel.saveMetadataOverride(newTitle, newArtist) },
                                onSetEditingMetadata = { viewModel.setEditingMetadata(it) },
                                onPlayPause = { viewModel.togglePlayPause() },
                                onSkipNext = { viewModel.skipNext() },
                                onSkipPrevious = { viewModel.skipPrevious() },
                                onToggleRepeat = { viewModel.toggleRepeatMode() },
                                onToggleShuffle = { viewModel.toggleShuffleMode() },
                                onSetPlaybackSpeed = { viewModel.setPlaybackSpeed(it) },
                                onSetSleepTimer = { viewModel.setSleepTimer(it) },
                                onCancelSleepTimer = { viewModel.cancelSleepTimer() },
                                onSetEqEnabled = { viewModel.setEqEnabled(it) },
                                onSetEqBandLevel = { bandId, level -> viewModel.setEqBandLevel(bandId, level) },
                                onUseEqPreset = { viewModel.useEqPreset(it) },
                                onSeek = { progressFraction -> viewModel.seekTo(progressFraction) }
                            )
                        } else {
                            Text(
                                "Select a track to play",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            } else {
                if (state.isSearchOpen) {
                    SearchScreen(
                        state = state,
                        viewModel = viewModel,
                        getTitleArtist = getTitleArtist,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (state.isSettingsOpen) {
                    SettingsScreen(
                        state = state,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val pagerState = rememberPagerState(
                        initialPage = if (state.selectedMainTab == MainTab.AUDIO) 0 else 1,
                        pageCount = { 2 }
                    )

                    androidx.compose.runtime.LaunchedEffect(state.selectedMainTab) {
                        val targetPage = if (state.selectedMainTab == MainTab.AUDIO) 0 else 1
                        if (pagerState.currentPage != targetPage) {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }

                    androidx.compose.runtime.LaunchedEffect(pagerState.currentPage) {
                        val newTab = if (pagerState.currentPage == 0) MainTab.AUDIO else MainTab.VIDEO
                        if (state.selectedMainTab != newTab) {
                            viewModel.setMainTab(newTab)
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        if (page == 0) {
                            LibraryContent(
                                state = state,
                                viewModel = viewModel,
                                getTitleArtist = getTitleArtist,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            VideoLibraryContent(
                                state = state,
                                viewModel = viewModel,
                                getTitleArtist = getTitleArtist,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                if (state.isBottomSheetVisible && state.currentTrack != null) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.dismissBottomSheet() },
                        sheetState = bottomSheetState,
                        dragHandle = { BottomSheetDefaults.DragHandle() },
                        containerColor = FrostedGlassDefaults.GlassDarkElevated,
                        scrimColor = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    ) {
                        val (title, artist) = getTitleArtist(state.currentTrack!!, state.mediaMetadata)
                        NowPlayingScreen(
                            track = state.currentTrack!!,
                            displayTitle = title,
                            displayArtist = artist,
                            mediaMetadata = state.mediaMetadata,
                            isPlaying = state.isPlaying,
                            isFavorite = state.favoriteTrackIds.contains(state.currentTrack?.id),
                            onToggleFavorite = { viewModel.toggleFavorite(state.currentTrack!!.id) },
                            onPlayNext = { viewModel.playNext(state.currentTrack!!) },
                            onAddToQueue = { viewModel.addToQueue(state.currentTrack!!) },
                            progressMs = state.progressMs,
                            durationMs = state.durationMs,
                            repeatMode = state.repeatMode,
                            shuffleModeEnabled = state.shuffleModeEnabled,
                            playbackSpeed = state.playbackSpeed,
                            currentLyrics = state.currentLyrics,
                            syncedLyrics = state.currentSyncedLyrics,
                            activeLyricIndex = state.activeLyricIndex,
                            isFetchingLyrics = state.isFetchingLyrics,
                            lyricsFetchMessage = state.lyricsFetchMessage,
                            isAutoScrollLyrics = state.isAutoScrollLyrics,
                            lyricsViewMode = state.lyricsViewMode,
                    syncedLyricsDisplayStyle = state.syncedLyricsDisplayStyle,
                    onSetSyncedLyricsDisplayStyle = { viewModel.setSyncedLyricsDisplayStyle(it) },
                            isEditingLyrics = state.isEditingLyrics,
                            isEditingMetadata = state.isEditingMetadata,
                            sleepTimerActive = state.sleepTimerActive,
                            sleepTimerRemainingMs = state.sleepTimerRemainingMs,
                            isEqEnabled = state.isEqEnabled,
                            eqBands = state.eqBands,
                            eqPresets = state.eqPresets,
                            currentEqPreset = state.currentEqPreset,
                            minEqLevel = state.minEqLevel,
                            maxEqLevel = state.maxEqLevel,
                            visualizerMode = state.visualizerMode,
                            visualizerTheme = state.visualizerTheme,
                            visualizerSensitivity = state.visualizerSensitivity,
                            isDarkTheme = state.isDarkTheme,
                            onClose = { viewModel.dismissBottomSheet() },
                            onOpenFullScreenLyrics = { viewModel.openFullScreenLyrics() },
                            onOpenQueue = { viewModel.openQueue() },
                            onAddToPlaylist = { viewModel.openAddToPlaylist(state.currentTrack!!) },
                            onSaveLyrics = { viewModel.saveLyrics(it) },
                            onSetEditingLyrics = { viewModel.setEditingLyrics(it) },
                            onSeekToPosition = { viewModel.seekToPosition(it) },
                            onFetchLyrics = { queryTitle, queryArtist -> viewModel.fetchLyricsForCurrentTrack(queryTitle, queryArtist) },
                            onToggleAutoScroll = { viewModel.toggleAutoScrollLyrics(it) },
                            onSetLyricsViewMode = { viewModel.setLyricsViewMode(it) },
                            onClearLyricsMessage = { viewModel.clearLyricsFetchMessage() },
                            onSaveMetadata = { newTitle, newArtist -> viewModel.saveMetadataOverride(newTitle, newArtist) },
                            onSetEditingMetadata = { viewModel.setEditingMetadata(it) },
                            onPlayPause = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.skipNext() },
                            onSkipPrevious = { viewModel.skipPrevious() },
                            onToggleRepeat = { viewModel.toggleRepeatMode() },
                            onToggleShuffle = { viewModel.toggleShuffleMode() },
                            onSetPlaybackSpeed = { viewModel.setPlaybackSpeed(it) },
                            onSetSleepTimer = { viewModel.setSleepTimer(it) },
                            onCancelSleepTimer = { viewModel.cancelSleepTimer() },
                            onSetEqEnabled = { viewModel.setEqEnabled(it) },
                            onSetEqBandLevel = { bandId, level -> viewModel.setEqBandLevel(bandId, level) },
                            onUseEqPreset = { viewModel.useEqPreset(it) },
                            onSeek = { progressFraction -> viewModel.seekTo(progressFraction) },
                            onOpenImportLyrics = { viewModel.openLyricsImport() }
                        )
                    }
                }
            }

            // Video Player Fullscreen / Modal
            if (state.isVideoPlayerOpen && state.currentTrack != null) {
                val (title, artist) = getTitleArtist(state.currentTrack!!, state.mediaMetadata)
                VideoPlayerModal(
                    track = state.currentTrack!!,
                    displayTitle = title,
                    displayArtist = artist,
                    player = viewModel.getPlayer(),
                    isPlaying = state.isPlaying,
                    progressMs = state.progressMs,
                    durationMs = state.durationMs,
                    playbackSpeed = state.playbackSpeed,
                    isFullscreen = state.isVideoFullscreen,
                    lockLandscape = state.videoPrefs.lockLandscapeOrientation,
                    onClose = { viewModel.closeVideoPlayer() },
                    onToggleFullscreen = { viewModel.toggleVideoFullscreen() },
                    onPlayPause = { viewModel.togglePlayPause() },
                    onSeek = { fraction -> viewModel.seekTo(fraction) },
                    onSeekRelative = { offsetMs -> viewModel.seekRelative(offsetMs) },
                    onSetPlaybackSpeed = { speed -> viewModel.setPlaybackSpeed(speed) },
                    onOpenVideoSettings = {
                        viewModel.closeVideoPlayer()
                        viewModel.navigateToSettingsSubPage(SettingsSubPage.VIDEO)
                    }
                )
            }

            // Full-screen Lyrics Modal / Screen
            if (state.isFullScreenLyricsOpen && state.currentTrack != null) {
                val (title, artist) = getTitleArtist(state.currentTrack!!, state.mediaMetadata)
                FullScreenLyricsScreen(
                    track = state.currentTrack!!,
                    displayTitle = title,
                    displayArtist = artist,
                    lyrics = state.currentLyrics,
                    syncedLyrics = state.currentSyncedLyrics,
                    activeLyricIndex = state.activeLyricIndex,
                    isPlaying = state.isPlaying,
                    progressMs = state.progressMs,
                    isFetchingLyrics = state.isFetchingLyrics,
                    lyricsFetchMessage = state.lyricsFetchMessage,
                    isAutoScrollLyrics = state.isAutoScrollLyrics,
                    lyricsViewMode = state.lyricsViewMode,
                    syncedLyricsDisplayStyle = state.syncedLyricsDisplayStyle,
                    onSetSyncedLyricsDisplayStyle = { viewModel.setSyncedLyricsDisplayStyle(it) },
                    lyricsFontSize = state.lyricsFontSize,
                    lyricsAlignment = state.lyricsAlignment,
                    lyricsScrollSpeed = state.lyricsScrollSpeed,
                    isDarkTheme = state.isDarkTheme,
                    onClose = { viewModel.closeFullScreenLyrics() },
                    onSaveLyrics = { viewModel.saveLyrics(it) },
                    onEdit = { viewModel.setEditingLyrics(true) },
                    onSeekToPosition = { viewModel.seekToPosition(it) },
                    onFetchLyrics = { qTitle, qArtist -> viewModel.fetchLyricsForCurrentTrack(qTitle, qArtist) },
                    onToggleAutoScroll = { viewModel.toggleAutoScrollLyrics(it) },
                    onSetLyricsViewMode = { viewModel.setLyricsViewMode(it) },
                    onClearLyricsMessage = { viewModel.clearLyricsFetchMessage() },
                    onOpenImportLyrics = { viewModel.openLyricsImport() },
                    onOpenSyncStudio = { viewModel.openSyncStudio() }
                )
            }

            // Lyrics Import Modal
            if (state.isLyricsImportOpen) {
                val importTrack = state.editingLyricsTrack ?: state.currentTrack
                if (importTrack != null) {
                    val (title, artist) = getTitleArtist(importTrack, if (importTrack == state.currentTrack) state.mediaMetadata else null)
                    LyricsImportModal(
                        track = importTrack,
                        displayTitle = title,
                        displayArtist = artist,
                        onDismiss = { viewModel.closeLyricsImport() },
                        onImportLyrics = { lyrics, source -> viewModel.importLyrics(lyrics, source) },
                        onImportFromUri = { uri -> viewModel.importLyricsFromUri(context, uri) },
                        onFetchFromUrl = { url -> viewModel.importLyricsFromUrl(url) },
                        isFetching = state.isFetchingLyrics,
                        feedbackMessage = state.lyricsFetchMessage,
                        onClearFeedbackMessage = { viewModel.clearLyricsFetchMessage() }
                    )
                }
            }

            // Edit Lyrics Dialog
            val editingTargetTrack = state.editingLyricsTrack ?: state.currentTrack
            if (state.isEditingLyrics && editingTargetTrack != null) {
                val (title, artist) = getTitleArtist(editingTargetTrack, if (editingTargetTrack == state.currentTrack) state.mediaMetadata else null)
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { viewModel.setEditingLyrics(false) },
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    EditLyricsScreen(
                        currentLyrics = state.currentLyrics,
                        progressMs = if (editingTargetTrack == state.currentTrack) state.progressMs else 0L,
                        displayTitle = title,
                        displayArtist = artist,
                        isFetchingLyrics = state.isFetchingLyrics,
                        lyricsFetchMessage = state.lyricsFetchMessage,
                        onSave = { viewModel.saveLyrics(it) },
                        onCancel = { viewModel.setEditingLyrics(false) },
                        onFetchLyrics = { qTitle, qArtist -> viewModel.fetchLyricsForCurrentTrack(qTitle, qArtist) },
                        onClearLyricsMessage = { viewModel.clearLyricsFetchMessage() },
                        onOpenImportLyrics = { viewModel.openLyricsImport(editingTargetTrack) },
                        onOpenSyncStudio = { viewModel.openSyncStudio() }
                    )
                }
            }

            // Sync Studio Modal
            if (state.isSyncStudioOpen && state.currentTrack != null) {
                val (title, artist) = getTitleArtist(state.currentTrack!!, state.mediaMetadata)
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { viewModel.closeSyncStudio() },
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    SyncLyricsScreen(
                        track = state.currentTrack!!,
                        displayTitle = title,
                        displayArtist = artist,
                        mediaMetadata = state.mediaMetadata,
                        rawLyrics = state.currentLyrics,
                        progressMs = state.progressMs,
                        durationMs = state.durationMs,
                        isPlaying = state.isPlaying,
                        onPlayPause = { viewModel.togglePlayPause() },
                        onSeekTo = { viewModel.seekToPosition(it) },
                        onSaveSyncedLyrics = { 
                            viewModel.saveLyrics(it)
                            viewModel.closeSyncStudio() 
                        },
                        onClose = { viewModel.closeSyncStudio() }
                    )
                }
            }

            if (state.isAddStreamOpen) {
                AddStreamDialog(
                    initialUrl = "https://suno.com/@technicalrave7605",
                    initialArtist = "TechnicalRave",
                    onDismiss = { viewModel.closeAddStream() },
                    onAddSunoProfile = { profileUrlOrHandle ->
                        viewModel.importSunoProfile(profileUrlOrHandle)
                    },
                    onAddSingleStream = { url, title, artist, artworkUrl, isVideo ->
                        viewModel.addCustomStreamTrack(url, title, artist, artworkUrl, isVideo)
                    }
                )
            }

            if (state.trackToAddToPlaylist != null) {
                AddToPlaylistDialog(
                    track = state.trackToAddToPlaylist!!,
                    playlists = state.playlists,
                    onDismiss = { viewModel.closeAddToPlaylist() },
                    onAddToPlaylist = { playlistId ->
                        viewModel.addTrackToPlaylist(playlistId, state.trackToAddToPlaylist!!)
                    },
                    onCreatePlaylistAndAdd = { name, desc ->
                        viewModel.createPlaylistAndAddTrack(name, desc, state.trackToAddToPlaylist!!)
                    }
                )
            }

            if (state.isQueueOpen) {
                QueueModal(
                    state = state,
                    viewModel = viewModel,
                    getTitleArtist = getTitleArtist,
                    onDismiss = { viewModel.closeQueue() }
                )
            }

            if (state.isThemeModalOpen) {
                ThemeModalDialog(
                    state = state,
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeThemeModal() }
                )
            }

            if (state.showLyricsSearchModal) {
                val targetTrack = state.editingLyricsTrack ?: state.currentTrack
                val (title, artist) = if (targetTrack != null) getTitleArtist(targetTrack, state.mediaMetadata) else Pair("Audio", "Artist")
                LyricsSearchModalDialog(
                    track = targetTrack,
                    displayTitle = title,
                    displayArtist = artist,
                    candidates = state.lyricsCandidates,
                    isSearching = state.isSearchingLyricsCandidates,
                    onDismiss = { viewModel.closeLyricsSearchModal() },
                    onSearch = { query -> viewModel.searchLyricsCandidates(query) },
                    onAutoCleanSearch = { viewModel.autoCleanSearchLyricsCandidates() },
                    onApplyCandidate = { cand -> viewModel.applyLyricsCandidate(cand) },
                    onGenerateAiLyrics = {
                        viewModel.closeLyricsSearchModal()
                        if (targetTrack != null) {
                            viewModel.generateAiLyricsForTrack(targetTrack)
                        }
                    }
                )
            }
        }
        } // Closing for Scaffold
        } // Closing for BoxWithConstraints
        } // Closing for if (showSplash) else { ... }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onPlayNextClick: (() -> Unit)? = null,
    onAddToQueueClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onEditLyricsClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onAddToPlaylistClick: (() -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) com.example.ui.theme.SurfaceGlass else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingContent != null) {
                leadingContent()
                Spacer(modifier = Modifier.width(10.dp))
            }

            TrackThumbnail(
                artworkUrl = track.artworkUrl,
                coverColorStr = track.coverColorStr,
                size = 48.dp,
                shape = RoundedCornerShape(12.dp),
                isVideo = track.isVideo,
                title = displayTitle,
                artist = displayArtist
            )
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (track.isVideo) {
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "VIDEO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = displayArtist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (trailingContent != null) {
                trailingContent()
                Spacer(modifier = Modifier.width(4.dp))
            }

            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Playing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            val hasMenu = onPlayNextClick != null || onAddToQueueClick != null || onEditClick != null || onEditLyricsClick != null || onDeleteClick != null || onAddToPlaylistClick != null
            if (hasMenu) {
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Song options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (onPlayNextClick != null) {
                            DropdownMenuItem(
                                text = { Text("Play Next") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.QueueMusic,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onPlayNextClick()
                                }
                            )
                        }
                        if (onAddToQueueClick != null) {
                            DropdownMenuItem(
                                text = { Text("Add to Queue") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.PlaylistAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onAddToQueueClick()
                                }
                            )
                        }
                        if (onAddToPlaylistClick != null) {
                            DropdownMenuItem(
                                text = { Text("Add to Playlist") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.PlaylistAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onAddToPlaylistClick()
                                }
                            )
                        }
                        if (onEditClick != null) {
                            DropdownMenuItem(
                                text = { Text("Edit Song Info") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                }
                            )
                        }
                        if (onEditLyricsClick != null) {
                            DropdownMenuItem(
                                text = { Text("Edit Lyrics") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.QueueMusic,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEditLyricsClick()
                                }
                            )
                        }
                        if (onFavoriteClick != null) {
                            DropdownMenuItem(
                                text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
                                leadingIcon = {
                                    Icon(
                                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onFavoriteClick()
                                }
                            )
                        }
                        if (onDeleteClick != null) {
                            DropdownMenuItem(
                                text = { Text("Delete Song", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.DeleteOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    isFavorite: Boolean = false,
    progressMs: Long = 0L,
    durationMs: Long = 0L,
    onToggleFavorite: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onSkipNext: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onClick: () -> Unit = {},
    isDarkTheme: Boolean = true
) {
    var isCollapsed by remember { mutableStateOf(false) }
    val artworkUri = mediaMetadata?.artworkUri?.toString() ?: track.artworkUrl
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mini_player_scale"
    )

    val progressFraction = if (durationMs > 0) (progressMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = "mini_player_progress"
    )

    FrostedGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { if (!isCollapsed) onClick() else isCollapsed = false }
            .testTag("mini_player_container"),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = if (isDarkTheme) FrostedGlassDefaults.GlassDarkElevated else Color.White.copy(alpha = 0.92f),
        backgroundBrush = FrostedGlassDefaults.glassGradient(
            topColor = if (isDarkTheme) Color(0xF21C223A) else Color(0xF2FFFFFF),
            bottomColor = if (isDarkTheme) Color(0xD9101322) else Color(0xD9F3F4F6)
        ),
        borderBrush = FrostedGlassDefaults.glassBorderBrush(
            topColor = if (isDarkTheme) Color.White.copy(alpha = 0.28f) else Color.Black.copy(alpha = 0.08f),
            bottomColor = if (isDarkTheme) Color.White.copy(alpha = 0.06f) else Color.Transparent
        ),
        borderWidth = 1.dp,
        elevation = 12.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Sleek real-time progress bar along the top edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                if (artworkUri != null) {
                    AsyncImage(
                        model = artworkUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .blur(36.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                if (isDarkTheme) Color(0xCC0E111F)
                                else Color(0xCCFFFFFF)
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(getArtworkBackgroundGradient(track.coverColorStr, isDarkTheme = isDarkTheme))
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = if (isCollapsed) 6.dp else 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close button on left
                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier
                            .size(if (isCollapsed) 28.dp else 34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                            .testTag("close_mini_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close player",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(if (isCollapsed) 14.dp else 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (!isCollapsed) {
                        TrackThumbnail(
                            artworkUrl = artworkUri,
                            coverColorStr = track.coverColorStr,
                            size = 44.dp,
                            shape = RoundedCornerShape(12.dp),
                            isVideo = track.isVideo,
                            title = displayTitle,
                            artist = displayArtist
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Metadata
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onClick() }
                    ) {
                        Text(
                            text = displayTitle,
                            style = if (isCollapsed) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!isCollapsed) {
                            Text(
                                text = displayArtist,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (!isCollapsed) {
                        // Favorite Toggle
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFavorite) Color(0xFFF43F5E).copy(alpha = 0.12f)
                                    else Color.Transparent
                                )
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Skip Previous
                        IconButton(
                            onClick = onSkipPrevious,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = "Previous track",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Glowing Play/Pause button
                    Box(
                        modifier = Modifier
                            .size(if (isCollapsed) 32.dp else 40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                FrostedGlassDefaults.glassBorderBrush(
                                    topColor = Color.White.copy(alpha = 0.4f),
                                    bottomColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ),
                                CircleShape
                            )
                            .clickable { onPlayPauseClick() }
                            .testTag("mini_player_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(if (isCollapsed) 16.dp else 22.dp)
                        )
                    }

                    if (!isCollapsed) {
                        // Skip Next
                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "Next track",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
enum class NowPlayingTab { ARTWORK, VISUALIZER, LYRICS, EQUALIZER }

@Composable
fun NowPlayingScreen(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onPlayNext: () -> Unit = {},
    onAddToQueue: () -> Unit = {},
    progressMs: Long,
    durationMs: Long,
    repeatMode: Int,
    shuffleModeEnabled: Boolean,
    playbackSpeed: Float,
    currentLyrics: String,
    syncedLyrics: List<LyricLine>,
    activeLyricIndex: Int,
    isFetchingLyrics: Boolean,
    lyricsFetchMessage: String?,
    isAutoScrollLyrics: Boolean,
    lyricsViewMode: LyricsViewMode,
    syncedLyricsDisplayStyle: SyncedLyricsDisplayStyle = SyncedLyricsDisplayStyle.LIST,
    onSetSyncedLyricsDisplayStyle: (SyncedLyricsDisplayStyle) -> Unit = {},
    isEditingLyrics: Boolean,
    isEditingMetadata: Boolean,
    sleepTimerActive: Boolean,
    sleepTimerRemainingMs: Long,
    isEqEnabled: Boolean,
    eqBands: List<EqBand>,
    eqPresets: List<String>,
    currentEqPreset: Short,
    minEqLevel: Short,
    maxEqLevel: Short,
    visualizerMode: VisualizerMode,
    visualizerTheme: VisualizerTheme,
    visualizerSensitivity: Float,
    isDarkTheme: Boolean = true,
    onClose: () -> Unit = {},
    onOpenFullScreenLyrics: () -> Unit = {},
    onOpenImportLyrics: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onSaveLyrics: (String) -> Unit,
    onSetEditingLyrics: (Boolean) -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onFetchLyrics: (String?, String?) -> Unit,
    onGenerateAiContent: () -> Unit = {},
    onToggleAutoScroll: (Boolean) -> Unit,
    onSetLyricsViewMode: (LyricsViewMode) -> Unit,
    onClearLyricsMessage: () -> Unit,
    onSaveMetadata: (String, String) -> Unit,
    onSetEditingMetadata: (Boolean) -> Unit,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onSetSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onSetEqEnabled: (Boolean) -> Unit,
    onSetEqBandLevel: (Short, Short) -> Unit,
    onUseEqPreset: (Short) -> Unit,
    onSeek: (Float) -> Unit
) {
    val artworkUri = mediaMetadata?.artworkUri?.toString() ?: track.artworkUrl

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    // More Options Modal Sheet
    if (showOptionsMenu) {
        NowPlayingOptionsModal(
            track = track,
            displayTitle = displayTitle,
            displayArtist = displayArtist,
            isEqEnabled = isEqEnabled,
            sleepTimerActive = sleepTimerActive,
            sleepTimerRemainingMs = sleepTimerRemainingMs,
            playbackSpeed = playbackSpeed,
            onDismiss = { showOptionsMenu = false },
            onAddToPlaylist = {
                showOptionsMenu = false
                onAddToPlaylist()
            },
            onPlayNext = {
                showOptionsMenu = false
                onPlayNext()
            },
            onAddToQueue = {
                showOptionsMenu = false
                onAddToQueue()
            },
            onOpenEqualizer = {
                showOptionsMenu = false
                showEqualizerDialog = true
            },
            onOpenSleepTimer = {
                showOptionsMenu = false
                showSleepTimerDialog = true
            },
            onEditMetadata = {
                showOptionsMenu = false
                onSetEditingMetadata(true)
            },
            onFetchLyrics = {
                showOptionsMenu = false
                onFetchLyrics(null, null)
            },
            onOpenFullScreenLyrics = {
                showOptionsMenu = false
                onOpenFullScreenLyrics()
            },
            onOpenSpeedDialog = {
                showOptionsMenu = false
                showSpeedDialog = true
            },
            onGenerateAiContent = {
                showOptionsMenu = false
                onGenerateAiContent()
            }
        )
    }

    // Playback Speed Dialog
    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Playback Speed")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
                    speeds.forEach { speed ->
                        val isSelected = playbackSpeed == speed
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    onSetPlaybackSpeed(speed)
                                    showSpeedDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Done")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
    
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sleep Timer")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (sleepTimerActive) {
                        val remMins = sleepTimerRemainingMs / 60000
                        val remSecs = (sleepTimerRemainingMs % 60000) / 1000
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Timer Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    String.format("%02d:%02d remaining", remMins, remSecs),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    val options = listOf(15, 30, 45, 60)
                    options.forEach { mins ->
                        TextButton(
                            onClick = { 
                                onSetSleepTimer(mins)
                                showSleepTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Text("$mins Minutes", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Row {
                    if (sleepTimerActive) {
                        TextButton(onClick = { 
                            onCancelSleepTimer()
                            showSleepTimerDialog = false
                        }) {
                            Text("Cancel Timer", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { showSleepTimerDialog = false }) {
                        Text("Close")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showEqualizerDialog) {
        AlertDialog(
            onDismissRequest = { showEqualizerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Audio Equalizer")
                }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                    EqualizerComponent(
                        isEnabled = isEqEnabled,
                        bands = eqBands,
                        presets = eqPresets,
                        currentPreset = currentEqPreset,
                        minLevel = minEqLevel,
                        maxLevel = maxEqLevel,
                        onEnabledChange = onSetEqEnabled,
                        onBandLevelChange = onSetEqBandLevel,
                        onPresetSelected = onUseEqPreset,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showEqualizerDialog = false }) {
                    Text("Done")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
    
    if (isEditingMetadata) {
        var editTitle by remember(displayTitle) { mutableStateOf(displayTitle) }
        var editArtist by remember(displayArtist) { mutableStateOf(displayArtist) }
        
        AlertDialog(
            onDismissRequest = { onSetEditingMetadata(false) },
            title = { Text("Edit Song Metadata") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editArtist,
                        onValueChange = { editArtist = it },
                        label = { Text("Artist") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onSaveMetadata(editTitle, editArtist) }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetEditingMetadata(false) }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getArtworkBackgroundGradient(track.coverColorStr, isDarkTheme = isDarkTheme))
            .padding(horizontal = 22.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decluttered Top Navigation Header: Left (Collapse), Center (Title/Badge), Right (Queue + More)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .testTag("close_player_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Collapse Player",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AURA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (pagerState.currentPage) {
                        1 -> "LYRICS"
                        2 -> "VISUALIZER"
                        else -> "NOW PLAYING"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onOpenQueue,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .testTag("player_queue_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.QueueMusic,
                        contentDescription = "Playing Queue",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showOptionsMenu = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .testTag("player_more_options_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Main Viewport (Artwork / Lyrics / Visualizer)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .heightIn(min = 260.dp, max = 320.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
            ) { page ->
                when (page) {
                    0 -> {
                        // Page 0: Artwork
                        NowPlayingArtworkCard(
                            track = track,
                            displayTitle = displayTitle,
                            displayArtist = displayArtist,
                            artworkUri = artworkUri,
                            isPlaying = isPlaying
                        )
                    }
                    1 -> {
                        // Page 1: Lyrics
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(26.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .clickable { onOpenFullScreenLyrics() }
                        ) {
                            LyricsComponent(
                                track = track,
                                displayTitle = displayTitle,
                                displayArtist = displayArtist,
                                lyrics = currentLyrics,
                                syncedLyrics = syncedLyrics,
                                activeLyricIndex = activeLyricIndex,
                                isFetchingLyrics = isFetchingLyrics,
                                lyricsFetchMessage = lyricsFetchMessage,
                                isAutoScrollLyrics = isAutoScrollLyrics,
                                lyricsViewMode = lyricsViewMode,
                                syncedLyricsDisplayStyle = syncedLyricsDisplayStyle,
                                onSetSyncedLyricsDisplayStyle = onSetSyncedLyricsDisplayStyle,
                                isEditing = isEditingLyrics,
                                onSave = onSaveLyrics,
                                onEdit = { onSetEditingLyrics(true) },
                                onCancelEdit = { onSetEditingLyrics(false) },
                                onSeekToPosition = onSeekToPosition,
                                onFetchLyrics = onFetchLyrics,
                                onToggleAutoScroll = onToggleAutoScroll,
                                onSetLyricsViewMode = onSetLyricsViewMode,
                                onClearLyricsMessage = onClearLyricsMessage,
                                onOpenFullScreen = onOpenFullScreenLyrics,
                                onOpenImportLyrics = onOpenImportLyrics,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    2 -> {
                        // Page 2: Premium Visualizer
                        PremiumVisualizerScreen(
                            track = track,
                            displayTitle = displayTitle,
                            displayArtist = displayArtist,
                            isPlaying = isPlaying,
                            progressMs = progressMs,
                            durationMs = durationMs,
                            isEqEnabled = isEqEnabled,
                            eqBands = eqBands,
                            onPlayPause = onPlayPause,
                            visualizerMode = visualizerMode,
                            visualizerTheme = visualizerTheme,
                            visualizerSensitivity = visualizerSensitivity,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Minimalist Smooth Dots Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(5.dp)
                        .width(if (isSelected) 18.dp else 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Track Info & Favorite Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displayArtist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFavorite) Color(0xFFF43F5E).copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                    .testTag("player_favorite_button")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Progress Timeline Scrubber
        val progressFraction = if (durationMs > 0) progressMs.toFloat() / durationMs else 0f
        
        Slider(
            value = progressFraction,
            onValueChange = onSeek,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = com.example.ui.theme.PrimaryUltraviolet,
                activeTrackColor = com.example.ui.theme.PrimaryUltraviolet,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
        )
        
        // Time labels and speed pill underneath progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(progressMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.clickable { showSpeedDialog = true }
            ) {
                Text(
                    text = "${playbackSpeed}x",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Playback Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleShuffle,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("player_shuffle_button")
            ) {
                val tint = if (shuffleModeEnabled) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    modifier = Modifier.size(24.dp),
                    tint = tint
                )
            }
            
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("player_prev_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                com.example.ui.theme.PrimaryUltraviolet,
                                com.example.ui.theme.PrimaryUltravioletVariant
                            )
                        ),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable { onPlayPause() }
                    .testTag("player_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            
            IconButton(
                onClick = onSkipNext,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("player_next_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(
                onClick = onToggleRepeat,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("player_repeat_button")
            ) {
                val icon = when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne
                    else -> Icons.Filled.Repeat
                }
                val tint = if (repeatMode == Player.REPEAT_MODE_OFF) 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                else 
                    MaterialTheme.colorScheme.primary
                    
                Icon(
                    imageVector = icon,
                    contentDescription = "Repeat Mode",
                    modifier = Modifier.size(24.dp),
                    tint = tint
                )
            }
        }

        // Active Status Pill (Equalizer / Sleep timer indicators)
        if (sleepTimerActive || isEqEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sleepTimerActive) {
                    val remMins = sleepTimerRemainingMs / 60000
                    FrostedGlassPill(
                        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        borderBrush = FrostedGlassDefaults.glassBorderBrush(
                            topColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            bottomColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.clickable { showSleepTimerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Timer: ${remMins}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (isEqEnabled) {
                    FrostedGlassPill(
                        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                        borderBrush = FrostedGlassDefaults.glassBorderBrush(
                            topColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            bottomColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.clickable { showEqualizerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "EQ On",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Clean, glassmorphic More Options modal sheet for Now Playing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingOptionsModal(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    isEqEnabled: Boolean,
    sleepTimerActive: Boolean,
    sleepTimerRemainingMs: Long,
    playbackSpeed: Float,
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onEditMetadata: () -> Unit,
    onFetchLyrics: () -> Unit,
    onOpenFullScreenLyrics: () -> Unit,
    onOpenSpeedDialog: () -> Unit,
    onGenerateAiContent: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = FrostedGlassDefaults.GlassDarkElevated,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Track header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrackThumbnail(
                    artworkUrl = track.artworkUrl,
                    coverColorStr = track.coverColorStr,
                    size = 48.dp,
                    shape = RoundedCornerShape(12.dp),
                    title = displayTitle,
                    artist = displayArtist
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = displayArtist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Menu Options
            NowPlayingOptionItem(
                icon = Icons.Filled.QueueMusic,
                title = "Play Next",
                subtitle = "Insert directly after current song",
                onClick = onPlayNext
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.Add,
                title = "Add to Playing Queue",
                subtitle = "Append to bottom of active queue",
                onClick = onAddToQueue
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.PlaylistAdd,
                title = "Add to Playlist",
                subtitle = "Save to a custom or existing playlist",
                onClick = onAddToPlaylist
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.GraphicEq,
                title = "Audio Equalizer",
                subtitle = if (isEqEnabled) "Active" else "Disabled",
                badge = if (isEqEnabled) "ON" else null,
                onClick = onOpenEqualizer
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.Timer,
                title = "Sleep Timer",
                subtitle = if (sleepTimerActive) "${sleepTimerRemainingMs / 60000}m remaining" else "Off",
                badge = if (sleepTimerActive) "ACTIVE" else null,
                onClick = onOpenSleepTimer
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.Speed,
                title = "Playback Speed",
                subtitle = "Currently ${playbackSpeed}x",
                onClick = onOpenSpeedDialog
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.Edit,
                title = "Edit Song Info",
                subtitle = "Modify title and artist tags",
                onClick = onEditMetadata
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.CloudDownload,
                title = "Fetch Synced Lyrics",
                subtitle = "Search LRCLIB database online",
                onClick = onFetchLyrics
            )
            NowPlayingOptionItem(
                icon = Icons.Filled.Star,
                title = if (track.isVideo) "✨ Generate AI Video Captions" else "✨ Generate AI Lyrics",
                subtitle = "Powered by Gemini AI",
                onClick = onGenerateAiContent
            )

            NowPlayingOptionItem(
                icon = Icons.Filled.Fullscreen,
                title = "Fullscreen Lyrics",
                subtitle = "Immersive karaoke mode",
                onClick = onOpenFullScreenLyrics
            )
        }
    }
}

@Composable
private fun NowPlayingOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun LyricsComponent(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    lyrics: String,
    syncedLyrics: List<LyricLine>,
    activeLyricIndex: Int,
    isFetchingLyrics: Boolean,
    lyricsFetchMessage: String?,
    isAutoScrollLyrics: Boolean,
    lyricsViewMode: LyricsViewMode,
    syncedLyricsDisplayStyle: SyncedLyricsDisplayStyle = SyncedLyricsDisplayStyle.LIST,
    onSetSyncedLyricsDisplayStyle: (SyncedLyricsDisplayStyle) -> Unit = {},
    isEditing: Boolean,
    onSave: (String) -> Unit,
    onEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onFetchLyrics: (String?, String?) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onSetLyricsViewMode: (LyricsViewMode) -> Unit,
    onClearLyricsMessage: () -> Unit,
    onOpenFullScreen: (() -> Unit)? = null,
    onOpenImportLyrics: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var editText by remember(lyrics, isEditing) { mutableStateOf(lyrics) }
    var showSearchDialog by remember { mutableStateOf(false) }

    if (showSearchDialog) {
        var queryTitle by remember { mutableStateOf(displayTitle) }
        var queryArtist by remember { mutableStateOf(displayArtist) }

        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Fetch Lyrics Online")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Search LRCLIB for synchronized LRC lyrics:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = queryTitle,
                        onValueChange = { queryTitle = it },
                        label = { Text("Track Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = queryArtist,
                        onValueChange = { queryArtist = it },
                        label = { Text("Artist") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSearchDialog = false
                        onFetchLyrics(queryTitle, queryArtist)
                    }
                ) {
                    Text("Fetch Lyrics")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSearchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(activeLyricIndex, isAutoScrollLyrics) {
        if (isAutoScrollLyrics && activeLyricIndex in syncedLyrics.indices) {
            val targetIndex = (activeLyricIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        // Fetching indicator or message banner
        if (isFetchingLyrics) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        if (lyricsFetchMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = lyricsFetchMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onClearLyricsMessage,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Main content body
        if (lyrics.isBlank()) {
            // Empty state
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "No lyrics available",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Tap to open full screen and add lyrics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (onOpenImportLyrics != null) {
                        Spacer(Modifier.height(8.dp))
                        FilledTonalButton(
                            onClick = onOpenImportLyrics,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileOpen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Import Lyrics")
                        }
                    }
                }
            }
        } else {
            when (lyricsViewMode) {
                LyricsViewMode.SYNCED -> {
                    if (syncedLyrics.isNotEmpty()) {
                                if (syncedLyricsDisplayStyle == SyncedLyricsDisplayStyle.FOCUS || syncedLyricsDisplayStyle == SyncedLyricsDisplayStyle.SINGLE_LINE) {
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        val activeLine = syncedLyrics.getOrNull(activeLyricIndex)
                                        if (activeLine != null) {
                                            Text(
                                                text = activeLine.text.ifBlank { "..." },
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                ),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(24.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "...",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(syncedLyrics) { index, line ->
                                val isActive = index == activeLyricIndex
                                val isPast = index < activeLyricIndex

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSeekToPosition(line.timeMs) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = if (isActive) 12.dp else 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = line.text.ifBlank { "..." },
                                            style = if (isActive) {
                                                MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 18.sp
                                                )
                                            } else {
                                                MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 15.sp
                                                )
                                            },
                                            color = when {
                                                isActive -> MaterialTheme.colorScheme.onPrimaryContainer
                                                isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                            },
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(Modifier.width(8.dp))

                                        Text(
                                            text = LrcParser.formatShortTime(line.timeMs),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isActive) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        }
                    } else {
                        // Synced mode selected, but lyrics only plain text
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "No timestamps found for synchronization",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "These lyrics do not contain LRC timestamps. You can fetch synced lyrics online or view in plain mode.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onFetchLyrics(null, null) }) {
                                    Text("Fetch Synced Online")
                                }
                                OutlinedButton(onClick = { onSetLyricsViewMode(LyricsViewMode.PLAIN) }) {
                                    Text("View Plain Text")
                                }
                            }
                        }
                    }
                }
                LyricsViewMode.PLAIN, LyricsViewMode.EDIT -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = LrcParser.toPlainText(lyrics),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EqualizerComponent(
    isEnabled: Boolean,
    bands: List<EqBand>,
    presets: List<String>,
    currentPreset: Short,
    minLevel: Short,
    maxLevel: Short,
    onEnabledChange: (Boolean) -> Unit,
    onBandLevelChange: (Short, Short) -> Unit,
    onPresetSelected: (Short) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Equalizer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            androidx.compose.material3.Switch(
                checked = isEnabled,
                onCheckedChange = onEnabledChange
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isEnabled) {
            Text("Presets", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                items(presets.size) { index ->
                    val isSelected = currentPreset.toInt() == index
                    Text(
                        text = presets[index],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPresetSelected(index.toShort()) }
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Manual Control", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bands
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bands.forEach { band ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (band.level > 0) "+${band.level / 100}dB" else "${band.level / 100}dB",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .height(150.dp)
                                .width(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Vertical slider hack using graphics layer rotation
                            androidx.compose.material3.Slider(
                                value = band.level.toFloat(),
                                onValueChange = { onBandLevelChange(band.bandId, it.toInt().toShort()) },
                                valueRange = minLevel.toFloat()..maxLevel.toFloat(),
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(40.dp)
                                    .graphicsLayer {
                                        this.rotationZ = -90f
                                        this.transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val freqStr = if (band.centerFreqHz >= 1000000) {
                            "${band.centerFreqHz / 1000000}k"
                        } else {
                            "${band.centerFreqHz / 1000}"
                        }
                        Text(
                            text = freqStr,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Equalizer is disabled",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EditTrackDialog(
    initialTitle: String,
    initialArtist: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, artist: String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var artist by remember { mutableStateOf(initialArtist) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Edit Song Information", 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), artist.trim().ifBlank { "Unknown Artist" })
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun DeleteTrackConfirmDialog(
    trackTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Delete Song", 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Text(
                "Are you sure you want to delete \"$trackTitle\"? This song will be removed from your library and playlists.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

