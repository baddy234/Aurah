package com.example
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.FrostedGlassDefaults
import com.example.ui.ThemeToggleButton
import com.example.ui.frostedGlass
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceGlass



@Composable
fun AuraBrandLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 42.dp,
    isVideoTheme: Boolean = false
) {
    val glowColor = if (isVideoTheme) {
        Color(0xFF00F2FE)
    } else {
        Color(0xFFA855F7)
    }

    Box(
        modifier = modifier
            .size(logoSize)
            .testTag("aura_brand_logo"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient glow behind logo
        Box(
            modifier = Modifier
                .size(logoSize * 1.15f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Image(
            painter = painterResource(id = R.drawable.img_app_logo),
            contentDescription = "Aura App Logo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(logoSize * 0.22f)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun VideoLibraryContent(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Clean Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Modernized Aura Brand Logo
                AuraBrandLogo(logoSize = 34.dp, isVideoTheme = true)

                Column {
                    Text(
                        text = "Aura",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Local & Streaming Video",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Minimal Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = { viewModel.setMainTab(if (state.selectedMainTab == MainTab.AUDIO) MainTab.VIDEO else MainTab.AUDIO) },
                    modifier = Modifier.testTag("toggle_audio_video_button")
                ) {
                    Icon(
                        imageVector = if (state.selectedMainTab == MainTab.AUDIO) Icons.Filled.Movie else Icons.Rounded.MusicNote,
                        contentDescription = if (state.selectedMainTab == MainTab.AUDIO) "Switch to Video Library" else "Switch to Audio Library",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                ThemeToggleButton(
                    isDark = state.isDarkTheme,
                    onToggle = { viewModel.toggleTheme() },
                    onLongClick = { viewModel.openThemeModal() }
                )

                IconButton(
                    onClick = { viewModel.openSettings(SettingsSubPage.VIDEO) },
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Video Player Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            VideosTabPage(state, viewModel, getTitleArtist)
        }
    }
}

@Composable
fun LibraryContent(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Clean Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Modernized Aura Brand Logo
                AuraBrandLogo(logoSize = 34.dp, isVideoTheme = false)

                Column {
                    Text(
                        text = "Aura",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "High Fidelity Audio",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Minimal Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                var showOverflow by remember { mutableStateOf(false) }

                ThemeToggleButton(
                    isDark = state.isDarkTheme,
                    onToggle = { viewModel.toggleTheme() },
                    onLongClick = { viewModel.openThemeModal() }
                )

                IconButton(
                    onClick = {
                        val filter = when {
                            state.selectedArtist != null -> SearchFilter.ARTIST_DETAIL
                            state.viewingPlaylist != null || state.selectedPlaylistId != null -> SearchFilter.PLAYLIST_DETAIL
                            state.selectedLibraryTab == LibraryTab.SONGS -> SearchFilter.SONGS
                            state.selectedLibraryTab == LibraryTab.ARTISTS -> SearchFilter.ARTISTS
                            state.selectedLibraryTab == LibraryTab.PLAYLIST -> SearchFilter.PLAYLISTS
                            state.selectedLibraryTab == LibraryTab.STORAGE -> SearchFilter.DEVICE
                            state.selectedLibraryTab == LibraryTab.HISTORY -> SearchFilter.HISTORY
                            else -> SearchFilter.ALL
                        }
                        viewModel.openSearch(filter)
                    },
                    modifier = Modifier.testTag("search_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.openSettings(SettingsSubPage.PLAYBACK) },
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Audio Player Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    IconButton(
                        onClick = { showOverflow = true },
                        modifier = Modifier.testTag("more_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showOverflow,
                        onDismissRequest = { showOverflow = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Playing Queue") },
                            leadingIcon = { Icon(Icons.Filled.QueueMusic, contentDescription = null) },
                            onClick = {
                                showOverflow = false
                                viewModel.openQueue()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add Custom Stream") },
                            leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
                            onClick = {
                                showOverflow = false
                                viewModel.openAddStream()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (state.isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (state.isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showOverflow = false
                                viewModel.toggleTheme()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Theme & Style...") },
                            leadingIcon = { Icon(Icons.Filled.Palette, contentDescription = null) },
                            onClick = {
                                showOverflow = false
                                viewModel.openThemeModal()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        

        // Scrollable Tabs: Songs, Storage, Playlist, Artists, History
        val tabs = LibraryTab.values()
        val selectedTabIndex = tabs.indexOf(state.selectedLibraryTab).coerceAtLeast(0)

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {},
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(2.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEach { tab ->
                val isSelected = state.selectedLibraryTab == tab
                val icon = when (tab) {
                    LibraryTab.SONGS -> Icons.Rounded.MusicNote
                    LibraryTab.STORAGE -> Icons.Rounded.FolderOpen
                    LibraryTab.PLAYLIST -> Icons.Filled.QueueMusic
                    LibraryTab.ARTISTS -> Icons.Filled.Person
                    LibraryTab.HISTORY -> Icons.Filled.History
                }
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.selectLibraryTab(tab) },
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            maxLines = 1,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }
        }
                Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (state.selectedLibraryTab) {
                LibraryTab.SONGS -> SongsTabPage(state, viewModel, getTitleArtist)
                LibraryTab.STORAGE -> StorageTabPage(state, viewModel, getTitleArtist)
                LibraryTab.PLAYLIST -> PlaylistTabPage(state, viewModel, getTitleArtist)
                LibraryTab.ARTISTS -> ArtistsTabPage(state, viewModel, getTitleArtist)
                LibraryTab.HISTORY -> HistoryTabPage(state, viewModel, getTitleArtist)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. SONGS TAB
// -------------------------------------------------------------
@Composable
fun SongsTabPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>
) {
    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }
    var isFilterModalOpen by remember { mutableStateOf(false) }

    if (isFilterModalOpen) {
        val songSortOptions = listOf(
            FilterOptionItem(
                value = SmartSortOption.TITLE_AZ,
                title = "Title (A to Z)",
                subtitle = "Alphabetical ascending order",
                icon = Icons.Filled.SortByAlpha
            ),
            FilterOptionItem(
                value = SmartSortOption.TITLE_ZA,
                title = "Title (Z to A)",
                subtitle = "Alphabetical descending order",
                icon = Icons.Filled.SortByAlpha
            ),
            FilterOptionItem(
                value = SmartSortOption.ARTIST_AZ,
                title = "Artist (A to Z)",
                subtitle = "Group and order alphabetically by creator",
                icon = Icons.Filled.Person
            ),
            FilterOptionItem(
                value = SmartSortOption.ARTIST_ZA,
                title = "Artist (Z to A)",
                subtitle = "Descending order by creator",
                icon = Icons.Filled.Person
            ),
            FilterOptionItem(
                value = SmartSortOption.NEWEST,
                title = "Recently Added (Newest)",
                subtitle = "Show latest imported tracks first",
                icon = Icons.Filled.Star
            ),
            FilterOptionItem(
                value = SmartSortOption.OLDEST,
                title = "Oldest Added",
                subtitle = "Show earliest imported tracks first",
                icon = Icons.Filled.History
            ),
            FilterOptionItem(
                value = SmartSortOption.DURATION_SHORTEST,
                title = "Duration (Shortest)",
                subtitle = "Shortest tracks first",
                icon = Icons.Filled.Schedule
            ),
            FilterOptionItem(
                value = SmartSortOption.DURATION_LONGEST,
                title = "Duration (Longest)",
                subtitle = "Longest tracks first",
                icon = Icons.Filled.Schedule
            ),
            FilterOptionItem(
                value = SmartSortOption.MOST_PLAYED,
                title = "Most Played",
                subtitle = "Ordered by play count frequency",
                icon = Icons.Filled.Star
            ),
            FilterOptionItem(
                value = SmartSortOption.BPM_ASCENDING,
                title = "Tempo (BPM: Slow to Fast)",
                subtitle = "Ascending musical tempo (warm-up / low to high BPM)",
                icon = Icons.Filled.Speed
            ),
            FilterOptionItem(
                value = SmartSortOption.BPM_DESCENDING,
                title = "Tempo (BPM: Fast to Slow)",
                subtitle = "Descending musical tempo (cooldown / high to low BPM)",
                icon = Icons.Filled.Speed
            )
        )

        FilterModalDialog(
            title = "Smart Sort Songs",
            subtitle = "Choose advanced sorting preference",
            options = songSortOptions,
            selectedOption = state.smartSortOption,
            onSelectOption = { viewModel.setSmartSortOption(it) },
            onDismiss = { isFilterModalOpen = false }
        )
    }

    if (editingTrack != null) {
        val (currentTitle, currentArtist) = getTitleArtist(editingTrack!!, null)
        EditTrackDialog(
            initialTitle = currentTitle,
            initialArtist = currentArtist,
            onDismiss = { editingTrack = null },
            onConfirm = { title, artist ->
                viewModel.editTrack(editingTrack!!.id, title, artist)
                editingTrack = null
            }
        )
    }

    if (deletingTrack != null) {
        val (delTitle, _) = getTitleArtist(deletingTrack!!, null)
        DeleteTrackConfirmDialog(
            trackTitle = delTitle,
            onDismiss = { deletingTrack = null },
            onConfirm = {
                viewModel.deleteTrack(deletingTrack!!.id)
                deletingTrack = null
            }
        )
    }

    val context = LocalContext.current
    val audioPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            viewModel.scanDeviceMusic()
        }
    }

    // Automatically detect audio files on the Songs page
    LaunchedEffect(Unit) {
        val hasAudioPerm = audioPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (hasAudioPerm) {
            viewModel.scanDeviceMusic()
        } else {
            audioPermissionLauncher.launch(audioPermissions)
        }
    }

    val audioOnlyTracks = remember(state.allTracks) {
        state.allTracks.filter { !it.isVideo }
    }

    val displayedTracks = remember(audioOnlyTracks, state.smartSortOption, state.playCounts) {
        when (state.smartSortOption) {
            SmartSortOption.TITLE_AZ -> audioOnlyTracks.sortedBy { getTitleArtist(it, null).first.lowercase() }
            SmartSortOption.TITLE_ZA -> audioOnlyTracks.sortedByDescending { getTitleArtist(it, null).first.lowercase() }
            SmartSortOption.ARTIST_AZ -> audioOnlyTracks.sortedBy { getTitleArtist(it, null).second.lowercase() }
            SmartSortOption.ARTIST_ZA -> audioOnlyTracks.sortedByDescending { getTitleArtist(it, null).second.lowercase() }
            SmartSortOption.NEWEST -> audioOnlyTracks.sortedByDescending { it.id.toLongOrNull() ?: 0L }
            SmartSortOption.OLDEST -> audioOnlyTracks.sortedBy { it.id.toLongOrNull() ?: 0L }
            SmartSortOption.DURATION_SHORTEST -> audioOnlyTracks.sortedBy { it.durationMs }
            SmartSortOption.DURATION_LONGEST -> audioOnlyTracks.sortedByDescending { it.durationMs }
            SmartSortOption.MOST_PLAYED -> audioOnlyTracks.sortedByDescending { state.playCounts[it.id] ?: 0 }
            SmartSortOption.BPM_ASCENDING -> audioOnlyTracks.sortedBy { it.effectiveBpm }
            SmartSortOption.BPM_DESCENDING -> audioOnlyTracks.sortedByDescending { it.effectiveBpm }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (displayedTracks.isNotEmpty()) {
            // Quick Action Bar: Song count + Filter Modal Button + Play All + Shuffle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${displayedTracks.size} songs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Smart Sort modal opener button
                    val isSortedCustom = state.smartSortOption != SmartSortOption.TITLE_AZ
                    IconButton(
                        onClick = { isFilterModalOpen = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSortedCustom) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                }
                            )
                            .testTag("songs_filter_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Smart Sort Songs",
                            tint = if (isSortedCustom) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { viewModel.playTrackList(displayedTracks, 0, shuffle = false) },
                        enabled = displayedTracks.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play all songs",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Play All", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = { viewModel.playTrackList(displayedTracks, 0, shuffle = true) },
                        enabled = displayedTracks.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "Shuffle songs",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Active Sort Pill
            if (state.smartSortOption != SmartSortOption.TITLE_AZ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.clickable { isFilterModalOpen = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Sort: ${state.smartSortOption.title}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { viewModel.setSmartSortOption(SmartSortOption.TITLE_AZ) },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Reset sort",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayedTracks, key = { it.id }) { track ->
                    val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                    val isPlaying = track.id == state.currentTrack?.id && state.isPlaying
                    TrackListItem(
                        track = track,
                        displayTitle = title,
                        displayArtist = artist,
                        isPlaying = isPlaying,
                        onPlayNextClick = { viewModel.playNext(track) },
                        onAddToQueueClick = { viewModel.addToQueue(track) },
                        onEditClick = { editingTrack = track },
                        onEditLyricsClick = {
                            viewModel.openEditLyricsForTrack(track)
                        },
                        onDeleteClick = { deletingTrack = track },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                        onClick = { viewModel.selectTrack(track, displayedTracks) }
                    )
                }
            }
        } else {
            // True vertical & horizontal centering for empty library state
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "No songs in library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Add songs to your library to begin listening.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1.5. VIDEOS TAB
// -------------------------------------------------------------
@Composable
fun VideosTabPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>
) {
    val context = LocalContext.current
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val anyGranted = results.values.any { it }
        if (anyGranted) {
            viewModel.scanDeviceMusic()
        } else {
            viewModel.setScanMessage("Storage permission was denied. You can tap 'Import Video' to choose videos directly.")
        }
    }

    // Automatically detect video files on the Video page
    LaunchedEffect(Unit) {
        val hasVideoPerm = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (hasVideoPerm) {
            viewModel.scanDeviceMusic()
        } else {
            multiplePermissionLauncher.launch(permissionsToRequest)
        }
    }

    // Android System Video Picker (Zero-permission)
    val visualMediaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importAudioUris(uris)
        }
    }

    // SAF Document / File Browser fallback
    val documentPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importAudioUris(uris)
        }
    }

    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }

    if (editingTrack != null) {
        val (currentTitle, currentArtist) = getTitleArtist(editingTrack!!, null)
        EditTrackDialog(
            initialTitle = currentTitle,
            initialArtist = currentArtist,
            onDismiss = { editingTrack = null },
            onConfirm = { title, artist ->
                viewModel.editTrack(editingTrack!!.id, title, artist)
                editingTrack = null
            }
        )
    }

    if (deletingTrack != null) {
        val (delTitle, _) = getTitleArtist(deletingTrack!!, null)
        DeleteTrackConfirmDialog(
            trackTitle = delTitle,
            onDismiss = { deletingTrack = null },
            onConfirm = {
                viewModel.deleteTrack(deletingTrack!!.id)
                deletingTrack = null
            }
        )
    }

    // Collect all video tracks from both device storage & library catalog
    val videoTracks = remember(state.allTracks, state.deviceTracks) {
        (state.allTracks + state.deviceTracks).distinctBy { it.id }.filter { it.isVideo }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Action Bar for Videos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Video Library",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${videoTracks.size} video(s) available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.openSearch(SearchFilter.VIDEOS) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Videos",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                FilledTonalButton(
                    onClick = {
                        try {
                            visualMediaPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        } catch (_: Exception) {
                            documentPickerLauncher.launch(arrayOf("video/*", "*/*"))
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Import Video",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Import Video",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                IconButton(
                    onClick = {
                        val hasPermission = permissionsToRequest.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }
                        if (hasPermission) {
                            viewModel.scanDeviceMusic()
                        } else {
                            multiplePermissionLauncher.launch(permissionsToRequest)
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Scan Storage",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (videoTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Movie,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = "No Videos Found",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Select video files from your device storage or run a scan to detect local videos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                try {
                                    visualMediaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                } catch (_: Exception) {
                                    documentPickerLauncher.launch(arrayOf("video/*", "*/*"))
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Videos")
                        }

                        OutlinedButton(
                            onClick = {
                                val hasPermission = permissionsToRequest.all {
                                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                                }
                                if (hasPermission) {
                                    viewModel.scanDeviceMusic()
                                } else {
                                    multiplePermissionLauncher.launch(permissionsToRequest)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Storage")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(videoTracks, key = { it.id }) { track ->
                    val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                    val isPlaying = track.id == state.currentTrack?.id && state.isPlaying

                    TrackListItem(
                        track = track,
                        displayTitle = title,
                        displayArtist = artist,
                        isPlaying = isPlaying,
                        onPlayNextClick = { viewModel.playNext(track) },
                        onAddToQueueClick = { viewModel.addToQueue(track) },
                        onEditClick = { editingTrack = track },
                        onDeleteClick = { deletingTrack = track },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                        onClick = { viewModel.selectTrack(track, videoTracks) }
                    )
                }
            }
        }
    }
}


@Composable
fun VideoCardItem(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    isPlaying: Boolean,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val coverUrl = track.artworkUrl ?: CoverImageProvider.getVideoFallbackCover(track.title, track.artist)
    val durationFormatted = remember(track.durationMs) {
        if (track.durationMs > 0) {
            val totalSec = track.durationMs / 1000
            val m = totalSec / 60
            val s = totalSec % 60
            String.format("%02d:%02d", m, s)
        } else "HD"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 16:9 Widescreen Video Thumbnail with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = displayTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark Gradient Scrim Overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                // Resolution Badge in Top Left
                val resBadge = track.resolution ?: if (track.width != null && track.height != null && track.width > 0) "${track.width}x${track.height}" else "HD"
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = resBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                // Play Button Center Overlay
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.55f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Playing" else "Play Video",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Duration Badge in Bottom Right
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = durationFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Video Info and Actions Below Thumbnail
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (!track.folderName.isNullOrBlank()) "${track.folderName} • $displayArtist" else displayArtist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // More Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Play Next") },
                            leadingIcon = { Icon(Icons.Filled.QueueMusic, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onPlayNextClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add to Playing Queue") },
                            leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAddToQueueClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add to Playlist") },
                            leadingIcon = { Icon(Icons.Filled.PlaylistAdd, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAddToPlaylistClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Title & Artist") },
                            leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Video", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
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

// -------------------------------------------------------------
// 2. STORAGE TAB (Dedicated Device Storage & Folder Explorer)
// -------------------------------------------------------------
enum class StorageDisplayMode {
    FOLDERS, ALL_FILES
}

enum class MediaKindFilter {
    ALL, AUDIO_ONLY, VIDEO_ONLY
}

fun formatFolderDuration(durationMs: Long): String {
    if (durationMs <= 0L) return ""
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return if (hours > 0) {
        "${hours}h ${remainingMinutes}m"
    } else {
        "${minutes}m ${seconds}s"
    }
}

@Composable
fun StorageTabPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>
) {
    val context = LocalContext.current
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val anyGranted = results.values.any { it }
        if (anyGranted) {
            viewModel.scanDeviceMusic()
        } else {
            viewModel.setScanMessage("Storage permission denied. You can still tap 'Import' to choose files directly.")
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importAudioUris(uris)
        }
    }

    val visualMediaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importAudioUris(uris)
        }
    }

    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }

    var selectedFolderId by remember { mutableStateOf<String?>(null) }
    var displayMode by remember { mutableStateOf(StorageDisplayMode.FOLDERS) }
    var kindFilter by remember { mutableStateOf(MediaKindFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showImportMenu by remember { mutableStateOf(false) }

    // Group device tracks dynamically into folders
    val folders = remember(state.deviceTracks) {
        state.deviceTracks
            .groupBy { it.folderName ?: if (it.isVideo) "Videos" else "Music" }
            .map { (folderName, folderTracks) ->
                val firstPath = folderTracks.firstNotNullOfOrNull { it.folderPath }
                MediaFolder(
                    id = folderName.lowercase().replace(" ", "_"),
                    name = folderName,
                    path = firstPath ?: "/storage/emulated/0/$folderName",
                    tracks = folderTracks
                )
            }
            .sortedByDescending { it.tracks.size }
    }

    val currentSelectedFolder = remember(folders, selectedFolderId) {
        folders.find { it.id == selectedFolderId }
    }

    val allAudioTracks = remember(state.deviceTracks) { state.deviceTracks.filter { !it.isVideo } }
    val allVideoTracks = remember(state.deviceTracks) { state.deviceTracks.filter { it.isVideo } }

    if (editingTrack != null) {
        val (currentTitle, currentArtist) = getTitleArtist(editingTrack!!, null)
        EditTrackDialog(
            initialTitle = currentTitle,
            initialArtist = currentArtist,
            onDismiss = { editingTrack = null },
            onConfirm = { title, artist ->
                viewModel.editTrack(editingTrack!!.id, title, artist)
                editingTrack = null
            }
        )
    }

    if (deletingTrack != null) {
        val (delTitle, _) = getTitleArtist(deletingTrack!!, null)
        DeleteTrackConfirmDialog(
            trackTitle = delTitle,
            onDismiss = { deletingTrack = null },
            onConfirm = {
                viewModel.deleteTrack(deletingTrack!!.id)
                deletingTrack = null
            }
        )
    }

    var showStorageActionsModal by remember { mutableStateOf(false) }

    if (showStorageActionsModal) {
        StorageActionsModal(
            onDismiss = { showStorageActionsModal = false },
            onScanStorage = {
                val hasPermission = permissionsToRequest.all {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                if (hasPermission) {
                    viewModel.scanDeviceMusic()
                } else {
                    permissionLauncher.launch(permissionsToRequest)
                }
            },
            onImportAudio = {
                documentPickerLauncher.launch(arrayOf("audio/*", "video/*"))
            },
            onImportVideo = {
                documentPickerLauncher.launch(arrayOf("video/*"))
            },
            onStreamAudio = {
                viewModel.openAddStream()
            }
        )
    }

    LaunchedEffect(state.storageScanMessage) {
        if (state.storageScanMessage != null) {
            delay(4000L)
            viewModel.dismissScanMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 0.dp, vertical = 8.dp)) {

        if (state.isScanningDevice) {
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        if (state.storageScanMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.storageScanMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.dismissScanMessage() },
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

        // Main Content Area: Folder Detail OR Folders List OR All Files
        if (currentSelectedFolder != null) {
            // FOLDER DETAIL VIEW
            FolderDetailView(
                folder = currentSelectedFolder,
                state = state,
                viewModel = viewModel,
                getTitleArtist = getTitleArtist,
                onBack = { selectedFolderId = null },
                onEditTrack = { editingTrack = it },
                onDeleteTrack = { deletingTrack = it }
            )
        } else if (displayMode == StorageDisplayMode.FOLDERS) {
            // FOLDERS VIEW (LIST OF FOLDERS CONTAINING MEDIA FILES)
            val filteredFolders = remember(folders, kindFilter, searchQuery) {
                folders.mapNotNull { folder ->
                    val kindFiltered = when (kindFilter) {
                        MediaKindFilter.ALL -> folder.tracks
                        MediaKindFilter.AUDIO_ONLY -> folder.tracks.filter { !it.isVideo }
                        MediaKindFilter.VIDEO_ONLY -> folder.tracks.filter { it.isVideo }
                    }
                    if (kindFiltered.isEmpty()) return@mapNotNull null

                    val searchFiltered = if (searchQuery.isNotBlank()) {
                        val matchesFolder = folder.name.contains(searchQuery, ignoreCase = true) ||
                            (folder.path?.contains(searchQuery, ignoreCase = true) == true)
                        if (matchesFolder) {
                            kindFiltered
                        } else {
                            kindFiltered.filter {
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                it.artist.contains(searchQuery, ignoreCase = true)
                            }
                        }
                    } else {
                        kindFiltered
                    }

                    if (searchFiltered.isEmpty()) null
                    else folder.copy(tracks = searchFiltered)
                }
            }

            if (filteredFolders.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 6.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Device Folders (${filteredFolders.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap folder to explore",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(filteredFolders, key = { it.id }) { folder ->
                        FolderCardItem(
                            folder = folder,
                            onFolderClick = { selectedFolderId = folder.id },
                            onPlayFolder = { viewModel.playTrackList(folder.tracks, 0, shuffle = false) },
                            onShuffleFolder = { viewModel.playTrackList(folder.tracks, 0, shuffle = true) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                StorageEmptyView(
                    isSearch = searchQuery.isNotBlank(),
                    onScan = {
                        val hasPermission = permissionsToRequest.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }
                        if (hasPermission) {
                            viewModel.scanDeviceMusic()
                        } else {
                            permissionLauncher.launch(permissionsToRequest)
                        }
                    },
                    onImport = { documentPickerLauncher.launch(arrayOf("audio/*", "video/*")) }
                )
            }
        } else {
            // ALL FILES VIEW
            val filteredAllTracks = remember(state.deviceTracks, kindFilter, searchQuery) {
                var list = when (kindFilter) {
                    MediaKindFilter.ALL -> state.deviceTracks
                    MediaKindFilter.AUDIO_ONLY -> state.deviceTracks.filter { !it.isVideo }
                    MediaKindFilter.VIDEO_ONLY -> state.deviceTracks.filter { it.isVideo }
                }
                if (searchQuery.isNotBlank()) {
                    list = list.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true) ||
                        (it.folderName?.contains(searchQuery, ignoreCase = true) == true)
                    }
                }
                list
            }

            if (filteredAllTracks.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${filteredAllTracks.size} files",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { viewModel.playTrackList(filteredAllTracks, 0, shuffle = false) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play All", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = { viewModel.playTrackList(filteredAllTracks, 0, shuffle = true) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAllTracks, key = { it.id }) { track ->
                        val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                        val isPlaying = track.id == state.currentTrack?.id && state.isPlaying
                        TrackListItem(
                            track = track,
                            displayTitle = title,
                            displayArtist = if (!track.folderName.isNullOrBlank()) "$artist • 📁 ${track.folderName}" else artist,
                            isPlaying = isPlaying,
                            onEditClick = { editingTrack = track },
                            onEditLyricsClick = { viewModel.openEditLyricsForTrack(track) },
                            onDeleteClick = { deletingTrack = track },
                            onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                            onClick = { viewModel.selectTrack(track, filteredAllTracks) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                StorageEmptyView(
                    isSearch = searchQuery.isNotBlank(),
                    onScan = {
                        val hasPermission = permissionsToRequest.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }
                        if (hasPermission) {
                            viewModel.scanDeviceMusic()
                        } else {
                            permissionLauncher.launch(permissionsToRequest)
                        }
                    },
                    onImport = { documentPickerLauncher.launch(arrayOf("audio/*", "video/*")) }
                )
            }
        }
    }
}

@Composable
fun FolderCardItem(
    folder: MediaFolder,
    onFolderClick: () -> Unit,
    onPlayFolder: () -> Unit,
    onShuffleFolder: () -> Unit
) {
    val isMostlyVideo = folder.videoCount > folder.audioCount
    val folderIcon = if (isMostlyVideo) Icons.Rounded.VideoLibrary else Icons.Rounded.Folder
    val primaryColor = if (isMostlyVideo) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onFolderClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Glowing Folder Icon Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                primaryColor.copy(alpha = 0.28f),
                                primaryColor.copy(alpha = 0.10f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = folderIcon,
                    contentDescription = folder.name,
                    tint = primaryColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Folder details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Folder stats line
                val breakdownStr = buildString {
                    append("${folder.trackCount} files")
                    if (folder.audioCount > 0 && folder.videoCount > 0) {
                        append(" • ${folder.audioCount} audio, ${folder.videoCount} video")
                    } else if (folder.videoCount > 0) {
                        append(" • ${folder.videoCount} videos")
                    } else {
                        append(" • ${folder.audioCount} audio")
                    }
                    val durStr = formatFolderDuration(folder.totalDurationMs)
                    if (durStr.isNotBlank()) {
                        append(" • $durStr")
                    }
                }

                Text(
                    text = breakdownStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                if (!folder.path.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }

            // Quick Play & Shuffle Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onPlayFolder,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play folder",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onShuffleFolder,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Shuffle folder",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Explore folder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun FolderDetailView(
    folder: MediaFolder,
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>,
    onBack: () -> Unit,
    onEditTrack: (Track) -> Unit,
    onDeleteTrack: (Track) -> Unit
) {
    var filterType by remember { mutableStateOf(MediaKindFilter.ALL) }
    var folderSearchQuery by remember { mutableStateOf("") }

    val filteredTracks = remember(folder.tracks, filterType, folderSearchQuery) {
        var list = when (filterType) {
            MediaKindFilter.ALL -> folder.tracks
            MediaKindFilter.AUDIO_ONLY -> folder.tracks.filter { !it.isVideo }
            MediaKindFilter.VIDEO_ONLY -> folder.tracks.filter { it.isVideo }
        }
        if (folderSearchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(folderSearchQuery, ignoreCase = true) ||
                it.artist.contains(folderSearchQuery, ignoreCase = true)
            }
        }
        list
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Folders",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("All Folders", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Folder Hero Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val isMostlyVideo = folder.videoCount > folder.audioCount
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMostlyVideo) Icons.Rounded.VideoLibrary else Icons.Rounded.Folder,
                            contentDescription = folder.name,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = folder.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${folder.trackCount} files • ${formatFolderDuration(folder.totalDurationMs)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!folder.path.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = folder.path,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Play All & Shuffle Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.playTrackList(filteredTracks, 0, shuffle = false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play All (${filteredTracks.size})", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { viewModel.playTrackList(filteredTracks, 0, shuffle = true) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Filled.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shuffle", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Folder search & kind filters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = folderSearchQuery,
                onValueChange = { folderSearchQuery = it },
                placeholder = { Text("Search in ${folder.name}...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                trailingIcon = {
                    if (folderSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { folderSearchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            if (folder.audioCount > 0 && folder.videoCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterType == MediaKindFilter.ALL,
                        onClick = { filterType = MediaKindFilter.ALL },
                        label = { Text("All (${folder.trackCount})", style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    FilterChip(
                        selected = filterType == MediaKindFilter.AUDIO_ONLY,
                        onClick = { filterType = MediaKindFilter.AUDIO_ONLY },
                        label = { Text("Audio (${folder.audioCount})", style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    FilterChip(
                        selected = filterType == MediaKindFilter.VIDEO_ONLY,
                        onClick = { filterType = MediaKindFilter.VIDEO_ONLY },
                        label = { Text("Videos (${folder.videoCount})", style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Tracks List
        if (filteredTracks.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTracks, key = { it.id }) { track ->
                    val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                    val isPlaying = track.id == state.currentTrack?.id && state.isPlaying
                    TrackListItem(
                        track = track,
                        displayTitle = title,
                        displayArtist = artist,
                        isPlaying = isPlaying,
                        onEditClick = { onEditTrack(track) },
                        onEditLyricsClick = { viewModel.openEditLyricsForTrack(track) },
                        onDeleteClick = { onDeleteTrack(track) },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                        onClick = { viewModel.selectTrack(track, filteredTracks) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No files found matching filter in ${folder.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StorageEmptyView(
    isSearch: Boolean,
    onScan: () -> Unit,
    onImport: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = if (isSearch) "No Matching Media Files" else "No Media Folders Found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (isSearch) "Try a different search query."
                    else "Tap 'Scan Storage' to scan your device folders for media files, or 'Import' to pick files directly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (!isSearch) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onScan,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Scan Storage")
                        }
                        FilledTonalButton(
                            onClick = onImport,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Import Files")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. PLAYLIST TAB
// -------------------------------------------------------------
@Composable
fun PlaylistTabPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }
    var isViewingFavorites by remember { mutableStateOf(false) }

    val favoriteTracks = remember(state.allTracks, state.favoriteTrackIds) {
        state.allTracks.filter { state.favoriteTrackIds.contains(it.id) && !it.isVideo }
    }

    if (editingTrack != null) {
        val (currentTitle, currentArtist) = getTitleArtist(editingTrack!!, null)
        EditTrackDialog(
            initialTitle = currentTitle,
            initialArtist = currentArtist,
            onDismiss = { editingTrack = null },
            onConfirm = { title, artist ->
                viewModel.editTrack(editingTrack!!.id, title, artist)
                editingTrack = null
            }
        )
    }

    if (deletingTrack != null) {
        val (delTitle, _) = getTitleArtist(deletingTrack!!, null)
        DeleteTrackConfirmDialog(
            trackTitle = delTitle,
            onDismiss = { deletingTrack = null },
            onConfirm = {
                viewModel.deleteTrack(deletingTrack!!.id)
                deletingTrack = null
            }
        )
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, desc ->
                viewModel.createPlaylist(name, desc)
                showCreateDialog = false
            }
        )
    }

    if (isViewingFavorites) {
        // Detailed Liked Songs / Favorites screen
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isViewingFavorites = false }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back to playlists",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Playlists",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Liked Songs Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFF43F5E),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                )
                                ,
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Liked Songs",
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Liked Songs",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your favorited audio library tracks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${favoriteTracks.size} favorites",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF43F5E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (favoriteTracks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { viewModel.playTrackList(favoriteTracks, 0, shuffle = false) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play all favorites")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Play All", fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.playTrackList(favoriteTracks, 0, shuffle = true) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle favorites")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Shuffle", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Liked songs tracks list or empty state
            if (favoriteTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF43F5E).copy(alpha = 0.15f))
                                    ,
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFF43F5E),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "No Favorites Yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Tap the heart icon on any song or stream to add it to your Liked Songs collection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteTracks) { track ->
                        val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                        val isPlaying = track.id == state.currentTrack?.id && state.isPlaying

                        TrackListItem(
                            track = track,
                            displayTitle = title,
                            displayArtist = artist,
                            isPlaying = isPlaying,
                            isFavorite = true,
                            onFavoriteClick = { viewModel.toggleFavorite(track.id) },
                            onPlayNextClick = { viewModel.playNext(track) },
                            onAddToQueueClick = { viewModel.addToQueue(track) },
                            onEditClick = { editingTrack = track },
                            onEditLyricsClick = {
                                viewModel.openEditLyricsForTrack(track)
                            },
                            onDeleteClick = { deletingTrack = track },
                            onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                            onClick = { viewModel.selectTrack(track, favoriteTracks) }
                        )
                    }
                }
            }
        }
    } else if (state.viewingPlaylist != null) {
        // Detailed custom playlist screen
        val playlist = state.viewingPlaylist
        val playlistAudioTracks = remember(playlist.tracks) {
            playlist.tracks.filter { !it.isVideo }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.viewPlaylistDetails(null) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back to playlists",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Playlists",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Playlist Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val playlistCover = playlist.coverUrl ?: playlist.coverTrack?.artworkUrl ?: CoverImageProvider.getFallbackCover(playlist.name, "Playlist")
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(playlistCover)
                                .crossfade(true)
                                .build(),
                            contentDescription = playlist.name,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = playlist.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${playlistAudioTracks.size} audio tracks",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Playlist Play & Shuffle controls
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { viewModel.playTrackList(playlistAudioTracks, 0, shuffle = false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play Playlist")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play", fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.playTrackList(playlistAudioTracks, 0, shuffle = true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle Playlist")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shuffle", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Playlist tracks
            if (playlistAudioTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                    ,
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QueueMusic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Playlist is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "This playlist has no audio tracks yet.\nAdd songs to listen here!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlistAudioTracks) { track ->
                        val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                        val isPlaying = track.id == state.currentTrack?.id && state.isPlaying
                        val isFavorite = state.favoriteTrackIds.contains(track.id)

                        TrackListItem(
                            track = track,
                            displayTitle = title,
                            displayArtist = artist,
                            isPlaying = isPlaying,
                            isFavorite = isFavorite,
                            onFavoriteClick = { viewModel.toggleFavorite(track.id) },
                            onPlayNextClick = { viewModel.playNext(track) },
                            onAddToQueueClick = { viewModel.addToQueue(track) },
                            onEditClick = { editingTrack = track },
                            onEditLyricsClick = {
                                viewModel.openEditLyricsForTrack(track)
                            },
                            onDeleteClick = { deletingTrack = track },
                            onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                            onClick = { viewModel.selectTrack(track, playlistAudioTracks) }
                        )
                    }
                }
            }
        }
    } else {
        // Playlists overview
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playlists",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Item 1: Liked Songs Card Component
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isViewingFavorites = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFF43F5E),
                                                Color(0xFF8B5CF6)
                                            )
                                        )
                                    )
                                    ,
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Liked Songs",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Liked Songs",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Favorite songs & saved streams",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${favoriteTracks.size} tracks",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFF43F5E),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            if (favoriteTracks.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.playTrackList(favoriteTracks, 0, shuffle = false)
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF43F5E).copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Play Liked Songs",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color(0xFFF43F5E)
                                    )
                                }
                            }
                        }
                    }
                }

                            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Create Playlist", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, description)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// -------------------------------------------------------------
// 4. ARTISTS TAB
// -------------------------------------------------------------
@Composable
fun ArtistsTabPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>
) {
    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }

    if (editingTrack != null) {
        val (currentTitle, currentArtist) = getTitleArtist(editingTrack!!, null)
        EditTrackDialog(
            initialTitle = currentTitle,
            initialArtist = currentArtist,
            onDismiss = { editingTrack = null },
            onConfirm = { title, artist ->
                viewModel.editTrack(editingTrack!!.id, title, artist)
                editingTrack = null
            }
        )
    }

    if (deletingTrack != null) {
        val (delTitle, _) = getTitleArtist(deletingTrack!!, null)
        DeleteTrackConfirmDialog(
            trackTitle = delTitle,
            onDismiss = { deletingTrack = null },
            onConfirm = {
                viewModel.deleteTrack(deletingTrack!!.id)
                deletingTrack = null
            }
        )
    }

    val artistGroups = remember(state.allTracks) {
        state.allTracks.filter { !it.isVideo }.groupBy { it.artist }
    }

    if (state.selectedArtist != null) {
        // Detailed Artist Screen
        val artistName = state.selectedArtist
        val artistTracks = artistGroups[artistName] ?: emptyList()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.selectArtist(null) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back to artists",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Artists",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { viewModel.openSearch(SearchFilter.ARTIST_DETAIL) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search in artist tracks",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Artist Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ArtistAvatar(
                            artistName = artistName,
                            size = 76.dp,
                            firstTrackArtwork = artistTracks.firstOrNull()?.artworkUrl
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = artistName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${artistTracks.size} audio tracks",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { viewModel.playTrackList(artistTracks, 0, shuffle = false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play Artist")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play All", fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.playTrackList(artistTracks, 0, shuffle = true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle Artist")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shuffle", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Songs by this artist
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(artistTracks) { track ->
                    val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                    val isPlaying = track.id == state.currentTrack?.id && state.isPlaying
                    TrackListItem(
                        track = track,
                        displayTitle = title,
                        displayArtist = artist,
                        isPlaying = isPlaying,
                        onEditClick = { editingTrack = track },
                        onEditLyricsClick = {
                            viewModel.openEditLyricsForTrack(track)
                        },
                        onDeleteClick = { deletingTrack = track },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                        onClick = { viewModel.selectTrack(track, artistTracks) }
                    )
                }
            }
        }
    } else {
        // Artists Overview List
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${artistGroups.size} Artists",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { viewModel.openSearch(SearchFilter.ARTISTS) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Artists",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(artistGroups.entries.toList()) { (artist, tracks) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectArtist(artist) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ArtistAvatar(
                                artistName = artist,
                                size = 56.dp,
                                firstTrackArtwork = tracks.firstOrNull()?.artworkUrl
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = artist,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${tracks.size} tracks",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.playTrackList(tracks) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play tracks by $artist",
                                    modifier = Modifier.size(19.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "View artist songs",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. HISTORY TAB (Recently played & Most played)
// -------------------------------------------------------------
@Composable
fun HistoryTabPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>
) {
    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }

    if (editingTrack != null) {
        val (currentTitle, currentArtist) = getTitleArtist(editingTrack!!, null)
        EditTrackDialog(
            initialTitle = currentTitle,
            initialArtist = currentArtist,
            onDismiss = { editingTrack = null },
            onConfirm = { title, artist ->
                viewModel.editTrack(editingTrack!!.id, title, artist)
                editingTrack = null
            }
        )
    }

    if (deletingTrack != null) {
        val (delTitle, _) = getTitleArtist(deletingTrack!!, null)
        DeleteTrackConfirmDialog(
            trackTitle = delTitle,
            onDismiss = { deletingTrack = null },
            onConfirm = {
                viewModel.deleteTrack(deletingTrack!!.id)
                deletingTrack = null
            }
        )
    }

    val rankedTracks = remember(state.allTracks, state.playCounts) {
        state.allTracks
            .filter { !it.isVideo && (state.playCounts[it.id] ?: 0) > 0 }
            .sortedByDescending { state.playCounts[it.id] ?: 0 }
    }

    val recentAudioTracks = remember(state.recentlyPlayed) {
        state.recentlyPlayed.filter { !it.isVideo }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ==========================================
        // SECTION 1: RECENTLY PLAYED
        // ==========================================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Recently Played",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Latest listening history",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (state.recentlyPlayed.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Clear recent history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item {
            if (recentAudioTracks.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentAudioTracks) { track ->
                        val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                        val isPlaying = track.id == state.currentTrack?.id && state.isPlaying

                        Card(
                            modifier = Modifier
                                .width(128.dp)
                                .clickable { viewModel.selectTrack(track, recentAudioTracks) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else SurfaceGlass
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    val historyCover = track.artworkUrl ?: CoverImageProvider.getFallbackCover(track.title, track.artist)
                                    AsyncImage(
                                        model = historyCover,
                                        contentDescription = track.title,
                                        modifier = Modifier
                                            .size(108.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    if (isPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.PlayArrow,
                                                contentDescription = "Playing",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks played recently. Play music to build your history!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ==========================================
        // SECTION 2: MOST PLAYED
        // ==========================================
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Most Played",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ranked by total plays",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (rankedTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No play statistics yet. Play songs to track your top music!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            itemsIndexed(rankedTracks) { index, track ->
                val (title, artist) = getTitleArtist(track, if (track.id == state.currentTrack?.id) state.mediaMetadata else null)
                val isPlaying = track.id == state.currentTrack?.id && state.isPlaying
                val count = state.playCounts[track.id] ?: 0

                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TrackListItem(
                        track = track,
                        displayTitle = title,
                        displayArtist = artist,
                        isPlaying = isPlaying,
                        onPlayNextClick = { viewModel.playNext(track) },
                        onAddToQueueClick = { viewModel.addToQueue(track) },
                        onEditClick = { editingTrack = track },
                        onEditLyricsClick = {
                            viewModel.openEditLyricsForTrack(track)
                        },
                        onDeleteClick = { deletingTrack = track },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) },
                        onClick = { viewModel.selectTrack(track, rankedTracks) },
                        leadingContent = {
                            val rankColor = when (index) {
                                0 -> MaterialTheme.colorScheme.primary
                                1 -> MaterialTheme.colorScheme.secondary
                                2 -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val textColor = when (index) {
                                0, 1, 2 -> Color.White
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(rankColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        },
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$count plays",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageActionsModal(
    onDismiss: () -> Unit,
    onScanStorage: () -> Unit,
    onImportAudio: () -> Unit,
    onImportVideo: () -> Unit,
    onStreamAudio: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Media & Storage Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select an action to scan storage, files, or stream audio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Scan Storage Option Card
                OutlinedCard(
                    onClick = {
                        onDismiss()
                        onScanStorage()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Scan Device Storage", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Detect local music and video folders", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Import Audio/Video Option Card
                OutlinedCard(
                    onClick = {
                        onDismiss()
                        onImportAudio()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Audiotrack, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Import Media Files", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Pick audio or video files from device", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Stream Audio / Suno Option Card
                OutlinedCard(
                    onClick = {
                        onDismiss()
                        onStreamAudio()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Add Stream / Suno Link", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Stream online audio or Suno track", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}