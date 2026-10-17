package com.example

import android.app.SearchManager
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FrostedGlassSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditLyricsScreen(
    currentLyrics: String,
    progressMs: Long,
    durationMs: Long = 0L,
    isPlaying: Boolean = false,
    onTogglePlayPause: () -> Unit = {},
    onSeekTo: (Long) -> Unit = {},
    displayTitle: String,
    displayArtist: String,
    isFetchingLyrics: Boolean = false,
    lyricsFetchMessage: String? = null,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    onFetchLyrics: ((String, String) -> Unit)? = null,
    onClearLyricsMessage: (() -> Unit)? = null,
    onOpenImportLyrics: (() -> Unit)? = null,
    onOpenSyncStudio: (() -> Unit)? = null
) {
    LaunchedEffect(Unit) {
        if (currentLyrics.isBlank()) {
            onFetchLyrics?.invoke(displayTitle, displayArtist)
        }
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var editText by remember(currentLyrics) { mutableStateOf(currentLyrics) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Stamper, 1: Text Editor, 2: Tools & Sync
    var useMonospace by remember { mutableStateOf(false) }

    // Automatically close the feedback toast notification after 3.5 seconds
    LaunchedEffect(lyricsFetchMessage) {
        if (!lyricsFetchMessage.isNullOrBlank() && !lyricsFetchMessage.endsWith("...")) {
            delay(3500)
            onClearLyricsMessage?.invoke()
        }
    }

    androidx.activity.compose.BackHandler {
        onCancel()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lyrics & Timestamp Editor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$displayTitle • $displayArtist",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("cancel_edit_lyrics_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { onSave(editText) },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("save_lyrics_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWideScreen = maxWidth > 650.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // 1. Interactive Audio Playback Deck
                InteractiveAudioPlaybackDeck(
                    displayTitle = displayTitle,
                    displayArtist = displayArtist,
                    progressMs = progressMs,
                    durationMs = durationMs,
                    isPlaying = isPlaying,
                    onTogglePlayPause = onTogglePlayPause,
                    onSeekTo = onSeekTo,
                    onQuickStamp = {
                        val stamp = "[${LrcParser.formatTimestamp(progressMs)}]"
                        editText = if (editText.isBlank()) {
                            "$stamp "
                        } else {
                            "$editText\n$stamp "
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Progress / Global Alert
                if (isFetchingLyrics) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = !lyricsFetchMessage.isNullOrBlank(),
                    enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(250)),
                    exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                ) {
                    if (!lyricsFetchMessage.isNullOrBlank()) {
                        FrostedGlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = lyricsFetchMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                if (onClearLyricsMessage != null) {
                                    IconButton(
                                        onClick = onClearLyricsMessage,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Layout Body: Dual-column on wide screens vs. Tab navigation on compact screens
                if (isWideScreen) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left Column: Live Timestamp Stamper (50%)
                        Box(modifier = Modifier.weight(0.52f)) {
                            LiveTimestampStamperCard(
                                rawText = editText,
                                progressMs = progressMs,
                                isPlaying = isPlaying,
                                onSeekTo = onSeekTo,
                                onUpdateRawText = { editText = it },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Right Column: Manual Editor & Tools (48%)
                        Column(
                            modifier = Modifier.weight(0.48f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                LyricsEditorCard(
                                    editText = editText,
                                    progressMs = progressMs,
                                    useMonospace = useMonospace,
                                    onEditTextChange = { editText = it },
                                    onToggleMonospace = { useMonospace = !useMonospace },
                                    onPasteClipboard = {
                                        val clip = clipboardManager.getText()?.text
                                        if (!clip.isNullOrBlank()) {
                                            editText = clip
                                        }
                                    },
                                    onCopyAll = {
                                        clipboardManager.setText(AnnotatedString(editText))
                                    },
                                    onClearAll = { editText = "" },
                                    onInsertCurrentTimestamp = {
                                        val stamp = "[${LrcParser.formatTimestamp(progressMs)}]"
                                        editText = if (editText.isBlank()) stamp else "$editText\n$stamp "
                                    },
                                    onClearTimestamps = {
                                        editText = LrcParser.toPlainText(editText)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Mobile Compact Navigation Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Live Stamper", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Text Editor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Tools", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        )
                    }

                    // Mobile Tab Content
                    Crossfade(targetState = selectedTab, label = "TabCrossfade") { tab ->
                        when (tab) {
                            0 -> LiveTimestampStamperCard(
                                rawText = editText,
                                progressMs = progressMs,
                                isPlaying = isPlaying,
                                onSeekTo = onSeekTo,
                                onUpdateRawText = { editText = it },
                                modifier = Modifier.fillMaxSize()
                            )
                            1 -> LyricsEditorCard(
                                editText = editText,
                                progressMs = progressMs,
                                useMonospace = useMonospace,
                                onEditTextChange = { editText = it },
                                onToggleMonospace = { useMonospace = !useMonospace },
                                onPasteClipboard = {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        editText = clip
                                    }
                                },
                                onCopyAll = {
                                    clipboardManager.setText(AnnotatedString(editText))
                                },
                                onClearAll = { editText = "" },
                                onInsertCurrentTimestamp = {
                                    val stamp = "[${LrcParser.formatTimestamp(progressMs)}]"
                                    editText = if (editText.isBlank()) stamp else "$editText\n$stamp "
                                },
                                onClearTimestamps = {
                                    editText = LrcParser.toPlainText(editText)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                            2 -> LyricsToolsHub(
                                displayTitle = displayTitle,
                                displayArtist = displayArtist,
                                onOpenSyncStudio = onOpenSyncStudio,
                                onOpenImportLyrics = onOpenImportLyrics,
                                onFetchLyrics = onFetchLyrics,
                                onShiftTimestamps = { offsetMs ->
                                    val lines = LrcParser.parse(editText)
                                    if (lines.isNotEmpty()) {
                                        val shifted = lines.map {
                                            it.copy(timeMs = (it.timeMs + offsetMs).coerceAtLeast(0L))
                                        }
                                        editText = LrcParser.toLrcString(shifted)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Audio Playback Deck:
 * Allows user to play, pause, seek, jump -3s/+3s, and view current high-precision song time.
 */
@Composable
private fun InteractiveAudioPlaybackDeck(
    displayTitle: String,
    displayArtist: String,
    progressMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onQuickStamp: () -> Unit,
    modifier: Modifier = Modifier
) {
    FrostedGlassSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Row 1: Track Details & Live Timestamp Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = displayArtist,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // High precision time badge
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "[${LrcParser.formatTimestamp(progressMs)}]",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Row 2: Scrub Slider & Time Label
            val safeDuration = durationMs.coerceAtLeast(1L).toFloat()
            val sliderValue = progressMs.coerceIn(0L, durationMs.coerceAtLeast(1L)).toFloat()

            Slider(
                value = sliderValue,
                onValueChange = { onSeekTo(it.toLong()) },
                valueRange = 0f..safeDuration,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )

            // Row 3: Playback Controls (Play/Pause, -3s, +3s, and Stamp button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Playback button group
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Rewind 3s
                    FilledTonalButton(
                        onClick = { onSeekTo((progressMs - 3000L).coerceAtLeast(0L)) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("-3s", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // Play / Pause
                    Button(
                        onClick = onTogglePlayPause,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "Pause" else "Play",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Forward 3s
                    FilledTonalButton(
                        onClick = { onSeekTo((progressMs + 3000L).coerceAtMost(durationMs)) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+3s", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick Stamp Button
                FilledTonalButton(
                    onClick = onQuickStamp,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "+ Stamp",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Live Timestamp Stamper Card:
 * Line-by-line interactive editor where users can listen to the track and stamp timestamps in real time.
 */
@Composable
private fun LiveTimestampStamperCard(
    rawText: String,
    progressMs: Long,
    isPlaying: Boolean,
    onSeekTo: (Long) -> Unit,
    onUpdateRawText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Parse lines into editable structures
    data class LineItem(
        val originalIndex: Int,
        val timestampMs: Long?,
        val text: String
    )

    val lines = remember(rawText) {
        val rawLines = rawText.lines()
        val result = mutableListOf<LineItem>()
        val tsRegex = Regex("\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{1,3}))?\\]")

        rawLines.forEachIndexed { idx, raw ->
            val match = tsRegex.find(raw)
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val fracStr = match.groupValues[3]
                val frac = when (fracStr.length) {
                    1 -> (fracStr.toLongOrNull() ?: 0L) * 100L
                    2 -> (fracStr.toLongOrNull() ?: 0L) * 10L
                    else -> (fracStr.take(3).toLongOrNull() ?: 0L)
                }
                val time = (min * 60000L) + (sec * 1000L) + frac
                val clean = raw.replace(match.value, "").trim()
                result.add(LineItem(idx, time, clean))
            } else {
                result.add(LineItem(idx, null, raw.trim()))
            }
        }
        result
    }

    var activeLineIndex by remember { mutableIntStateOf(0) }

    // Helper to serialize back to LRC
    fun syncBack(updatedLines: List<LineItem>) {
        val serialized = updatedLines.joinToString("\n") { line ->
            if (line.timestampMs != null) {
                "[${LrcParser.formatTimestamp(line.timestampMs)}] ${line.text}"
            } else {
                line.text
            }
        }
        onUpdateRawText(serialized)
    }

    // Auto-scroll to active stamping line
    LaunchedEffect(activeLineIndex) {
        if (lines.isNotEmpty() && activeLineIndex in lines.indices) {
            coroutineScope.launch {
                listState.animateScrollToItem((activeLineIndex - 1).coerceAtLeast(0))
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top Stamper Action Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (lines.isEmpty()) "No lyrics loaded" else "Line ${activeLineIndex + 1} of ${lines.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap stamp to record song time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Big "Stamp Active Line" button
                    Button(
                        onClick = {
                            if (lines.isNotEmpty() && activeLineIndex in lines.indices) {
                                val updated = lines.toMutableList()
                                val cur = updated[activeLineIndex]
                                updated[activeLineIndex] = cur.copy(timestampMs = progressMs)
                                syncBack(updated)
                                if (activeLineIndex < lines.size - 1) {
                                    activeLineIndex += 1
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Stamp Line [${LrcParser.formatTimestamp(progressMs)}]",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Add new line button
                    FilledTonalButton(
                        onClick = {
                            val updated = lines.toMutableList()
                            val insertIdx = (activeLineIndex + 1).coerceAtMost(updated.size)
                            updated.add(insertIdx, LineItem(insertIdx, progressMs, "New lyric line"))
                            syncBack(updated)
                            activeLineIndex = insertIdx
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Line", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Line-by-Line List
        if (lines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No lyrics lines available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onUpdateRawText("First lyric line\nSecond lyric line\nThird lyric line")
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Sample Lines")
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(lines) { index, item ->
                    val isActive = index == activeLineIndex
                    var isEditingLine by remember { mutableStateOf(false) }
                    var lineEditText by remember(item.text) { mutableStateOf(item.text) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeLineIndex = index },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            }
                        ),
                        border = if (isActive) {
                            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Line Number Badge
                            Surface(
                                shape = CircleShape,
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            // Timestamp Badge (Clickable to Seek)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (item.timestampMs != null) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                },
                                modifier = Modifier.clickable {
                                    if (item.timestampMs != null) {
                                        onSeekTo(item.timestampMs)
                                    } else {
                                        val updated = lines.toMutableList()
                                        updated[index] = item.copy(timestampMs = progressMs)
                                        syncBack(updated)
                                    }
                                }
                            ) {
                                Text(
                                    text = if (item.timestampMs != null) {
                                        "[${LrcParser.formatTimestamp(item.timestampMs)}]"
                                    } else {
                                        "[+ Set Time]"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.timestampMs != null) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            // Lyric Text or Inline Editor
                            if (isEditingLine) {
                                OutlinedTextField(
                                    value = lineEditText,
                                    onValueChange = { lineEditText = it },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            val updated = lines.toMutableList()
                                            updated[index] = item.copy(text = lineEditText)
                                            syncBack(updated)
                                            isEditingLine = false
                                        }) {
                                            Icon(Icons.Filled.Check, contentDescription = "Done", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                )
                            } else {
                                Text(
                                    text = if (item.text.isBlank()) "(Empty line)" else item.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isEditingLine = true },
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(Modifier.width(4.dp))

                            // Action buttons: Re-stamp and Delete
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val updated = lines.toMutableList()
                                        updated[index] = item.copy(timestampMs = progressMs)
                                        syncBack(updated)
                                        activeLineIndex = (index + 1).coerceAtMost(lines.size - 1)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Timer,
                                        contentDescription = "Stamp current time",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val updated = lines.toMutableList()
                                        updated.removeAt(index)
                                        syncBack(updated)
                                        activeLineIndex = activeLineIndex.coerceAtMost(updated.size - 1).coerceAtLeast(0)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete Line",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
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

/**
 * Freeform Text Editor Card:
 * Full multiline editor with timestamp injection toolbar.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LyricsEditorCard(
    editText: String,
    progressMs: Long,
    useMonospace: Boolean,
    onEditTextChange: (String) -> Unit,
    onToggleMonospace: () -> Unit,
    onPasteClipboard: () -> Unit,
    onCopyAll: () -> Unit,
    onClearAll: () -> Unit,
    onInsertCurrentTimestamp: () -> Unit,
    onClearTimestamps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lineCount = remember(editText) { if (editText.isBlank()) 0 else editText.lines().size }
    val charCount = remember(editText) { editText.length }
    val hasTimestamps = remember(editText) { editText.contains("[") && editText.contains("]") }

    Column(modifier = modifier.fillMaxSize()) {
        // Toolbar with Stats & Quick Actions
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Stats Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$lineCount lines",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$charCount chars",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (hasTimestamps) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LRC Timed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Quick Tool Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Insert Timestamp at cursor
                FilledTonalButton(
                    onClick = onInsertCurrentTimestamp,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ Stamp Time", style = MaterialTheme.typography.labelSmall)
                }

                // Strip Timestamps
                if (hasTimestamps) {
                    TextButton(
                        onClick = onClearTimestamps,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Strip Time", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Font Toggle
                TextButton(
                    onClick = onToggleMonospace,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (useMonospace) "Mono" else "Sans",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Copy
                IconButton(onClick = onCopyAll, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy All", modifier = Modifier.size(15.dp))
                }

                // Paste
                IconButton(onClick = onPasteClipboard, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.ContentPaste, contentDescription = "Paste Clipboard", modifier = Modifier.size(15.dp))
                }

                // Clear
                if (editText.isNotEmpty()) {
                    IconButton(onClick = onClearAll, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear All", modifier = Modifier.size(15.dp))
                    }
                }
            }
        }

        // Frosted Text Input Canvas
        OutlinedTextField(
            value = editText,
            onValueChange = onEditTextChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Type or paste lyrics here...\n\nTap '+ Stamp Time' or the Playback Deck to record timestamps automatically in real time:\n[00:15.20] First line of lyrics\n[00:28.45] Second line of lyrics",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = if (useMonospace) FontFamily.Monospace else FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.60f)
            )
        )
    }
}

/**
 * Tools & Sync Hub:
 * Provides LRCLIB online fetching, file importing, and timestamp shifting.
 */
@Composable
private fun LyricsToolsHub(
    displayTitle: String,
    displayArtist: String,
    onOpenSyncStudio: (() -> Unit)?,
    onOpenImportLyrics: (() -> Unit)?,
    onFetchLyrics: ((String, String) -> Unit)?,
    onShiftTimestamps: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Lyrics Sync & Import Hub",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // 1. Shift Timestamps
        if (onShiftTimestamps != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Timestamp Offset Shift",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nudge all timestamps earlier or later to fix audio latency.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onShiftTimestamps(-500L) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("-0.5s", style = MaterialTheme.typography.labelSmall)
                        }
                        FilledTonalButton(
                            onClick = { onShiftTimestamps(-100L) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("-0.1s", style = MaterialTheme.typography.labelSmall)
                        }
                        FilledTonalButton(
                            onClick = { onShiftTimestamps(100L) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+0.1s", style = MaterialTheme.typography.labelSmall)
                        }
                        FilledTonalButton(
                            onClick = { onShiftTimestamps(500L) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+0.5s", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 2. Fetch Online from LRCLIB
        if (onFetchLyrics != null) {
            Card(
                onClick = { onFetchLyrics(displayTitle, displayArtist) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Search Online LRCLIB",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Download verified community-synchronized lyrics for '$displayTitle'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // 3. Import File
        if (onOpenImportLyrics != null) {
            Card(
                onClick = onOpenImportLyrics,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.FileOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import .LRC File",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pick a local .lrc or .txt lyrics file from device storage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
