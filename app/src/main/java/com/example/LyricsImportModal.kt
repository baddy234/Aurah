package com.example

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FrostedGlassDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class LyricsImportTab(val title: String) {
    FILE("Local File"),
    CLIPBOARD("Clipboard"),
    WEB_LINK("Web Link")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsImportModal(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    onDismiss: () -> Unit,
    onImportLyrics: (lyrics: String, sourceDescription: String) -> Unit,
    onImportFromUri: (Uri) -> Unit,
    onFetchFromUrl: (String) -> Unit,
    isFetching: Boolean = false,
    feedbackMessage: String? = null,
    onClearFeedbackMessage: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(LyricsImportTab.FILE) }

    // File tab state
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileText by remember { mutableStateOf<String?>(null) }
    var isReadingFile by remember { mutableStateOf(false) }

    // Clipboard tab state
    var clipboardText by remember { mutableStateOf("") }

    // Web link tab state
    var webUrl by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            isReadingFile = true
            scope.launch(Dispatchers.IO) {
                var fileName = "lyrics.lrc"
                try {
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            fileName = cursor.getString(nameIndex) ?: "lyrics.lrc"
                        }
                    }
                    val text = context.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.bufferedReader(Charsets.UTF_8).readText()
                    } ?: ""

                    withContext(Dispatchers.Main) {
                        selectedFileName = fileName
                        selectedFileText = text
                        isReadingFile = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isReadingFile = false
                        selectedFileName = "Failed to load file"
                        selectedFileText = null
                    }
                }
            }
        }
    }

    // Refresh clipboard on tab switch to clipboard
    LaunchedEffect(selectedTab) {
        if (selectedTab == LyricsImportTab.CLIPBOARD) {
            val clip = clipboardManager.getText()?.text ?: ""
            clipboardText = clip
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .testTag("lyrics_import_modal")
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
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
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Import Lyrics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$displayTitle • $displayArtist",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp).testTag("dismiss_lyrics_import_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Tab Navigation Pill Bar
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LyricsImportTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            onClick = { selectedTab = tab },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("import_tab_${tab.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = when (tab) {
                                        LyricsImportTab.FILE -> Icons.Filled.FolderOpen
                                        LyricsImportTab.CLIPBOARD -> Icons.Filled.ContentPaste
                                        LyricsImportTab.WEB_LINK -> Icons.Filled.Link
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Feedback Banner if any
            if (!feedbackMessage.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClearFeedbackMessage, modifier = Modifier.size(22.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Tab Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    LyricsImportTab.FILE -> {
                        FileImportContent(
                            isReadingFile = isReadingFile,
                            fileName = selectedFileName,
                            fileText = selectedFileText,
                            onPickFile = {
                                filePickerLauncher.launch(
                                    arrayOf("text/*", "application/octet-stream", "*/*")
                                )
                            },
                            onApply = {
                                val text = selectedFileText
                                if (!text.isNullOrBlank()) {
                                    val name = selectedFileName ?: "file.lrc"
                                    onImportLyrics(text, name)
                                    onDismiss()
                                }
                            }
                        )
                    }

                    LyricsImportTab.CLIPBOARD -> {
                        ClipboardImportContent(
                            clipboardText = clipboardText,
                            onRefreshClipboard = {
                                clipboardText = clipboardManager.getText()?.text ?: ""
                            },
                            onTextChange = { clipboardText = it },
                            onApply = {
                                if (clipboardText.isNotBlank()) {
                                    onImportLyrics(clipboardText, "clipboard")
                                    onDismiss()
                                }
                            }
                        )
                    }

                    LyricsImportTab.WEB_LINK -> {
                        WebLinkImportContent(
                            url = webUrl,
                            onUrlChange = { webUrl = it },
                            isFetching = isFetching,
                            onPasteUrl = {
                                val clip = clipboardManager.getText()?.text ?: ""
                                if (clip.startsWith("http://") || clip.startsWith("https://")) {
                                    webUrl = clip
                                }
                            },
                            onFetch = {
                                if (webUrl.isNotBlank()) {
                                    onFetchFromUrl(webUrl.trim())
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileImportContent(
    isReadingFile: Boolean,
    fileName: String?,
    fileText: String?,
    onPickFile: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isReadingFile) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Reading file content...", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else if (fileText == null) {
            // Empty / Browse State
            Surface(
                onClick = onPickFile,
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("browse_lyrics_file_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "Select .lrc or .txt File",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Supports standard LRC files with timestamps like [00:15.30] or plain text lyrics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = onPickFile,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("browse_lyrics_file_button")
                    ) {
                        Icon(Icons.Filled.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Browse Device Files")
                    }
                }
            }
        } else {
            // File Loaded & Inspected State
            val parsedLines = remember(fileText) { LrcParser.parse(fileText) }
            val hasTimestamps = parsedLines.isNotEmpty()
            val totalLineCount = remember(fileText) { fileText.lines().filter { it.isNotBlank() }.size }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (hasTimestamps) Icons.Filled.Timer else Icons.Filled.Description,
                                contentDescription = null,
                                tint = if (hasTimestamps) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = fileName ?: "file.lrc",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        TextButton(onClick = onPickFile) {
                            Text("Change")
                        }
                    }

                    // Format Badge
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (hasTimestamps) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = if (hasTimestamps) "✓ Synchronized LRC" else "Plain Text Format",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hasTimestamps) Color(0xFF10B981) else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (hasTimestamps) "${parsedLines.size} sync points • $totalLineCount lines" else "$totalLineCount lines",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Scrollable text preview box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = fileText.take(1500),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            if (fileText.length > 1500) {
                                Text(
                                    text = "... (+ ${fileText.length - 1500} more characters)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Apply Button
            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_imported_lyrics_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (hasTimestamps) "Apply Synchronized Lyrics" else "Apply Plain Lyrics",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ClipboardImportContent(
    clipboardText: String,
    onRefreshClipboard: () -> Unit,
    onTextChange: (String) -> Unit,
    onApply: () -> Unit
) {
    val parsedLines = remember(clipboardText) { LrcParser.parse(clipboardText) }
    val hasTimestamps = parsedLines.isNotEmpty()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Paste or Edit Lyrics Text",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(
                onClick = onRefreshClipboard,
                modifier = Modifier.testTag("refresh_clipboard_button")
            ) {
                Icon(Icons.Filled.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Paste Clipboard")
            }
        }

        OutlinedTextField(
            value = clipboardText,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp, max = 220.dp)
                .testTag("clipboard_lyrics_input"),
            placeholder = {
                Text(
                    "Paste lyrics here...\nSupports standard LRC tags like:\n[00:12.34] Singing first line\n[00:25.80] Second line...",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            shape = RoundedCornerShape(14.dp)
        )

        if (clipboardText.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (hasTimestamps) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (hasTimestamps) Icons.Filled.CheckCircle else Icons.Filled.Info,
                        contentDescription = null,
                        tint = if (hasTimestamps) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (hasTimestamps) "Detected ${parsedLines.size} synchronized lines" else "Plain text lyrics (no timestamps detected)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hasTimestamps) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_clipboard_lyrics_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Apply to Track", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun WebLinkImportContent(
    url: String,
    onUrlChange: (String) -> Unit,
    isFetching: Boolean,
    onPasteUrl: () -> Unit,
    onFetch: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Direct Raw Lyrics URL",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Enter a direct web link to a .lrc or raw text file (e.g., GitHub raw or Pastebin):",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("URL Link (https://...)") },
            placeholder = { Text("https://example.com/lyrics.lrc") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = { onFetch() }),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (url.isNotBlank()) {
                        IconButton(onClick = { onUrlChange("") }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                    IconButton(onClick = onPasteUrl, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(16.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weblink_lyrics_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Button(
            onClick = onFetch,
            enabled = url.isNotBlank() && !isFetching,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("fetch_weblink_lyrics_button")
        ) {
            if (isFetching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Fetching Lyrics...")
            } else {
                Icon(Icons.Filled.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Download & Import", fontWeight = FontWeight.Bold)
            }
        }
    }
}
