package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.media3.common.MediaMetadata
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.ui.theme.SurfaceGlass
import com.example.ui.FrostedGlassDefaults
import com.example.ui.FrostedGlassSurface
import com.example.ui.FrostedGlassPill
import com.example.ui.frostedGlass
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueueDragDropState(
    var onMoveItem: (fromIndex: Int, toIndex: Int) -> Unit,
    var getBoundsForIndex: (index: Int) -> Pair<Int, Int>
) {
    var draggedIndex by mutableStateOf<Int?>(null)
    var dragOffset by mutableFloatStateOf(0f)
    var itemHeightPx by mutableFloatStateOf(0f)

    fun onDragStart(index: Int) {
        draggedIndex = index
        dragOffset = 0f
    }

    fun onDrag(dragAmount: Float, onSwapped: ((Int) -> Unit)? = null) {
        var currentIndex = draggedIndex ?: return
        dragOffset += dragAmount

        val effectiveHeight = if (itemHeightPx > 0f) itemHeightPx else 190f
        val (minAllowed, maxAllowed) = getBoundsForIndex(currentIndex)

        while (dragOffset > effectiveHeight * 0.5f && currentIndex < maxAllowed) {
            val nextIndex = currentIndex + 1
            onMoveItem(currentIndex, nextIndex)
            currentIndex = nextIndex
            draggedIndex = currentIndex
            dragOffset -= effectiveHeight
            onSwapped?.invoke(currentIndex)
        }
        while (dragOffset < -effectiveHeight * 0.5f && currentIndex > minAllowed) {
            val prevIndex = currentIndex - 1
            onMoveItem(currentIndex, prevIndex)
            currentIndex = prevIndex
            draggedIndex = currentIndex
            dragOffset += effectiveHeight
            onSwapped?.invoke(currentIndex)
        }
    }

    fun onDragEnd() {
        draggedIndex = null
        dragOffset = 0f
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueModal(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    getTitleArtist: (Track, MediaMetadata?) -> Pair<String, String>,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var showAddTracksDialog by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var showBpmSortDialog by remember { mutableStateOf(false) }
    var showPreviousTracks by remember { mutableStateOf(false) }

    // Auto-clear feedback message
    LaunchedEffect(state.queueFeedbackMessage) {
        if (state.queueFeedbackMessage != null) {
            delay(2800L)
            viewModel.clearQueueFeedbackMessage()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FrostedGlassDefaults.GlassDarkElevated,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                // Header Bar
                QueueHeader(
                    queueSize = state.tracks.size,
                    currentTrack = state.currentTrack,
                    onShuffle = { viewModel.shuffleQueue() },
                    onBpmSort = { showBpmSortDialog = true },
                    onClear = { showClearConfirmation = true },
                    onAddMore = { showAddTracksDialog = true },
                    onClose = onDismiss
                )

                // Feedback Banner
                AnimatedVisibility(
                    visible = state.queueFeedbackMessage != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.queueFeedbackMessage ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Queue Content
                if (state.tracks.isEmpty()) {
                    EmptyQueueView(onAddTracksClick = { showAddTracksDialog = true })
                } else {
                    val currentPlayingIndex = state.tracks.indexOfFirst { it.id == state.currentTrack?.id }
                    val lazyListState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    val haptic = LocalHapticFeedback.current
                    val density = LocalDensity.current
                    val spacingPx = with(density) { 10.dp.toPx() }

                    val stableTrackKeys = remember(state.tracks) {
                        val counts = mutableMapOf<String, Int>()
                        state.tracks.map { track ->
                            val count = counts.getOrDefault(track.id, 0)
                            counts[track.id] = count + 1
                            "${track.id}_#$count"
                        }
                    }

                    val dragDropState = remember {
                        QueueDragDropState(
                            onMoveItem = { from, to -> viewModel.moveQueueItem(from, to) },
                            getBoundsForIndex = { index ->
                                val curPlay = state.tracks.indexOfFirst { it.id == state.currentTrack?.id }
                                if (curPlay != -1 && index < curPlay) {
                                    Pair(0, curPlay - 1)
                                } else if (curPlay != -1) {
                                    Pair(curPlay + 1, state.tracks.size - 1)
                                } else {
                                    Pair(0, state.tracks.size - 1)
                                }
                            }
                        )
                    }
                    dragDropState.onMoveItem = { from, to -> viewModel.moveQueueItem(from, to) }
                    dragDropState.getBoundsForIndex = { index ->
                        val curPlay = state.tracks.indexOfFirst { it.id == state.currentTrack?.id }
                        if (curPlay != -1 && index < curPlay) {
                            Pair(0, curPlay - 1)
                        } else if (curPlay != -1) {
                            Pair(curPlay + 1, state.tracks.size - 1)
                        } else {
                            Pair(0, state.tracks.size - 1)
                        }
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // 0. Previous Songs Section (Collapsible)
                        val previousTracks = if (currentPlayingIndex > 0) {
                            state.tracks.mapIndexed { index, track -> Pair(index, track) }.take(currentPlayingIndex)
                        } else {
                            emptyList()
                        }

                        if (previousTracks.isNotEmpty()) {
                            item(key = "previous_tracks_header") {
                                Surface(
                                    onClick = { showPreviousTracks = !showPreviousTracks },
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .testTag("toggle_previous_tracks_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.History,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "PREVIOUS SONGS (${previousTracks.size})",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(
                                            imageVector = if (showPreviousTracks) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                            contentDescription = if (showPreviousTracks) "Collapse previous tracks" else "Expand previous tracks",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            if (showPreviousTracks) {
                                items(
                                    items = previousTracks,
                                    key = { (index, _) -> stableTrackKeys.getOrElse(index) { "prev_$index" } }
                                ) { (index, track) ->
                                    val (title, artist) = getTitleArtist(track, null)
                                    val isDragging = dragDropState.draggedIndex == index
                                    val dragOffset = if (isDragging) dragDropState.dragOffset else 0f

                                    QueueTrackItem(
                                        track = track,
                                        queueIndex = index,
                                        totalQueueSize = state.tracks.size,
                                        displayTitle = title,
                                        displayArtist = artist,
                                        isDragging = isDragging,
                                        dragOffset = dragOffset,
                                        dragHandleModifier = Modifier.pointerInput(track.id) {
                                            detectVerticalDragGestures(
                                                onDragStart = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    val startIndex = state.tracks.indexOfFirst { it.id == track.id }
                                                    if (startIndex != -1) dragDropState.onDragStart(startIndex)
                                                },
                                                onDragEnd = { dragDropState.onDragEnd() },
                                                onDragCancel = { dragDropState.onDragEnd() },
                                                onVerticalDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragDropState.onDrag(dragAmount) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    }
                                                }
                                            )
                                        },
                                        onItemHeightMeasured = { height ->
                                            dragDropState.itemHeightPx = height.toFloat() + spacingPx
                                        },
                                        onPlayNow = { viewModel.playQueueTrackAt(index) },
                                        onRemove = { viewModel.removeFromQueue(index) }
                                    )
                                }
                            }
                        }

                        // 1. Now Playing Item
                        if (state.currentTrack != null && currentPlayingIndex != -1) {
                            item(key = "now_playing_section") {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.GraphicEq,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "NOW PLAYING",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.2.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    val (title, artist) = getTitleArtist(state.currentTrack!!, state.mediaMetadata)
                                    NowPlayingQueueCard(
                                        track = state.currentTrack!!,
                                        title = title,
                                        artist = artist,
                                        isPlaying = state.isPlaying,
                                        onPlayPause = { viewModel.togglePlayPause() },
                                        onRemove = { viewModel.removeFromQueue(currentPlayingIndex) }
                                    )
                                }
                            }
                        }

                        // 2. Up Next Section (Only songs strictly after the currently playing song)
                        val upcomingTracks = if (currentPlayingIndex != -1) {
                            state.tracks.mapIndexed { index, track -> Pair(index, track) }
                                .filter { it.first > currentPlayingIndex }
                        } else {
                            state.tracks.mapIndexed { index, track -> Pair(index, track) }
                        }

                        item(key = "up_next_header") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "UP NEXT (${upcomingTracks.size})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (upcomingTracks.size > 1) {
                                        Text(
                                            text = "• Drag ≡ to arrange",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = { showAddTracksDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("queue_add_songs_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add Songs",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        if (upcomingTracks.isNotEmpty()) {
                            items(
                                items = upcomingTracks,
                                key = { (index, _) -> stableTrackKeys.getOrElse(index) { "upcoming_$index" } }
                            ) { (index, track) ->
                                val (title, artist) = getTitleArtist(track, null)
                                val isDragging = dragDropState.draggedIndex == index
                                val dragOffset = if (isDragging) dragDropState.dragOffset else 0f

                                QueueTrackItem(
                                    track = track,
                                    queueIndex = index,
                                    totalQueueSize = state.tracks.size,
                                    displayTitle = title,
                                    displayArtist = artist,
                                    isDragging = isDragging,
                                    dragOffset = dragOffset,
                                    dragHandleModifier = Modifier.pointerInput(track.id) {
                                        detectVerticalDragGestures(
                                            onDragStart = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                val startIndex = state.tracks.indexOfFirst { it.id == track.id }
                                                if (startIndex != -1) dragDropState.onDragStart(startIndex)
                                            },
                                            onDragEnd = { dragDropState.onDragEnd() },
                                            onDragCancel = { dragDropState.onDragEnd() },
                                            onVerticalDrag = { change, dragAmount ->
                                                change.consume()
                                                dragDropState.onDrag(dragAmount) { swappedIndex ->
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    val visible = lazyListState.layoutInfo.visibleItemsInfo
                                                    val firstVisible = visible.firstOrNull()?.index ?: 0
                                                    val lastVisible = visible.lastOrNull()?.index ?: 0
                                                    if (swappedIndex >= lastVisible - 1) {
                                                        coroutineScope.launch {
                                                            lazyListState.animateScrollToItem((swappedIndex + 1).coerceAtMost(state.tracks.size - 1))
                                                        }
                                                    } else if (swappedIndex <= firstVisible + 1) {
                                                        coroutineScope.launch {
                                                            lazyListState.animateScrollToItem((swappedIndex - 1).coerceAtLeast(0))
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    },
                                    onItemHeightMeasured = { height ->
                                        dragDropState.itemHeightPx = height.toFloat() + spacingPx
                                    },
                                    onPlayNow = { viewModel.playQueueTrackAt(index) },
                                    onRemove = { viewModel.removeFromQueue(index) }
                                )
                            }
                        } else {
                            item(key = "end_of_queue_state") {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "End of queue",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "No more upcoming songs after this track.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = { showAddTracksDialog = true },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Add Songs")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Clear Confirmation Dialog
            if (showClearConfirmation) {
                AlertDialog(
                    onDismissRequest = { showClearConfirmation = false },
                    title = { Text("Clear Queue") },
                    text = { Text("Do you want to clear upcoming tracks from the queue?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearQueue(keepCurrentTrack = true)
                                showClearConfirmation = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Clear")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearConfirmation = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Rearrange Queue by BPM Dialog
            if (showBpmSortDialog) {
                QueueBpmSortDialog(
                    onDismiss = { showBpmSortDialog = false },
                    onApplySort = { mode, upcomingOnly ->
                        viewModel.sortQueueByBpm(mode, onlyUpcoming = upcomingOnly)
                    }
                )
            }

            // Add Tracks Selector Dialog
            if (showAddTracksDialog) {
                AddTracksToQueueDialog(
                    allTracks = state.allTracks.ifEmpty { state.deviceTracks },
                    currentQueueTrackIds = state.tracks.map { it.id }.toSet(),
                    onDismiss = { showAddTracksDialog = false },
                    onAddToQueue = { track ->
                        viewModel.addToQueue(track)
                    },
                    onPlayNext = { track ->
                        viewModel.playNext(track)
                    }
                )
            }
        }
    }
}

@Composable
private fun QueueHeader(
    queueSize: Int,
    currentTrack: Track?,
    onShuffle: () -> Unit,
    onBpmSort: () -> Unit,
    onClear: () -> Unit,
    onAddMore: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                Icon(
                    imageVector = Icons.Filled.QueueMusic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = "Playing Queue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$queueSize song${if (queueSize == 1) "" else "s"} in line",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = onShuffle,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .testTag("shuffle_queue_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle Queue",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (queueSize > 1) {
                IconButton(
                    onClick = onBpmSort,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("sort_queue_bpm_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = "Rearrange Queue by BPM",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (queueSize > 1) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("clear_queue_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ClearAll,
                        contentDescription = "Clear Queue",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            IconButton(
                onClick = onAddMore,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                    .testTag("add_tracks_to_queue_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Track to Queue",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .testTag("close_queue_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun NowPlayingQueueCard(
    track: Track,
    title: String,
    artist: String,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onRemove: () -> Unit
) {
    FrostedGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
        borderBrush = Brush.horizontalGradient(
            listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
            )
        ),
        borderWidth = 1.5.dp,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackThumbnail(
                artworkUrl = track.artworkUrl,
                coverColorStr = track.coverColorStr,
                size = 54.dp,
                shape = RoundedCornerShape(14.dp),
                isVideo = track.isVideo
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${track.effectiveBpm} BPM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Play / Pause Icon Button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.GraphicEq else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Playing" else "Paused",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Remove Button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove from queue",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.QueueTrackItem(
    track: Track,
    queueIndex: Int,
    totalQueueSize: Int,
    displayTitle: String,
    displayArtist: String,
    isDragging: Boolean,
    dragOffset: Float,
    dragHandleModifier: Modifier,
    onItemHeightMeasured: (Int) -> Unit,
    onPlayNow: () -> Unit,
    onRemove: () -> Unit
) {
    FrostedGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (isDragging) 10f else 1f)
            .graphicsLayer {
                translationY = dragOffset
                if (isDragging) {
                    scaleX = 1.025f
                    scaleY = 1.025f
                    shadowElevation = 20f
                }
            }
            .then(if (!isDragging) Modifier.animateItem() else Modifier)
            .onGloballyPositioned { coordinates ->
                if (coordinates.size.height > 0) {
                    onItemHeightMeasured(coordinates.size.height)
                }
            }
            .clickable(enabled = !isDragging) { onPlayNow() },
        shape = RoundedCornerShape(14.dp),
        backgroundColor = if (isDragging) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else {
            FrostedGlassDefaults.GlassDarkBase
        },
        borderBrush = if (isDragging) {
            Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )
        } else {
            FrostedGlassDefaults.glassBorderBrush(
                topColor = Color.White.copy(alpha = 0.12f),
                bottomColor = Color.White.copy(alpha = 0.03f)
            )
        },
        borderWidth = if (isDragging) 1.5.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${queueIndex + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            TrackThumbnail(
                artworkUrl = track.artworkUrl,
                coverColorStr = track.coverColorStr,
                size = 44.dp,
                shape = RoundedCornerShape(10.dp),
                isVideo = track.isVideo
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = displayArtist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${track.effectiveBpm} BPM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Drag Handle for rearranging track
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else Color.Transparent
                    )
                    .testTag("queue_drag_handle_$queueIndex")
                    .then(dragHandleModifier),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DragHandle,
                    contentDescription = "Drag to arrange",
                    tint = if (isDragging) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            // Remove from queue button
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("queue_remove_track_$queueIndex")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyQueueView(onAddTracksClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.QueueMusic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Queue is Empty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Add songs to queue up your custom listening session",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAddTracksClick,
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Tracks to Queue")
        }
    }
}

@Composable
private fun AddTracksToQueueDialog(
    allTracks: List<Track>,
    currentQueueTrackIds: Set<String>,
    onDismiss: () -> Unit,
    onAddToQueue: (Track) -> Unit,
    onPlayNext: (Track) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredTracks = remember(allTracks, searchQuery) {
        if (searchQuery.isBlank()) allTracks
        else allTracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FrostedGlassSurface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = FrostedGlassDefaults.GlassDarkElevated,
            borderBrush = FrostedGlassDefaults.glassBorderBrush(
                topColor = Color.White.copy(alpha = 0.25f),
                bottomColor = Color.White.copy(alpha = 0.05f)
            ),
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlaylistAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Songs to Queue",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search songs or artists...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Track List
                if (filteredTracks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tracks found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = filteredTracks,
                            key = { _, item -> item.id }
                        ) { _, track ->
                            val isInQueue = currentQueueTrackIds.contains(track.id)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TrackThumbnail(
                                        artworkUrl = track.artworkUrl,
                                        coverColorStr = track.coverColorStr,
                                        size = 42.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        isVideo = track.isVideo
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = track.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = track.artist,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Play Next Button
                                    TextButton(
                                        onClick = { onPlayNext(track) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Play Next",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    // Add to End of Queue Button
                                    FilledTonalButton(
                                        onClick = { onAddToQueue(track) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isInQueue) "Add Again" else "Queue",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun QueueBpmSortDialog(
    onDismiss: () -> Unit,
    onApplySort: (QueueBpmSortMode, Boolean) -> Unit
) {
    var upcomingOnly by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Rearrange Queue by BPM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Musical tempo order & harmonic flow",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Scope Toggle
                FrostedGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { upcomingOnly = !upcomingOnly },
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    borderWidth = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "Keep Currently Playing Song",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (upcomingOnly) "Sort upcoming songs only" else "Reorder entire queue",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = upcomingOnly,
                            onCheckedChange = { upcomingOnly = it }
                        )
                    }
                }

                // 3 BPM Sorting Options
                BpmSortOptionCard(
                    title = "Slow to Fast (Warm-up)",
                    subtitle = "Gradually builds tempo from relaxed beats to high energy",
                    badge = "Low → High",
                    icon = Icons.Filled.ArrowUpward,
                    accentColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        onApplySort(QueueBpmSortMode.ASCENDING, upcomingOnly)
                        onDismiss()
                    }
                )

                BpmSortOptionCard(
                    title = "Fast to Slow (Cooldown)",
                    subtitle = "Steps down tempo to relax and unwind after high energy",
                    badge = "High → Low",
                    icon = Icons.Filled.ArrowDownward,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    onClick = {
                        onApplySort(QueueBpmSortMode.DESCENDING, upcomingOnly)
                        onDismiss()
                    }
                )

                BpmSortOptionCard(
                    title = "Harmonic Flow (DJ Mix)",
                    subtitle = "Pairs adjacent songs with nearest BPM for seamless transitions",
                    badge = "DJ Mix Flow",
                    icon = Icons.Filled.AutoAwesome,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        onApplySort(QueueBpmSortMode.HARMONIC_FLOW, upcomingOnly)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun BpmSortOptionCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    FrostedGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        borderBrush = Brush.horizontalGradient(
            listOf(
                accentColor.copy(alpha = 0.5f),
                accentColor.copy(alpha = 0.1f)
            )
        ),
        borderWidth = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
