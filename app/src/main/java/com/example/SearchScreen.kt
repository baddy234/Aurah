package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FrostedGlassDefaults
import com.example.ui.FrostedGlassSurface
import com.example.ui.FrostedGlassPill
import com.example.ui.frostedGlass
import androidx.media3.common.MediaMetadata
import com.example.ui.theme.PrimaryUltraviolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TertiaryEmerald

@Composable
fun SearchScreen(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var editingTrack by remember { mutableStateOf<Track?>(null) }
    var deletingTrack by remember { mutableStateOf<Track?>(null) }
    var isFilterModalOpen by remember { mutableStateOf(false) }

    val audioTracks = remember(state.allTracks) { state.allTracks.filter { !it.isVideo } }
    val distinctArtistsCount = remember(audioTracks) {
        audioTracks.map { it.artist.trim() }.filter { it.isNotBlank() }.distinct().size
    }

    if (isFilterModalOpen) {
        val filterOptions = listOf(
            FilterOptionItem(
                value = SearchFilter.ALL,
                title = "All Media",
                subtitle = "Search songs, videos, artists & local files",
                icon = Icons.Filled.MusicNote,
                badgeCount = state.allTracks.size
            ),
            FilterOptionItem(
                value = SearchFilter.ARTISTS,
                title = "Artists",
                subtitle = "Match specifically by artist or creator name",
                icon = Icons.Rounded.Person,
                badgeCount = distinctArtistsCount
            ),
            FilterOptionItem(
                value = SearchFilter.SONGS,
                title = "Audio Songs",
                subtitle = "Filter to music tracks & lossless audio",
                icon = Icons.Filled.MusicNote,
                badgeCount = audioTracks.size
            ),
            FilterOptionItem(
                value = SearchFilter.VIDEOS,
                title = "Videos Only",
                subtitle = "Filter to visual and video playback files",
                icon = Icons.Filled.Movie,
                badgeCount = state.allTracks.count { it.isVideo }
            ),
            FilterOptionItem(
                value = SearchFilter.DEVICE,
                title = "Device Storage",
                subtitle = "Local files stored on device",
                icon = Icons.Rounded.FolderOpen,
                badgeCount = state.allTracks.count { it.isLocal }
            ),
            FilterOptionItem(
                value = SearchFilter.PLAYLISTS,
                title = "Playlists",
                subtitle = "Search playlists & curated collections",
                icon = Icons.Filled.Star,
                badgeCount = state.playlists.size
            ),
            FilterOptionItem(
                value = SearchFilter.HISTORY,
                title = "Listening History",
                subtitle = "Recently played tracks on device",
                icon = Icons.Filled.History,
                badgeCount = state.recentlyPlayed.size
            ),
            FilterOptionItem(
                value = SearchFilter.ONLINE,
                title = "Online & Suno Streams",
                subtitle = "Cloud and imported Suno stream feeds",
                icon = Icons.Filled.Star,
                badgeCount = state.allTracks.count { !it.isLocal }
            )
        )

        FilterModalDialog(
            title = "Search Filter",
            subtitle = "Narrow down your search results",
            options = filterOptions,
            selectedOption = state.searchFilter,
            onSelectOption = { viewModel.setSearchFilter(it) },
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

    // Filter tracks based on query and selected filter
    val query = state.searchQuery.trim().lowercase()

    val artistGroups = remember(audioTracks) {
        audioTracks.groupBy { it.artist.trim() }.filterKeys { it.isNotBlank() }
    }

    val matchingArtists = remember(artistGroups, query) {
        if (query.isBlank()) emptyList()
        else {
            artistGroups.entries
                .filter { (artistName, _) -> artistName.lowercase().contains(query) }
                .sortedBy { (artistName, _) ->
                    if (artistName.lowercase().startsWith(query)) 0 else 1
                }
                .map { it.key to it.value }
        }
    }

    val matchingPlaylists = remember(state.playlists, query) {
        if (query.isBlank()) emptyList()
        else state.playlists.filter { it.name.lowercase().contains(query) }
    }

    val baseTracks = when (state.searchFilter) {
        SearchFilter.ALL -> state.allTracks
        SearchFilter.ARTISTS -> audioTracks
        SearchFilter.SONGS -> audioTracks
        SearchFilter.VIDEOS -> state.allTracks.filter { it.isVideo }
        SearchFilter.DEVICE -> state.allTracks.filter { it.isLocal }
        SearchFilter.ONLINE -> state.allTracks.filter { !it.isLocal }
        SearchFilter.PLAYLISTS -> audioTracks
        SearchFilter.HISTORY -> state.recentlyPlayed
        SearchFilter.ARTIST_DETAIL -> {
            val selectedArtist = state.selectedArtist
            if (selectedArtist != null) state.allTracks.filter { it.artist.equals(selectedArtist, ignoreCase = true) }
            else audioTracks
        }
        SearchFilter.PLAYLIST_DETAIL -> {
            val currentPlaylist = state.viewingPlaylist ?: state.playlists.find { it.id == state.selectedPlaylistId }
            currentPlaylist?.tracks ?: emptyList()
        }
    }

    val searchResults = if (query.isEmpty()) {
        emptyList()
    } else {
        baseTracks.filter { track ->
            val (title, artist) = getTitleArtist(track, null)
            when (state.searchFilter) {
                SearchFilter.ARTISTS -> artist.lowercase().contains(query)
                SearchFilter.SONGS -> title.lowercase().contains(query) || artist.lowercase().contains(query)
                SearchFilter.ARTIST_DETAIL -> title.lowercase().contains(query) || (track.mediaUrl.lowercase().contains(query))
                SearchFilter.PLAYLIST_DETAIL -> title.lowercase().contains(query) || artist.lowercase().contains(query)
                SearchFilter.HISTORY -> title.lowercase().contains(query) || artist.lowercase().contains(query)
                else -> title.lowercase().contains(query) ||
                        artist.lowercase().contains(query) ||
                        (track.mediaUrl.lowercase().contains(query))
            }
        }
    }

    val searchPlaceholder = when (state.searchFilter) {
        SearchFilter.ARTISTS -> "Search artists by name..."
        SearchFilter.SONGS -> "Search audio songs & tracks..."
        SearchFilter.VIDEOS -> "Search videos, clips & recordings..."
        SearchFilter.DEVICE -> "Search local device files..."
        SearchFilter.PLAYLISTS -> "Search playlists..."
        SearchFilter.HISTORY -> "Search listening history..."
        SearchFilter.ARTIST_DETAIL -> "Search in ${state.selectedArtist ?: "artist"} tracks..."
        SearchFilter.PLAYLIST_DETAIL -> "Search tracks in playlist..."
        SearchFilter.ONLINE -> "Search online streams..."
        SearchFilter.ALL -> "Search tracks, artists, audio files..."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Header Bar
        FrostedGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            backgroundColor = FrostedGlassDefaults.GlassDarkElevated,
            borderBrush = FrostedGlassDefaults.glassBorderBrush(
                topColor = Color.White.copy(alpha = 0.08f),
                bottomColor = Color.White.copy(alpha = 0.18f)
            ),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.closeSearch() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("search_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back to Library",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = searchPlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = state.searchQuery.isNotEmpty(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                IconButton(
                                    onClick = { viewModel.clearSearchQuery() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear Search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .testTag("search_text_input"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color.White.copy(alpha = 0.08f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (state.searchQuery.isNotBlank()) {
                                    viewModel.addRecentSearch(state.searchQuery)
                                }
                                focusManager.clearFocus()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Filter Modal Trigger Button
                    val isFiltered = state.searchFilter != SearchFilter.ALL
                    IconButton(
                        onClick = { isFilterModalOpen = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isFiltered) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.White.copy(alpha = 0.08f)
                                }
                            )
                            .testTag("search_filter_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Filter media",
                            tint = if (isFiltered) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Active Filter Indicator Badge (if non-default filter is applied)
                if (state.searchFilter != SearchFilter.ALL) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                    text = "Filtered: ${state.searchFilter.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { viewModel.setSearchFilter(SearchFilter.ALL) },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Reset filter",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Content
        if (query.isEmpty()) {
            // Initial Discovery / Empty Query View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Recent Searches
                if (state.recentSearches.isNotEmpty()) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Recent Searches",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                TextButton(onClick = { viewModel.clearRecentSearches() }) {
                                    Text(
                                        text = "Clear",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.recentSearches) { term ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.clickable {
                                            viewModel.setSearchQuery(term)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = term,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            IconButton(
                                                onClick = { viewModel.removeRecentSearch(term) },
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Remove",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Discovery Suggestions
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = null,
                                tint = SecondaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = when (state.searchFilter) {
                                    SearchFilter.ARTISTS -> "Discover Artists"
                                    SearchFilter.PLAYLISTS -> "Discover Playlists"
                                    SearchFilter.VIDEOS -> "Discover Videos"
                                    SearchFilter.DEVICE -> "Local Storage"
                                    else -> "Quick Discover"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val suggestions = when (state.searchFilter) {
                            SearchFilter.ARTISTS -> {
                                val topArtists = artistGroups.keys.take(6).toList()
                                if (topArtists.isNotEmpty()) topArtists
                                else listOf("Acoustic", "Jazz", "Instrumental")
                            }
                            SearchFilter.PLAYLISTS -> {
                                val plNames = state.playlists.map { it.name }.take(6)
                                if (plNames.isNotEmpty()) plNames
                                else listOf("Favorites", "Recently Added", "Daily Mix")
                            }
                            SearchFilter.VIDEOS -> {
                                val videoTitles = state.allTracks.filter { it.isVideo }.map { it.title.trim() }.filter { it.isNotBlank() }.take(6)
                                if (videoTitles.isNotEmpty()) videoTitles
                                else listOf("Music Videos", "Tutorials", "Recorded Media", "Streams", "Shorts", "Live")
                            }
                            SearchFilter.DEVICE -> listOf("Downloads", "Music", "Recordings", "Podcasts")
                            else -> {
                                val topTitles = state.allTracks.map { it.title.trim() }.filter { it.isNotBlank() }.take(6)
                                if (topTitles.isNotEmpty()) topTitles
                                else listOf("Acoustic", "Jazz", "Instrumental", "Electronic", "Rock", "Classical")
                            }
                        }

                        val firstRow = suggestions.take((suggestions.size + 1) / 2)
                        val secondRow = suggestions.drop(firstRow.size)

                        if (firstRow.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                firstRow.forEach { suggestion ->
                                    SuggestionChip(
                                        title = suggestion,
                                        onClick = {
                                            viewModel.setSearchQuery(suggestion)
                                            viewModel.addRecentSearch(suggestion)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        if (secondRow.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                secondRow.forEach { suggestion ->
                                    SuggestionChip(
                                        title = suggestion,
                                        onClick = {
                                            viewModel.setSearchQuery(suggestion)
                                            viewModel.addRecentSearch(suggestion)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Suno Stream Importer Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openAddStream() }
                            .testTag("suno_stream_search_banner"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Suno Importer",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Import Suno / Custom Stream",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Stream @technicalrave7605 or paste direct song links",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Library Media Stats Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(PrimaryUltraviolet, SecondaryCyan)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MusicNote,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "${state.allTracks.size} Media Files Ready",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${state.allTracks.count { it.isLocal }} local device tracks • ${state.allTracks.count { !it.isLocal }} online lossless streams",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Search Results
            val hasArtists = state.searchFilter == SearchFilter.ARTISTS && matchingArtists.isNotEmpty()
            val hasPlaylists = state.searchFilter == SearchFilter.PLAYLISTS && matchingPlaylists.isNotEmpty()
            val isResultsEmpty = searchResults.isEmpty() && !hasArtists && !hasPlaylists

            if (isResultsEmpty) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
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
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (state.searchFilter) {
                                    SearchFilter.ARTISTS -> Icons.Rounded.Person
                                    SearchFilter.PLAYLISTS -> Icons.Filled.Star
                                    SearchFilter.VIDEOS -> Icons.Filled.Movie
                                    SearchFilter.DEVICE -> Icons.Rounded.FolderOpen
                                    else -> Icons.Filled.SearchOff
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = when (state.searchFilter) {
                                SearchFilter.ARTISTS -> "No artists found for \"${state.searchQuery}\""
                                SearchFilter.PLAYLISTS -> "No playlists found for \"${state.searchQuery}\""
                                SearchFilter.VIDEOS -> "No videos found for \"${state.searchQuery}\""
                                else -> "No results found for \"${state.searchQuery}\""
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = when (state.searchFilter) {
                                SearchFilter.ARTISTS -> "Try checking the artist name spelling or browse the Artists library tab."
                                SearchFilter.PLAYLISTS -> "Check the playlist name or create a new playlist in Library."
                                SearchFilter.VIDEOS -> "No videos matched your query. You can import video files directly via the Videos tab."
                                SearchFilter.DEVICE -> "No local device media files matched your query."
                                else -> "Check the spelling or try searching for a different artist, album, or file name."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Results List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick Artist Matches Pill Row (When searching in ALL mode and artist matches exist)
                    if (state.searchFilter == SearchFilter.ALL && matchingArtists.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text(
                                    text = "Matching Artists",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(matchingArtists) { (artistName, tracks) ->
                                        Surface(
                                            onClick = {
                                                viewModel.addRecentSearch(state.searchQuery)
                                                viewModel.selectArtist(artistName)
                                                viewModel.closeSearch()
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(26.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            Brush.linearGradient(
                                                                listOf(
                                                                    MaterialTheme.colorScheme.primary,
                                                                    MaterialTheme.colorScheme.secondary
                                                                )
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = artistName.take(1).uppercase(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                                Text(
                                                    text = artistName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "(${tracks.size})",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Artist Scoped View: Dedicated Matching Artist Profiles
                    if (state.searchFilter == SearchFilter.ARTISTS && matchingArtists.isNotEmpty()) {
                        item {
                            Text(
                                text = "Artist Profiles (${matchingArtists.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(matchingArtists, key = { it.first }) { (artistName, tracks) ->
                            FrostedGlassSurface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addRecentSearch(state.searchQuery)
                                        viewModel.selectArtist(artistName)
                                        viewModel.closeSearch()
                                    },
                                shape = RoundedCornerShape(16.dp),
                                elevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.secondary
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = artistName.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = artistName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${tracks.size} tracks available",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.addRecentSearch(state.searchQuery)
                                            viewModel.playTrackList(tracks, 0, shuffle = false)
                                            viewModel.closeSearch()
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Play tracks by $artistName",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = "View artist discography",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Playlists Scoped View
                    if (state.searchFilter == SearchFilter.PLAYLISTS && matchingPlaylists.isNotEmpty()) {
                        item {
                            Text(
                                text = "Playlists (${matchingPlaylists.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(matchingPlaylists, key = { it.id }) { playlist ->
                            FrostedGlassSurface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addRecentSearch(state.searchQuery)
                                        viewModel.viewPlaylistDetails(playlist)
                                        viewModel.closeSearch()
                                    },
                                shape = RoundedCornerShape(16.dp),
                                elevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(PrimaryUltraviolet, SecondaryCyan)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = playlist.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${playlist.tracks.size} songs",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = "Open Playlist",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Matching Tracks Header with count and Play All / Shuffle actions
                    if (searchResults.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = when (state.searchFilter) {
                                        SearchFilter.ARTISTS -> "Songs by Matching Artists (${searchResults.size})"
                                        SearchFilter.PLAYLISTS -> "Matching Songs (${searchResults.size})"
                                        SearchFilter.VIDEOS -> "Videos (${searchResults.size})"
                                        SearchFilter.ARTIST_DETAIL -> "Songs in Discography (${searchResults.size})"
                                        else -> "${searchResults.size} matches found"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = {
                                            viewModel.addRecentSearch(state.searchQuery)
                                            viewModel.playTrackList(searchResults, 0, shuffle = false)
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    TextButton(
                                        onClick = {
                                            viewModel.addRecentSearch(state.searchQuery)
                                            viewModel.playTrackList(searchResults, 0, shuffle = true)
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Shuffle,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Shuffle", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // Results Items
                        itemsIndexed(searchResults, key = { _, track -> track.id }) { index, track ->
                            val (title, artist) = getTitleArtist(track, null)
                            val isPlayingThis = state.isPlaying && state.currentTrack?.id == track.id
                            val isFav = state.favoriteTrackIds.contains(track.id)

                            TrackListItem(
                                track = track,
                                displayTitle = title,
                                displayArtist = artist,
                                isPlaying = isPlayingThis,
                                isFavorite = if (track.isVideo) false else isFav,
                                onClick = {
                                    viewModel.addRecentSearch(state.searchQuery)
                                    viewModel.playTrackList(searchResults, index, shuffle = false)
                                },
                                onPlayNextClick = { viewModel.playNext(track) },
                                onAddToQueueClick = { viewModel.addToQueue(track) },
                                onFavoriteClick = if (track.isVideo) null else { { viewModel.toggleFavorite(track.id) } },
                                onEditClick = { editingTrack = track },
                                onEditLyricsClick = { viewModel.openEditLyricsForTrack(track) },
                                onDeleteClick = { deletingTrack = track },
                                onAddToPlaylistClick = { viewModel.openAddToPlaylist(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
