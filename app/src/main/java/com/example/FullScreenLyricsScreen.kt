package com.example

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dedicated Full Screen Lyrics Page featuring:
 * - Dynamic Artwork Gradient Background
 * - Real-time Synced Lyrics with Tap-to-Seek & Auto-Scroll
 * - Plain Text Lyrics with Adjustable Typography
 * - Add / Edit Lyrics with Live Timestamp Inserter
 * - Online Lyrics Search & Fetch
 * - Copy to Clipboard
 */
@Composable
fun FullScreenLyricsScreen(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    lyrics: String,
    syncedLyrics: List<LyricLine>,
    activeLyricIndex: Int,
    isPlaying: Boolean,
    progressMs: Long,
    isFetchingLyrics: Boolean,
    lyricsFetchMessage: String?,
    isAutoScrollLyrics: Boolean,
    lyricsViewMode: LyricsViewMode,
    syncedLyricsDisplayStyle: SyncedLyricsDisplayStyle = SyncedLyricsDisplayStyle.LIST,
    onSetSyncedLyricsDisplayStyle: (SyncedLyricsDisplayStyle) -> Unit = {},
    lyricsFontSize: Int,
    lyricsAlignment: String,
    lyricsScrollSpeed: Float,
    isDarkTheme: Boolean = true,
    onClose: () -> Unit,
    onSaveLyrics: (String) -> Unit,
    onEdit: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onFetchLyrics: (queryTitle: String?, queryArtist: String?) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onSetLyricsViewMode: (LyricsViewMode) -> Unit,
    onClearLyricsMessage: () -> Unit,
    onOpenImportLyrics: (() -> Unit)? = null,
    onOpenSyncStudio: (() -> Unit)? = null,
    onGenerateAiContent: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var isEditing by remember { mutableStateOf(false) }
    var editText by remember(lyrics, isEditing) { mutableStateOf(lyrics) }
    var showFetchDialog by remember { mutableStateOf(false) }
    var queryTitle by remember(displayTitle) { mutableStateOf(displayTitle) }
    var queryArtist by remember(displayArtist) { mutableStateOf(displayArtist) }
    val alignText = when (lyricsAlignment) {
        "Left" -> TextAlign.Start
        "Right" -> TextAlign.End
        else -> TextAlign.Center
    }

    // Auto-scroll logic to center the active singing line
    LaunchedEffect(activeLyricIndex, isAutoScrollLyrics, lyricsViewMode) {
        if (isAutoScrollLyrics && lyricsViewMode == LyricsViewMode.SYNCED && activeLyricIndex >= 0 && syncedLyrics.isNotEmpty()) {
            val targetScroll = (activeLyricIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    // Custom Online Lyrics Search Dialog
    if (showFetchDialog) {
        AlertDialog(
            onDismissRequest = { showFetchDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Fetch Lyrics Online", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Search synchronized LRC lyrics by track title and artist name:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = queryTitle,
                        onValueChange = { queryTitle = it },
                        label = { Text("Song Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = queryArtist,
                        onValueChange = { queryArtist = it },
                        label = { Text("Artist Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onFetchLyrics(queryTitle.trim(), queryArtist.trim())
                        showFetchDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Search & Fetch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFetchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(getArtworkBackgroundGradient(track.coverColorStr, isDarkTheme = isDarkTheme))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Close/Back Button
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("close_fullscreen_lyrics_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Close Lyrics Screen",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Center Track Info Pill
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isPlaying) {
                                        Icon(
                                            imageVector = Icons.Filled.GraphicEq,
                                            contentDescription = "Playing",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = displayTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = displayArtist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Mode Toggle & Edit Actions
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Synced vs Plain Toggle
                                IconButton(
                                    onClick = {
                                        onSetLyricsViewMode(
                                            if (lyricsViewMode == LyricsViewMode.SYNCED) LyricsViewMode.PLAIN
                                            else LyricsViewMode.SYNCED
                                        )
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (lyricsViewMode == LyricsViewMode.SYNCED) Icons.Filled.Timer else Icons.Filled.FormatAlignLeft,
                                        contentDescription = "Toggle Synced / Plain mode",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Fetch Online
                                IconButton(
                                    onClick = { showFetchDialog = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDownload,
                                        contentDescription = "Fetch Online Lyrics",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Display Style Toggle
                                IconButton(
                                    onClick = {
                                        onSetSyncedLyricsDisplayStyle(
                                            if (syncedLyricsDisplayStyle == SyncedLyricsDisplayStyle.LIST)
                                                SyncedLyricsDisplayStyle.FOCUS
                                            else
                                                SyncedLyricsDisplayStyle.LIST
                                        )
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (syncedLyricsDisplayStyle == SyncedLyricsDisplayStyle.LIST) Icons.Filled.FormatAlignLeft else Icons.Filled.GraphicEq,
                                        contentDescription = "Toggle Synced Lyrics Style",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                // Import Lyrics File / Text
                                if (onOpenImportLyrics != null) {
                                    IconButton(
                                        onClick = onOpenImportLyrics,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("fullscreen_lyrics_import_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.FileOpen,
                                            contentDescription = "Import Lyrics (.lrc / .txt)",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Sync Studio
                                if (onOpenSyncStudio != null) {
                                    IconButton(
                                        onClick = onOpenSyncStudio,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Timer,
                                            contentDescription = "Open Sync Studio",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Edit / Add Lyrics
                                IconButton(
                                    onClick = { onEdit() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = if (isEditing) "Close Editor" else "Add or Edit Lyrics",
                                        tint = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Sub-header controls (Auto scroll lock, Copy, Font slider)
                        if (!isEditing && lyrics.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (lyricsViewMode == LyricsViewMode.SYNCED) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isAutoScrollLyrics) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { onToggleAutoScroll(!isAutoScrollLyrics) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isAutoScrollLyrics) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                                contentDescription = null,
                                                tint = if (isAutoScrollLyrics) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (isAutoScrollLyrics) "Auto-Scroll: ON" else "Auto-Scroll: OFF",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAutoScrollLyrics) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Song Lyrics", lyrics)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Lyrics copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Copy Lyrics", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Fetching progress bar
                if (isFetchingLyrics) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                // Notification / status banner
                if (lyricsFetchMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lyricsFetchMessage,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f),
                                                    textAlign = alignText
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

                // Main Content Body: Editor OR Lyrics Viewer
                if (isEditing) {
                    // Full Lyrics Editor
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Add / Edit Synchronized Lyrics",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                // Insert current timestamp helper button
                                FilledTonalButton(
                                    onClick = {
                                        val formattedStamp = "[${LrcParser.formatTimestamp(progressMs)}]"
                                        editText = if (editText.isBlank()) {
                                            "$formattedStamp "
                                        } else {
                                            "$editText\n$formattedStamp "
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Insert Stamp: ${LrcParser.formatShortTime(progressMs)}", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = editText,
                                onValueChange = { editText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Enter lyrics here...\n\nFor Synchronized Lyrics, use timestamp format:\n[00:15.20] First lyric line\n[00:28.45] Second lyric line\n\nOr paste plain lyrics without timestamps."
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                            )

                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                        putExtra(SearchManager.QUERY, "$displayTitle $displayArtist lyrics lrc")
                                    }
                                    context.startActivity(intent)
                                }) {
                                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Search Web for LRC")
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { isEditing = false }) {
                                        Text("Cancel")
                                    }
                                    Button(
                                        onClick = {
                                            onSaveLyrics(editText)
                                            isEditing = false
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Save Lyrics")
                                    }
                                }
                            }
                        }
                    }
                } else if (lyrics.isBlank()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Mic,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Text(
                                    text = "No Lyrics Available",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Fetch synchronized lyrics from online repository or add custom lyrics.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { onFetchLyrics(null, null) },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Fetch Online")
                                    }
                                    if (onOpenImportLyrics != null) {
                                        Spacer(Modifier.width(8.dp))
                                        FilledTonalButton(
                                            onClick = onOpenImportLyrics,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.testTag("empty_state_import_lyrics_button")
                                        ) {
                                            Icon(Icons.Filled.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Import .lrc")
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = { onEdit() },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Add Lyrics")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Display Lyrics Mode
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
                                                    fontSize = (lyricsFontSize + 6).sp
                                                ),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                textAlign = alignText,
                                                modifier = Modifier.padding(24.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "...",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 60.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    itemsIndexed(syncedLyrics) { index, line ->
                                        val isActive = index == activeLyricIndex
                                        val isPast = index < activeLyricIndex

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onSeekToPosition(line.timeMs) },
                                            shape = RoundedCornerShape(18.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isActive) {
                                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                                                } else {
                                                    Color.Black.copy(alpha = 0.2f)
                                                }
                                            ),
                                            border = if (isActive) {
                                                androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                            } else null
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 18.dp, vertical = if (isActive) 16.dp else 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = line.text.ifBlank { "..." },
                                                    style = if (isActive) {
                                                        MaterialTheme.typography.headlineSmall.copy(
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = lyricsFontSize.sp
                                                        )
                                                    } else {
                                                        MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = (lyricsFontSize * 0.8f).sp
                                                        )
                                                    },
                                                    color = when {
                                                        isActive -> MaterialTheme.colorScheme.onPrimaryContainer
                                                        isPast -> Color.White.copy(alpha = 0.45f)
                                                        else -> Color.White.copy(alpha = 0.85f)
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    textAlign = alignText
                                                )

                                                Spacer(Modifier.width(10.dp))

                                                Text(
                                                    text = LrcParser.formatShortTime(line.timeMs),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isActive) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        Color.White.copy(alpha = 0.4f)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                }
                            } else {
                                // Plain text fallback when in Synced mode
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "No Timestamps Available for Real-Time Sync",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            Text(
                                                text = "Displaying plain text lyrics. You can add timestamps using the editor.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(Modifier.height(14.dp))
                                            Text(
                                                text = lyrics,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    lineHeight = 28.sp,
                                                    fontSize = lyricsFontSize.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = alignText
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        LyricsViewMode.PLAIN, LyricsViewMode.EDIT -> {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = lyrics,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            lineHeight = (lyricsFontSize * 1.5).sp,
                                            fontSize = lyricsFontSize.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = alignText
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
