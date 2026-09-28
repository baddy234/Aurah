package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog for adding custom streams from Suno (profile URL, song URL, CDN audio link, or generic audio stream)
 */
@Composable
fun AddStreamDialog(
    initialUrl: String = "",
    initialArtist: String = "",
    onDismiss: () -> Unit,
    onAddSunoProfile: (profileUrlOrHandle: String) -> Unit,
    onAddSingleStream: (url: String, title: String, artist: String, artworkUrl: String?, isVideo: Boolean) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (initialUrl.contains("suno.com/@")) 0 else 0) }
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    // Tab 0: Suno Account / Profile Stream
    var sunoInput by remember { mutableStateOf(if (initialUrl.isNotBlank()) initialUrl else "https://suno.com/@technicalrave7605") }
    var sunoArtistName by remember { mutableStateOf(if (initialArtist.isNotBlank()) initialArtist else "TechnicalRave") }

    // Tab 1: Direct Song or Stream Link
    var directUrl by remember { mutableStateOf(if (initialUrl.contains("suno.com/song") || initialUrl.contains(".mp3") || initialUrl.contains(".m3u8")) initialUrl else "") }
    var directTitle by remember { mutableStateOf("") }
    var directArtist by remember { mutableStateOf(if (initialArtist.isNotBlank()) initialArtist else "Suno AI Creator") }
    var directArtwork by remember { mutableStateOf("") }
    var isDirectVideo by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("add_suno_stream_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Add Suno / Custom Stream",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Stream directly into your library",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Suno Profile",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Direct Stream / Song",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Filled.Link, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TAB 0: SUNO PROFILE / ACCOUNT
                if (selectedTab == 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Enter your Suno profile URL or handle (e.g. suno.com/@technicalrave7605) to generate and sync your stream tracks.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        OutlinedTextField(
                            value = sunoInput,
                            onValueChange = { sunoInput = it },
                            label = { Text("Suno Profile URL or Handle") },
                            placeholder = { Text("https://suno.com/@technicalrave7605") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("suno_profile_input"),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        clipboardManager.getText()?.text?.let { clip ->
                                            if (clip.isNotBlank()) sunoInput = clip.trim()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentPaste,
                                        contentDescription = "Paste from clipboard",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )

                        OutlinedTextField(
                            value = sunoArtistName,
                            onValueChange = { sunoArtistName = it },
                            label = { Text("Artist / Profile Display Name") },
                            placeholder = { Text("TechnicalRave") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Quick presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quick Fill:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FilledTonalButton(
                                onClick = {
                                    sunoInput = "https://suno.com/@technicalrave7605"
                                    sunoArtistName = "TechnicalRave"
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("@technicalrave7605", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (sunoInput.isNotBlank()) {
                                    onAddSunoProfile(sunoInput.trim())
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("import_suno_profile_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import Suno Stream & Playlist", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // TAB 1: DIRECT STREAM / SONG URL
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = directUrl,
                            onValueChange = { url ->
                                directUrl = url
                                // Auto-populate title if Suno link
                                if (url.contains("suno.com/song/") && directTitle.isBlank()) {
                                    val songId = url.substringAfter("suno.com/song/").substringBefore("?").substringBefore("/")
                                    directTitle = "Suno Track ($songId)"
                                }
                            },
                            label = { Text("Stream / Audio / Video URL") },
                            placeholder = { Text("https://cdn1.suno.ai/... or https://suno.com/song/...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        clipboardManager.getText()?.text?.let { clip ->
                                            if (clip.isNotBlank()) directUrl = clip.trim()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentPaste,
                                        contentDescription = "Paste",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )

                        OutlinedTextField(
                            value = directTitle,
                            onValueChange = { directTitle = it },
                            label = { Text("Song / Stream Title") },
                            placeholder = { Text("e.g. Neon Horizon (Suno Rave)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = directArtist,
                            onValueChange = { directArtist = it },
                            label = { Text("Artist Name") },
                            placeholder = { Text("TechnicalRave") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = directArtwork,
                            onValueChange = { directArtwork = it },
                            label = { Text("Artwork / Cover Image URL (Optional)") },
                            placeholder = { Text("https://...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { isDirectVideo = !isDirectVideo },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isDirectVideo) "Type: Video Stream" else "Type: Audio Stream")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (directUrl.isNotBlank()) {
                                    val effectiveTitle = if (directTitle.isNotBlank()) directTitle.trim() else "Custom Stream"
                                    val effectiveArtist = if (directArtist.isNotBlank()) directArtist.trim() else "Suno Stream"
                                    onAddSingleStream(
                                        directUrl.trim(),
                                        effectiveTitle,
                                        effectiveArtist,
                                        if (directArtwork.isNotBlank()) directArtwork.trim() else null,
                                        isDirectVideo
                                    )
                                    onDismiss()
                                }
                            },
                            enabled = directUrl.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Library & Streams", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
