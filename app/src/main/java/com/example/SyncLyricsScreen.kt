package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaMetadata
import com.example.ui.theme.SurfaceGlass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncLyricsScreen(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    mediaMetadata: MediaMetadata?,
    rawLyrics: String,
    progressMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSaveSyncedLyrics: (String) -> Unit,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Parse plain text lyrics (ignoring existing tags for fresh sync if desired, but here we just take the text)
    val lines = remember(rawLyrics) {
        rawLyrics.lines()
            .map { it.replace(Regex("\\[\\d+:\\d+\\.\\d+\\]"), "").trim() }
            .filter { it.isNotEmpty() }
    }
    
    var currentLineIndex by remember { mutableStateOf(0) }
    var stampedLines by remember { mutableStateOf<List<Pair<Long, String>>>(emptyList()) }
    val listState = rememberLazyListState()

    // Auto-scroll to the current stamping line
    LaunchedEffect(currentLineIndex) {
        if (lines.isNotEmpty() && currentLineIndex < lines.size) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = maxOf(0, currentLineIndex - 2) // keep it somewhat centered
                )
            }
        }
    }

    // formatting helper
    val formatTimeStr: (Long) -> String = { ms ->
        val totalSecs = ms / 1000
        val m = totalSecs / 60
        val s = totalSecs % 60
        String.format("%d:%02d", m, s)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Sync Studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(displayTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Sync Studio")
                    }
                },
                actions = {
                    if (stampedLines.isNotEmpty()) {
                        TextButton(onClick = {
                            // Generate LRC content
                            val sb = StringBuilder()
                            stampedLines.forEach { (timeMs, text) ->
                                val min = timeMs / 60000
                                val sec = (timeMs % 60000) / 1000
                                val cs = (timeMs % 1000) / 10
                                sb.append(String.format("[%02d:%02d.%02d]%s\n", min, sec, cs, text))
                            }
                            // Add remaining unsynced lines at the end without stamps? Or just skip them.
                            // We will just leave them off for now, or append them without stamps.
                            // Usually, un-stamped lines just aren't included in synced playback, or they just show up at the very end.
                            // Let's just output the stamped ones.
                            onSaveSyncedLyrics(sb.toString())
                            onClose()
                        }) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress bar
                    Slider(
                        value = if (durationMs > 0) progressMs.toFloat() / durationMs.toFloat() else 0f,
                        onValueChange = { onSeekTo((it * durationMs).toLong()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTimeStr(progressMs), style = MaterialTheme.typography.bodySmall)
                        Text(formatTimeStr(durationMs), style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onSeekTo(maxOf(0, progressMs - 10000)) }) {
                            Icon(Icons.Filled.Replay10, contentDescription = "Rewind 10s", modifier = Modifier.size(32.dp))
                        }
                        
                        FloatingActionButton(
                            onClick = onPlayPause,
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        IconButton(onClick = { onSeekTo(minOf(durationMs, progressMs + 10000)) }) {
                            Icon(Icons.Filled.Forward10, contentDescription = "Forward 10s", modifier = Modifier.size(32.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentLineIndex < lines.size) {
                        Button(
                            onClick = {
                                val currentLineText = lines[currentLineIndex]
                                stampedLines = stampedLines + Pair(progressMs, currentLineText)
                                currentLineIndex++
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(Icons.Filled.Timer, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("STAMP: ${lines[currentLineIndex]}", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { /* Done */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = false
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("All lines stamped!", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Reset Button
                    if (stampedLines.isNotEmpty()) {
                        TextButton(onClick = {
                            stampedLines = emptyList()
                            currentLineIndex = 0
                            onSeekTo(0)
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Restart Syncing")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (lines.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No lyrics available to sync.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(lines) { index, line ->
                    val isPast = index < currentLineIndex
                    val isCurrent = index == currentLineIndex
                    
                    val alpha by animateFloatAsState(
                        targetValue = if (isCurrent) 1f else if (isPast) 0.5f else 0.3f,
                        animationSpec = tween(300)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(alpha)
                            .clickable {
                                // Allow jumping to a specific line to re-stamp from there
                                currentLineIndex = index
                                stampedLines = stampedLines.take(index)
                            },
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = line,
                            style = if (isCurrent) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
