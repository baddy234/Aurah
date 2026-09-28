package com.example
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryUltraviolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TertiaryEmerald

@Composable
fun GlassmorphicCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
    content: @Composable () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    borderColor,
                    borderColor.copy(alpha = 0.1f)
                )
            )
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsScreen(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val currentSubPage = state.currentSettingsSubPage

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Settings Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentSubPage != null) {
                        viewModel.backFromSettingsSubPage()
                    } else {
                        viewModel.closeSettings()
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            val strings = appStrings()
            Column {
                Text(
                    text = currentSubPage?.getLocalizedTitle(strings) ?: strings.settingsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentSubPage != null) "Customize your experience" else "Preferences & Configurations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Body Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (currentSubPage == null) {
                MainSettingsList(state = state, viewModel = viewModel)
            } else {
                when (currentSubPage) {
                    SettingsSubPage.PROFILE -> ProfileSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.PLAYTIME -> PlaytimeSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.THEME -> ThemeSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.SLEEP_TIMER -> SleepTimerSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.PLAYBACK -> PlaybackSettingsSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.VIDEO -> VideoPlayerSettingsSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.VISUALIZER -> VisualizerSettingsSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.NOTIFICATIONS -> NotificationSettingsSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.LANGUAGE -> LanguageSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.FEEDBACK -> FeedbackSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.ABOUT -> AboutSubPage(state = state, viewModel = viewModel)
                    SettingsSubPage.LYRICS -> LyricsSettingsSubPage(state = state, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun MainSettingsList(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Quick Profile Banner Card
        item {
            GlassmorphicCard(
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.PROFILE) },
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
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
                            text = state.profile.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.profile.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PRO",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Text(
                            text = state.profile.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.profile.bio,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1. Profile Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.Person,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "Profile",
                subtitle = "Name, avatar, bio & music preferences",
                testTag = "settings_card_profile",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.PROFILE) }
            )
        }

        // 2. Playtime Card
        item {
            val totalHours = state.totalPlaytimeSeconds / 3600
            val totalMins = (state.totalPlaytimeSeconds % 3600) / 60
            SettingsFeatureCard(
                icon = Icons.Filled.Headphones,
                iconTint = SecondaryCyan,
                title = "Playtime & Stats",
                subtitle = "${totalHours}h ${totalMins}m total listened • Goal: ${state.dailyPlaytimeGoalMinutes}m/day",
                testTag = "settings_card_playtime",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.PLAYTIME) }
            )
        }

        // 3. Theme Toggle Card (with direct inline Switch toggle + page navigation)
        item {
            GlassmorphicCard(
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.THEME) },
                modifier = Modifier.testTag("settings_card_theme")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                (if (state.isDarkTheme) Color(0xFF6366F1) else Color(0xFFF59E0B)).copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = if (state.isDarkTheme) Color(0xFF818CF8) else Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Theme & Display",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Current: ${state.themeMode} mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Direct toggle switch
                    Switch(
                        checked = state.isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // 4. The Sleep Timer Card
        item {
            val sleepSubtitle = if (state.sleepTimerActive) {
                val remMins = (state.sleepTimerRemainingMs / 60000L).coerceAtLeast(1L)
                "Active: ~$remMins min remaining"
            } else {
                "Timer is off • Auto-pause music"
            }
            SettingsFeatureCard(
                icon = Icons.Filled.Timer,
                iconTint = Color(0xFFF59E0B),
                title = "Sleep Timer",
                subtitle = sleepSubtitle,
                testTag = "settings_card_sleep_timer",
                badgeText = if (state.sleepTimerActive) "RUNNING" else null,
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.SLEEP_TIMER) }
            )
        }

        // 5. Playback Settings Card
        item {
            val eqStatus = if (state.isEqEnabled) {
                val presetName = if (state.currentEqPreset.toInt() in state.eqPresets.indices) {
                    state.eqPresets[state.currentEqPreset.toInt()]
                } else "Custom"
                "EQ: $presetName (Active)"
            } else {
                "Equalizer Off"
            }
            SettingsFeatureCard(
                icon = Icons.Filled.Tune,
                iconTint = TertiaryEmerald,
                title = "Playback & Equalizer",
                subtitle = "$eqStatus • ${state.playbackPrefs.audioQuality}",
                testTag = "settings_card_playback",
                badgeText = if (state.isEqEnabled) "EQ ON" else null,
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.PLAYBACK) }
            )
        }

        // 5b. Video Player Settings Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.SmartDisplay,
                iconTint = SecondaryCyan,
                title = "Video Player Settings",
                subtitle = "${state.videoPrefs.defaultQuality} • ${state.videoPrefs.defaultResizeMode} • ${state.videoPrefs.seekJumpSeconds}s skip",
                testTag = "settings_card_video_player",
                badgeText = if (state.videoPrefs.backgroundAudioPlayback) "BG AUDIO" else null,
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.VIDEO) }
            )
        }

        item {
            SettingsFeatureCard(
                icon = Icons.Filled.AutoAwesome,
                iconTint = Color(0xFFF59E0B),
                title = "Visualizer Settings",
                subtitle = "Themes, Mode & Sensitivity",
                testTag = "settings_card_visualizer",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.VISUALIZER) }
            )
        }

        // 6. Lyrics Settings Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.QueueMusic,
                iconTint = Color(0xFF8B5CF6),
                title = "Lyrics Display",
                subtitle = "Default view mode & text size",
                testTag = "settings_card_lyrics",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.LYRICS) }
            )
        }

        // 7. Notification Settings Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.Notifications,
                iconTint = Color(0xFFEC4899),
                title = "Notification Settings",
                subtitle = "Lock screen player, artwork & haptics",
                testTag = "settings_card_notifications",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.NOTIFICATIONS) }
            )
        }

        // 7. Language Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.Language,
                iconTint = Color(0xFF3B82F6),
                title = "Language",
                subtitle = state.selectedLanguage,
                testTag = "settings_card_language",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.LANGUAGE) }
            )
        }

        // 8. Feedback Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.Feedback,
                iconTint = Color(0xFF14B8A6),
                title = "Feedback & Support",
                subtitle = "Report issues, feature requests & ratings",
                testTag = "settings_card_feedback",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.FEEDBACK) }
            )
        }

        // 9. About Card
        item {
            SettingsFeatureCard(
                icon = Icons.Filled.Info,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "About Aura",
                subtitle = "v1.2.0 • Open source licenses & legal",
                testTag = "settings_card_about",
                onClick = { viewModel.navigateToSettingsSubPage(SettingsSubPage.ABOUT) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsFeatureCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        onClick = onClick,
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(iconTint.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = iconTint,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ------------------------------------------------------------------------
// 1. Profile SubPage
// ------------------------------------------------------------------------
@Composable
private fun ProfileSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    var name by remember { mutableStateOf(state.profile.name) }
    var bio by remember { mutableStateOf(state.profile.bio) }
    var favoriteGenre by remember { mutableStateOf(state.profile.favoriteGenre) }
    var showSavedDialog by remember { mutableStateOf(false) }

    val avatarColors = listOf("FF8B5CF6", "FF06B6D4", "FF10B981", "FFEC4899", "FFF59E0B")
    var selectedColor by remember { mutableStateOf(state.profile.avatarColor) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Avatar Section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(android.graphics.Color.parseColor("#$selectedColor")),
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(2).uppercase().ifBlank { "ME" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Select Avatar Color",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    avatarColors.forEach { colStr ->
                        val col = Color(android.graphics.Color.parseColor("#$colStr"))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(col)
                                .clickable { selectedColor = colStr }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colStr) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = state.profile.email,
            onValueChange = {},
            label = { Text("Email Address") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio / Status") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = favoriteGenre,
            onValueChange = { favoriteGenre = it },
            label = { Text("Favorite Music Genre") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        // Account Stats Summary
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.favoriteTrackIds.size}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.playlists.size}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryCyan
                    )
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.deviceTracks.size}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryEmerald
                    )
                    Text(
                        text = "Local Songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.updateProfile(name, bio, selectedColor, favoriteGenre)
                showSavedDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
        }
    }

    if (showSavedDialog) {
        AlertDialog(
            onDismissRequest = { showSavedDialog = false },
            confirmButton = {
                TextButton(onClick = { showSavedDialog = false }) {
                    Text("Done")
                }
            },
            title = { Text("Profile Updated") },
            text = { Text("Your profile settings have been successfully saved.") }
        )
    }
}

// ------------------------------------------------------------------------
// 2. Playtime SubPage
// ------------------------------------------------------------------------
@Composable
private fun PlaytimeSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    val totalHours = state.totalPlaytimeSeconds / 3600
    val totalMins = (state.totalPlaytimeSeconds % 3600) / 60
    var dailyGoal by remember { mutableFloatStateOf(state.dailyPlaytimeGoalMinutes.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Playtime Card
        GlassmorphicCard(
            onClick = {},
            containerColor = SecondaryCyan.copy(alpha = 0.15f),
            borderColor = SecondaryCyan.copy(alpha = 0.4f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Playback Time",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${totalHours}h ${totalMins}m",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = SecondaryCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Across all your listening sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Daily Listening Goal
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Listening Goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${dailyGoal.toInt()} mins",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = dailyGoal,
                    onValueChange = { dailyGoal = it },
                    onValueChangeFinished = {
                        viewModel.setDailyPlaytimeGoal(dailyGoal.toInt())
                    },
                    valueRange = 15f..240f,
                    steps = 14
                )
                Text(
                    text = "Target duration for your daily music immersion.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 7-day Listening Chart Visualizer
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "This Week's Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 1.0f, 0.8f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.zip(heights).forEach { (day, h) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height((90 * h).dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                SecondaryCyan
                                            )
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Most Played Track
        if (state.recentlyPlayed.isNotEmpty()) {
            val topTrack = state.recentlyPlayed.first()
            GlassmorphicCard(onClick = {}) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TertiaryEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = TertiaryEmerald
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Top Track This Week",
                            style = MaterialTheme.typography.labelSmall,
                            color = TertiaryEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = topTrack.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = topTrack.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// 3. Merged Theme & Display SubPage
// ------------------------------------------------------------------------
@Composable
private fun ThemeSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("App Theme", "Visualizer", "Lyrics Display", "Video Canvas")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> AppThemeSettingsTab(state = state, viewModel = viewModel)
                1 -> VisualizerSettingsSubPage(state = state, viewModel = viewModel)
                2 -> LyricsSettingsSubPage(state = state, viewModel = viewModel)
                3 -> VideoPlayerSettingsSubPage(state = state, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AppThemeSettingsTab(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    val themeOptions = listOf("Dark", "Light", "System")
    val accentColors = listOf(
        "Ultraviolet" to Color(0xFF8B5CF6),
        "Cyan" to Color(0xFF06B6D4),
        "Emerald" to Color(0xFF10B981),
        "Amber" to Color(0xFFF59E0B),
        "Rose Pink" to Color(0xFFEC4899),
        "Indigo" to Color(0xFF6366F1)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Color Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        themeOptions.forEach { mode ->
            GlassmorphicCard(
                onClick = { viewModel.setThemeMode(mode) },
                containerColor = if (state.themeMode == mode) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                },
                borderColor = if (state.themeMode == mode) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (mode) {
                                "Dark" -> Icons.Filled.DarkMode
                                "Light" -> Icons.Filled.LightMode
                                else -> Icons.Filled.ColorLens
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "$mode Theme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (mode) {
                                    "Dark" -> "High contrast dark canvas for night listening"
                                    "Light" -> "Clean and luminous palette for daylight"
                                    else -> "Automatically match device system theme"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    RadioButton(
                        selected = state.themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Accent Color Palette",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            accentColors.forEachIndexed { index, (name, col) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(col)
                            .clickable { viewModel.setAccentColor(index) }
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.accentColorIndex == index) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Material You Switch
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dynamic Colors (Material You)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Derive player palette from your device wallpaper (Android 12+)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.dynamicColor,
                    onCheckedChange = { viewModel.setDynamicColor(it) }
                )
            }
        }
    }
}

// ------------------------------------------------------------------------
// 4. The Sleep Timer SubPage
// ------------------------------------------------------------------------
@Composable
private fun SleepTimerSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    var customMinutes by remember { mutableFloatStateOf(30f) }
    var stopAtEndOfSong by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Status Card
        GlassmorphicCard(
            onClick = {},
            containerColor = if (state.sleepTimerActive) Color(0xFFF59E0B).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            borderColor = if (state.sleepTimerActive) Color(0xFFF59E0B).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (state.sleepTimerActive) {
                        val remMins = (state.sleepTimerRemainingMs / 60000L).coerceAtLeast(1L)
                        "Timer Running: ~$remMins min left"
                    } else {
                        "No Active Timer"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (state.sleepTimerActive) {
                        "Playback will automatically fade and stop when timer completes."
                    } else {
                        "Set a duration to stop playback automatically when you fall asleep."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (state.sleepTimerActive) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.cancelSleepTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel Active Timer")
                    }
                }
            }
        }

        Text(
            text = "Quick Presets",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        val presets = listOf(15, 30, 45, 60, 90)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { mins ->
                Button(
                    onClick = {
                        viewModel.setSleepTimer(mins)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("${mins}m", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Custom Slider
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custom Duration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${customMinutes.toInt()} minutes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = customMinutes,
                    onValueChange = { customMinutes = it },
                    valueRange = 5f..120f,
                    steps = 22
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.setSleepTimer(customMinutes.toInt())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text("Start ${customMinutes.toInt()} Min Timer", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Extra Options
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Wait for Track to Finish",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Avoid abruptly stopping in the middle of a song",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = stopAtEndOfSong,
                    onCheckedChange = { stopAtEndOfSong = it }
                )
            }
        }
    }
}

// ------------------------------------------------------------------------
// 5. Playback Settings SubPage
// ------------------------------------------------------------------------
@Composable
private fun PlaybackSettingsSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    val prefs = state.playbackPrefs
    var crossfade by remember { mutableFloatStateOf(prefs.crossfadeSeconds.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Interactive Equalizer Master Card
        EqualizerMasterCard(state = state, viewModel = viewModel)

        // 2. DSP & Acoustic Enhancement Card
        AudioEnhancementCard(state = state, viewModel = viewModel)

        // 3. Audio Quality
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Streaming Audio Quality",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                val qualities = listOf("Normal (160 kbps)", "High (320 kbps)", "Lossless (FLAC)")
                qualities.forEach { q ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updatePlaybackPrefs(prefs.copy(audioQuality = q))
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = q,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        RadioButton(
                            selected = prefs.audioQuality == q,
                            onClick = {
                                viewModel.updatePlaybackPrefs(prefs.copy(audioQuality = q))
                            }
                        )
                    }
                }
            }
        }

        // Crossfade
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crossfade Duration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (crossfade.toInt() == 0) "Off" else "${crossfade.toInt()}s",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryEmerald
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = crossfade,
                    onValueChange = { crossfade = it },
                    onValueChangeFinished = {
                        viewModel.updatePlaybackPrefs(prefs.copy(crossfadeSeconds = crossfade.toInt()))
                    },
                    valueRange = 0f..12f,
                    steps = 11
                )
                Text(
                    text = "Seamlessly blend transitions between consecutive songs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Gapless Playback
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gapless Playback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Eliminate silence between live albums and continuous mixes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.isGapless,
                    onCheckedChange = {
                        viewModel.updatePlaybackPrefs(prefs.copy(isGapless = it))
                    }
                )
            }
        }

        // Auto-play similar songs
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Automix Similar Music",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Keep music playing when playlist reaches the end",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.autoPlaySimilar,
                    onCheckedChange = {
                        viewModel.updatePlaybackPrefs(prefs.copy(autoPlaySimilar = it))
                    }
                )
            }
        }

        // Normalize Volume
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Normalize Volume (ReplayGain)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Maintain consistent audio level across loud and quiet songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.normalizeVolume,
                    onCheckedChange = {
                        viewModel.updatePlaybackPrefs(prefs.copy(normalizeVolume = it))
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------------------
// Equalizer Components
// ------------------------------------------------------------------------
@Composable
private fun EqualizerMasterCard(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    GlassmorphicCard(onClick = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (state.isEqEnabled) TertiaryEmerald.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = if (state.isEqEnabled) TertiaryEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Audio Equalizer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (state.isEqEnabled) TertiaryEmerald.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (state.isEqEnabled) "ACTIVE" else "OFF",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isEqEnabled) TertiaryEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "5-band precision frequency tuning",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = state.isEqEnabled,
                    onCheckedChange = { viewModel.setEqEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TertiaryEmerald
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PRESET PROFILES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                if (state.isEqEnabled) {
                    TextButton(
                        onClick = { viewModel.resetEqualizer() },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.eqPresets) { index, presetName ->
                    val isSelected = state.currentEqPreset.toInt() == index
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (!state.isEqEnabled) viewModel.setEqEnabled(true)
                            viewModel.useEqPreset(index.toShort())
                        },
                        label = {
                            Text(
                                text = presetName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                }

                // Custom Chip
                item {
                    val isCustom = state.currentEqPreset == (-1).toShort()
                    FilterChip(
                        selected = isCustom,
                        onClick = {
                            if (!state.isEqEnabled) viewModel.setEqEnabled(true)
                        },
                        label = {
                            Text(
                                text = "Custom",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isCustom) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SecondaryCyan,
                            selectedLabelColor = Color.Black,
                            selectedLeadingIconColor = Color.Black,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isCustom) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Curve Spectrum Visualizer
            FrequencyResponseVisualizer(
                bands = state.eqBands,
                isEnabled = state.isEqEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5-Band Sliders Row
            if (state.eqBands.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.eqBands.forEach { band ->
                        FrequencyBandStrip(
                            band = band,
                            isEnabled = state.isEqEnabled,
                            onLevelChanged = { newLevel ->
                                if (!state.isEqEnabled) viewModel.setEqEnabled(true)
                                viewModel.setEqBandLevel(band.bandId, newLevel)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FrequencyResponseVisualizer(
    bands: List<EqBand>,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val cyanColor = SecondaryCyan
    val emeraldColor = TertiaryEmerald
    val onSurfaceVar = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val maxDbRange = 1500f // -1500 to +1500 mB

            // Reference grid lines
            val yPlus10 = centerY - (1000f / maxDbRange) * (centerY - 8.dp.toPx())
            drawLine(
                color = onSurfaceVar.copy(alpha = 0.15f),
                start = Offset(0f, yPlus10),
                end = Offset(width, yPlus10),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // 0 dB center line
            drawLine(
                color = if (isEnabled) cyanColor.copy(alpha = 0.35f) else onSurfaceVar.copy(alpha = 0.2f),
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1.5.dp.toPx()
            )

            // -10 dB line
            val yMinus10 = centerY + (1000f / maxDbRange) * (centerY - 8.dp.toPx())
            drawLine(
                color = onSurfaceVar.copy(alpha = 0.15f),
                start = Offset(0f, yMinus10),
                end = Offset(width, yMinus10),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            if (bands.isEmpty()) return@Canvas

            val n = bands.size
            val padX = 24.dp.toPx()
            val availableW = width - 2 * padX
            val stepX = if (n > 1) availableW / (n - 1) else 0f

            val points = bands.mapIndexed { idx, band ->
                val x = padX + idx * stepX
                val level = if (isEnabled) band.level.toFloat() else 0f
                val clampedLevel = level.coerceIn(-maxDbRange, maxDbRange)
                val y = centerY - (clampedLevel / maxDbRange) * (centerY - 12.dp.toPx())
                Offset(x, y)
            }

            // Build smooth cubic bezier path
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX = (p0.x + p1.x) / 2f
                    cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                }
            }

            // Build filled area below curve
            val fillPath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX = (p0.x + p1.x) / 2f
                    cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                }
                lineTo(points.last().x, height)
                lineTo(points.first().x, height)
                close()
            }

            // Draw gradient fill
            val fillBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    if (isEnabled) primaryColor.copy(alpha = 0.35f) else onSurfaceVar.copy(alpha = 0.1f),
                    if (isEnabled) cyanColor.copy(alpha = 0.05f) else Color.Transparent
                ),
                startY = 0f,
                endY = height
            )
            drawPath(path = fillPath, brush = fillBrush)

            // Draw line stroke
            val strokeBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                colors = listOf(
                    if (isEnabled) primaryColor else onSurfaceVar.copy(alpha = 0.4f),
                    if (isEnabled) cyanColor else onSurfaceVar.copy(alpha = 0.4f),
                    if (isEnabled) emeraldColor else onSurfaceVar.copy(alpha = 0.4f)
                )
            )
            drawPath(
                path = strokePath,
                brush = strokeBrush,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw node points
            points.forEachIndexed { index, pt ->
                val nodeColor = if (isEnabled) {
                    if (bands[index].level > 0) cyanColor
                    else if (bands[index].level < 0) Color(0xFFF43F5E)
                    else primaryColor
                } else onSurfaceVar.copy(alpha = 0.4f)

                // Glow ring
                drawCircle(
                    color = nodeColor.copy(alpha = 0.25f),
                    radius = 8.dp.toPx(),
                    center = pt
                )
                // Outer ring
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = pt
                )
                // Inner dot
                drawCircle(
                    color = nodeColor,
                    radius = 2.5.dp.toPx(),
                    center = pt
                )
            }
        }
    }
}

@Composable
private fun FrequencyBandStrip(
    band: EqBand,
    isEnabled: Boolean,
    onLevelChanged: (Short) -> Unit,
    modifier: Modifier = Modifier
) {
    val levelDb = band.level / 100.0
    val levelText = when {
        levelDb > 0 -> "+%.1f".format(levelDb)
        levelDb < 0 -> "%.1f".format(levelDb)
        else -> "0.0"
    }

    val levelColor = when {
        !isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
        band.level > 0 -> SecondaryCyan
        band.level < 0 -> Color(0xFFFB7185)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gain dB Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(levelColor.copy(alpha = 0.15f))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = levelText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = levelColor,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Plus Step Button
        IconButton(
            onClick = {
                val nextLevel = (band.level + 100).coerceAtMost(1500).toShort()
                onLevelChanged(nextLevel)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increase ${formatBandFrequency(band.centerFreqHz)}",
                tint = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Interactive Vertical Slider Track
        val trackHeight = 120.dp
        val cyanColor = SecondaryCyan
        val coralColor = Color(0xFFF43F5E)

        Box(
            modifier = Modifier
                .width(28.dp)
                .height(trackHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .pointerInput(isEnabled) {
                    if (isEnabled) {
                        detectTapGestures { offset ->
                            val normalized = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                            val newLevel = (-1500 + normalized * 3000).toInt().toShort()
                            onLevelChanged(newLevel)
                        }
                    }
                }
                .pointerInput(isEnabled) {
                    if (isEnabled) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val normalized = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                            val newLevel = (-1500 + normalized * 3000).toInt().toShort()
                            onLevelChanged(newLevel)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f
                val normLevel = (band.level / 1500f).coerceIn(-1f, 1f)
                val thumbY = midY - normLevel * (midY - 12.dp.toPx())

                // Draw center 0 dB tick
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(4.dp.toPx(), midY),
                    end = Offset(w - 4.dp.toPx(), midY),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Draw active fill bar from center midY to thumbY
                if (band.level != 0.toShort() && isEnabled) {
                    val barColor = if (band.level > 0) cyanColor else coralColor
                    val topY = if (band.level > 0) thumbY else midY
                    val botY = if (band.level > 0) midY else thumbY
                    drawRoundRect(
                        color = barColor.copy(alpha = 0.6f),
                        topLeft = Offset(w / 2f - 3.dp.toPx(), topY),
                        size = androidx.compose.ui.geometry.Size(6.dp.toPx(), (botY - topY).coerceAtLeast(1f)),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                }

                // Draw Draggable Thumb Handle
                drawCircle(
                    color = if (isEnabled) Color.White else Color.Gray,
                    radius = 8.dp.toPx(),
                    center = Offset(w / 2f, thumbY)
                )
                drawCircle(
                    color = if (isEnabled) levelColor else Color.DarkGray,
                    radius = 5.dp.toPx(),
                    center = Offset(w / 2f, thumbY)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Minus Step Button
        IconButton(
            onClick = {
                val nextLevel = (band.level - 100).coerceAtLeast(-1500).toShort()
                onLevelChanged(nextLevel)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease ${formatBandFrequency(band.centerFreqHz)}",
                tint = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Frequency Label
        Text(
            text = formatBandFrequency(band.centerFreqHz),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Band Tag (SUB, BASS, MID, PRES, AIR)
        Text(
            text = formatBandTag(band.bandId.toInt()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun AudioEnhancementCard(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    GlassmorphicCard(onClick = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SpatialAudio,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Acoustic FX & Spatial Audio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Hardware DSP & psychoacoustic enhancements",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 1. Bass Boost
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Speaker,
                            contentDescription = null,
                            tint = TertiaryEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Bass Boost",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${state.bassBoostLevel}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryEmerald
                    )
                }
                Slider(
                    value = state.bassBoostLevel.toFloat(),
                    onValueChange = { viewModel.setBassBoost(it.toInt()) },
                    valueRange = 0f..100f,
                    steps = 19
                )
            }

            // 2. 3D Virtualizer
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "3D Virtualizer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${state.virtualizerLevel}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryCyan
                    )
                }
                Slider(
                    value = state.virtualizerLevel.toFloat(),
                    onValueChange = { viewModel.setVirtualizer(it.toInt()) },
                    valueRange = 0f..100f,
                    steps = 19
                )
            }

            // 3. Acoustic Environment / Reverb
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Acoustic Environment (Reverb)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                val reverbOptions = listOf("Off", "Small Room", "Studio", "Concert Hall", "Stadium")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reverbOptions) { opt ->
                        val isSelected = state.reverbPreset == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setReverb(opt) },
                            label = {
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun formatBandFrequency(centerFreqHz: Int): String {
    return when {
        centerFreqHz >= 1000000 -> {
            val khz = centerFreqHz / 1000000.0
            if (khz % 1.0 == 0.0) "${khz.toInt()}k" else "%.1fk".format(khz)
        }
        centerFreqHz >= 1000 -> "${centerFreqHz / 1000}Hz"
        else -> "${centerFreqHz}Hz"
    }
}

private fun formatBandTag(bandId: Int): String {
    return when (bandId) {
        0 -> "SUB"
        1 -> "BASS"
        2 -> "MID"
        3 -> "PRES"
        4 -> "AIR"
        else -> "B$bandId"
    }
}

// ------------------------------------------------------------------------
// 6. Notification Settings SubPage
// ------------------------------------------------------------------------
@Composable
private fun NotificationSettingsSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    val prefs = state.notificationPrefs
    val currentTrack = state.currentTrack

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Notification Widget Preview Card (Clicking opens Audio Player)
        GlassmorphicCard(
            onClick = {
                viewModel.openAudioPlayer()
            },
            modifier = Modifier.testTag("notification_widget_preview_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Notification Widget Preview",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Tap to Open Player",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Interactive Media Notification Widget Simulation
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (prefs.colorizeNotification) {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                    } else {
                        Color(0xFF1E212B)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Artwork or Placeholder Icon
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentTrack != null) {
                                TrackThumbnail(
                                    artworkUrl = currentTrack.artworkUrl,
                                    coverColorStr = currentTrack.coverColorStr,
                                    modifier = Modifier.fillMaxSize(),
                                    isVideo = currentTrack.isVideo
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Track title and artist
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTrack?.title ?: "No track currently playing",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = currentTrack?.artist ?: "Tap anywhere on this widget to open player",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        // Play/Pause button
                        IconButton(
                            onClick = {
                                if (currentTrack != null) {
                                    viewModel.togglePlayPause()
                                } else {
                                    viewModel.openAudioPlayer()
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (state.isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lock Screen Media Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Show responsive playback controls on your lock screen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.showLockscreenControls,
                    onCheckedChange = {
                        viewModel.updateNotificationPrefs(prefs.copy(showLockscreenControls = it))
                    }
                )
            }
        }

        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Display Album Artwork",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Render high-resolution album cover on device lock screen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.showLockscreenArtwork,
                    onCheckedChange = {
                        viewModel.updateNotificationPrefs(prefs.copy(showLockscreenArtwork = it))
                    }
                )
            }
        }

        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dynamic Notification Tint",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Colorize background of media notification to match current song art",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.colorizeNotification,
                    onCheckedChange = {
                        viewModel.updateNotificationPrefs(prefs.copy(colorizeNotification = it))
                    }
                )
            }
        }

        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Compact Notification Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Keep notification compact to save status bar space",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.compactControls,
                    onCheckedChange = {
                        viewModel.updateNotificationPrefs(prefs.copy(compactControls = it))
                    }
                )
            }
        }

        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Haptic Click Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Vibrate subtly on skipping, pause and play taps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.hapticFeedback,
                    onCheckedChange = {
                        viewModel.updateNotificationPrefs(prefs.copy(hapticFeedback = it))
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------------------
// 7. Language SubPage
// ------------------------------------------------------------------------
@Composable
private fun LanguageSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    var searchQuery by remember { mutableStateOf("") }

    val languages = listOf(
        "English (US)" to "Default",
        "English (UK)" to "British English",
        "Español" to "Spanish",
        "Français" to "French",
        "Deutsch" to "German",
        "日本語" to "Japanese",
        "中文 (简体)" to "Simplified Chinese",
        "Português" to "Portuguese",
        "Italiano" to "Italian",
        "한국어" to "Korean"
    )

    val filtered = languages.filter {
        it.first.contains(searchQuery, ignoreCase = true) || it.second.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search language...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { (langName, desc) ->
                val isSelected = state.selectedLanguage == langName
                GlassmorphicCard(
                    onClick = { viewModel.setLanguage(langName) },
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = langName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// 8. Feedback SubPage
// ------------------------------------------------------------------------
@Composable
private fun FeedbackSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    var rating by remember { mutableIntStateOf(5) }
    var selectedCategory by remember { mutableStateOf("Feature Request") }
    var comments by remember { mutableStateOf("") }
    var showThankYouDialog by remember { mutableStateOf(false) }

    val categories = listOf("Feature Request", "Bug Report", "Audio Quality", "UI Design", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Rating card
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How would you rate Media Player?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Rounded.StarOutline,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(3).forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 11.sp) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.drop(3).forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 11.sp) }
                )
            }
        }

        OutlinedTextField(
            value = comments,
            onValueChange = { comments = it },
            placeholder = { Text("What did you enjoy or what should we improve?...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = state.profile.email,
            onValueChange = {},
            label = { Text("Your Email for follow-ups") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Button(
            onClick = {
                showThankYouDialog = true
                comments = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Submit Feedback", fontWeight = FontWeight.Bold)
        }
    }

    if (showThankYouDialog) {
        AlertDialog(
            onDismissRequest = { showThankYouDialog = false },
            confirmButton = {
                TextButton(onClick = { showThankYouDialog = false }) {
                    Text("Close")
                }
            },
            title = { Text("Thank You!") },
            text = { Text("We appreciate your feedback! It helps make Media Player better with every update.") }
        )
    }
}

// ------------------------------------------------------------------------
// 9. About SubPage
// ------------------------------------------------------------------------
@Composable
private fun AboutSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showUpdateToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Large Logo Badge
        AuraBrandLogo(
            logoSize = 90.dp,
            isVideoTheme = false
        )

        Text(
            text = "Aura",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Version 1.2.0 (Build 2026.09)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Glassmorphic Info Cards
        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Crafted with Kotlin & Jetpack Compose",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Creator: Riel Sites",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Engineered with ExoPlayer Media3, Room Database, and Material 3 Design System. Built for seamless high-fidelity audio playback and offline local library management.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Buttons
        GlassmorphicCard(
            onClick = { showUpdateToast = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = SecondaryCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Check for Updates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Latest",
                    style = MaterialTheme.typography.labelSmall,
                    color = TertiaryEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        GlassmorphicCard(
            onClick = { showLicensesDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Open Source Licenses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        GlassmorphicCard(
            onClick = { showTermsDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Terms & Privacy Policy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showUpdateToast) {
        AlertDialog(
            onDismissRequest = { showUpdateToast = false },
            confirmButton = {
                TextButton(onClick = { showUpdateToast = false }) { Text("OK") }
            },
            title = { Text("App is Up to Date") },
            text = { Text("Media Player 1.2.0 is the latest available build with active patch optimizations.") }
        )
    }

    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            confirmButton = {
                TextButton(onClick = { showLicensesDialog = false }) { Text("Close") }
            },
            title = { Text("Open Source Libraries") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("• AndroidX Media3 (ExoPlayer) - Apache 2.0")
                    Text("• Jetpack Compose & Material 3 - Apache 2.0")
                    Text("• AndroidX Room - Apache 2.0")
                    Text("• Coil Image Loader - Apache 2.0")
                    Text("• Kotlin Coroutines - Apache 2.0")
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) { Text("Close") }
            },
            title = { Text("Privacy Policy & Terms") },
            text = {
                Text("Media Player operates with privacy-first standards. Audio files stored on your local device remain strictly local and are never uploaded to external servers.")
            }
        )
    }
}

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------
// Visualizer SubPage
@Composable
private fun VisualizerSettingsSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Mode Selection
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Visualizer Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            for (mode in VisualizerMode.values()) {
                val isSelected = state.visualizerMode == mode
                GlassmorphicCard(
                    onClick = { viewModel.setVisualizerMode(mode) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = mode.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mode.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = mode.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Theme Selection
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Color Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            for (theme in VisualizerTheme.values()) {
                val isSelected = state.visualizerTheme == theme
                GlassmorphicCard(
                    onClick = { viewModel.setVisualizerTheme(theme) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(theme.colors))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = theme.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Sensitivity
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Responsiveness / Sensitivity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            GlassmorphicCard(onClick = {}) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(state.visualizerSensitivity * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Slider(
                        value = state.visualizerSensitivity,
                        onValueChange = { viewModel.setVisualizerSensitivity(it) },
                        valueRange = 0.5f..2.5f,
                        steps = 19,
                        colors = androidx.compose.material3.SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Adjust how strongly the visualizer reacts to audio.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
// ------------------------------------------------------------------------
// 10. Lyrics SubPage
// ------------------------------------------------------------------------
@Composable
private fun LyricsSettingsSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Mode Selection
        Text(
            text = "Default View Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        val modes = listOf(
            LyricsViewMode.SYNCED to "Synchronized",
            LyricsViewMode.PLAIN to "Plain Text"
        )

        for ((mode, label) in modes) {
            val isSelected = state.lyricsViewMode == mode
            GlassmorphicCard(
                onClick = { viewModel.setLyricsViewMode(mode) },
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (mode == LyricsViewMode.SYNCED) Icons.Filled.Sync else Icons.Filled.FormatAlignLeft,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (mode == LyricsViewMode.SYNCED) "Highlight active line with music" else "Show full lyrics as static text",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Synced Lyrics Display Style
        Text(
            text = "Synced Display Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )
        
        val displayStyles = listOf(
            com.example.SyncedLyricsDisplayStyle.LIST to "List (Scrollable)",
            com.example.SyncedLyricsDisplayStyle.FOCUS to "Focus (Current Line Only)"
        )
        
        for ((style, label) in displayStyles) {
            val isSelected = state.syncedLyricsDisplayStyle == style
            GlassmorphicCard(
                onClick = { viewModel.setSyncedLyricsDisplayStyle(style) },
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (style == com.example.SyncedLyricsDisplayStyle.LIST) Icons.Filled.FormatAlignLeft else Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (style == com.example.SyncedLyricsDisplayStyle.LIST) "Show previous and upcoming lines" else "Display only the active singing line",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Automatic Lyrics Search Online Toggle
        GlassmorphicCard(
            onClick = { viewModel.setAutoSearchLyricsOnline(!state.isAutoSearchLyricsOnline) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Search Lyrics Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Automatically query online LRCLIB database when songs without lyrics are played",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.isAutoSearchLyricsOnline,
                    onCheckedChange = { viewModel.setAutoSearchLyricsOnline(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Online Database Modal Action
        GlassmorphicCard(
            onClick = { viewModel.openLyricsSearchModal() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Search Online Lyrics Database",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Search, preview, and match synchronized LRC lyrics for active track",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Auto Scroll Toggle (Only applies to Synced mode)
        GlassmorphicCard(
            onClick = { viewModel.toggleAutoScrollLyrics(!state.isAutoScrollLyrics) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Scroll Sync",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Automatically jump to active line in Synchronized mode",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.isAutoScrollLyrics,
                    onCheckedChange = { viewModel.toggleAutoScrollLyrics(it) }
                )
            }
        }
        // Font Size Setting
        Text(
            text = "Font Size",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )
        GlassmorphicCard(onClick = {}) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Size", color = MaterialTheme.colorScheme.onSurface)
                    Text("${state.lyricsFontSize} sp", color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = state.lyricsFontSize.toFloat(),
                    onValueChange = { viewModel.setLyricsFontSize(it.toInt()) },
                    valueRange = 12f..36f,
                    steps = 24
                )
            }
        }

        // Alignment Setting
        Text(
            text = "Alignment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )
        GlassmorphicCard(onClick = {}) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (align in listOf("Left", "Center", "Right")) {
                    val isSelected = state.lyricsAlignment == align
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setLyricsAlignment(align) },
                        label = { Text(align) }
                    )
                }
            }
        }

        // Auto-Scroll Speed
        if (state.isAutoScrollLyrics) {
            Text(
                text = "Auto-Scroll Speed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
            GlassmorphicCard(onClick = {}) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Speed", color = MaterialTheme.colorScheme.onSurface)
                        Text(String.format("%.1fx", state.lyricsScrollSpeed), color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = state.lyricsScrollSpeed,
                        onValueChange = { viewModel.setLyricsScrollSpeed(it) },
                        valueRange = 0.5f..2.0f,
                        steps = 14
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// Video Player Dedicated Settings SubPage
// ------------------------------------------------------------------------
@Composable
private fun VideoPlayerSettingsSubPage(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel
) {
    val prefs = state.videoPrefs
    var subtitleFontSize by remember(prefs.subtitleFontSize) { mutableFloatStateOf(prefs.subtitleFontSize.toFloat()) }
    var subtitleOpacity by remember(prefs.subtitleBackgroundOpacity) { mutableFloatStateOf(prefs.subtitleBackgroundOpacity) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Video Player Display & Subtitle Simulation Card
        VideoPlayerPreviewCard(
            quality = prefs.defaultQuality,
            resizeMode = prefs.defaultResizeMode,
            subtitleFontSize = subtitleFontSize.toInt(),
            subtitleOpacity = subtitleOpacity,
            showResolutionBadge = prefs.showVideoResolutionBadge
        )

        // 2. Default Resolution & Quality Section
        Text(
            text = "Video Quality & Resolution",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.HighQuality,
                        contentDescription = null,
                        tint = SecondaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Preferred Streaming Quality",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Set initial playback stream resolution",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val qualityOptions = listOf("Auto (HQ)", "1080p Full HD", "720p HD", "480p SD", "360p")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    qualityOptions.forEach { q ->
                        val isSelected = prefs.defaultQuality == q
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                    else Color.Transparent
                                )
                                .clickable { viewModel.updateVideoDefaultQuality(q) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updateVideoDefaultQuality(q) }
                                )
                                Text(
                                    text = q,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (q.contains("1080p") || q.contains("Auto")) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (q.contains("Auto")) "RECOMMENDED" else "HD",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Aspect Ratio & Screen Fit Mode
        Text(
            text = "Screen Fit & Aspect Ratio",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AspectRatio,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Default Fit Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Choose how wide & ultrawide videos map to your display",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        "Fit Screen" to "Preserve letterbox",
                        "Crop / Fill" to "Zoom full view",
                        "Stretch 16:9" to "Fill screen edges"
                    )
                    modes.forEach { (modeTitle, modeDesc) ->
                        val isSelected = prefs.defaultResizeMode == modeTitle
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.updateVideoDefaultResizeMode(modeTitle) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = modeTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = modeDesc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Playback Controls & Navigation Gestures
        Text(
            text = "Playback & Navigation Gestures",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Seek Skip Step
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Double-Tap / Button Skip Interval",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${prefs.seekJumpSeconds} seconds",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 30).forEach { sec ->
                            val isSelected = prefs.seekJumpSeconds == sec
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateVideoSeekJump(sec) },
                                label = { Text("${sec}s") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Controls Auto-Hide Timeout
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Controls Auto-Hide Delay",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (prefs.autoHideControlsSeconds == 0) "Manual" else "${prefs.autoHideControlsSeconds}s",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(3 to "3s", 4 to "4.5s", 8 to "8s", 0 to "Never").forEach { (sec, label) ->
                            val isSelected = prefs.autoHideControlsSeconds == sec
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateVideoAutoHideControls(sec) },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Auto Rotate On Fullscreen Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ScreenRotation,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Auto-Rotate to Landscape",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Automatically switch orientation when entering fullscreen",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = prefs.autoRotateLandscapeOnFullscreen,
                        onCheckedChange = { viewModel.toggleVideoAutoRotate(it) }
                    )
                }

                // Lock Landscape Orientation Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ScreenRotation,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Lock Player to Landscape",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Always lock the video player into landscape orientation regardless of device sensor rotation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = prefs.lockLandscapeOrientation,
                        onCheckedChange = { viewModel.toggleLockLandscapeOrientation(it) }
                    )
                }

                // Video Resolution Badge Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HighQuality,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Show Resolution Tag",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Display resolution badge (e.g. 1080p, 4K) on top HUD bar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = prefs.showVideoResolutionBadge,
                        onCheckedChange = { viewModel.toggleVideoResolutionBadge(it) }
                    )
                }
            }
        }

        // 5. Subtitles & Closed Caption Styling
        Text(
            text = "Subtitles & Closed Captions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Subtitles,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Caption Typography & Background",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tailor subtitle legibility for dark and bright video scenes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Subtitle Font Size
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Font Size",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${subtitleFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = subtitleFontSize,
                        onValueChange = {
                            subtitleFontSize = it
                            viewModel.updateVideoSubtitleSettings(it.toInt(), subtitleOpacity)
                        },
                        valueRange = 12f..24f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Subtitle Background Box Opacity
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Background Box Opacity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(subtitleOpacity * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = subtitleOpacity,
                        onValueChange = {
                            subtitleOpacity = it
                            viewModel.updateVideoSubtitleSettings(subtitleFontSize.toInt(), it)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // 6. Advanced Performance & Audio section
        Text(
            text = "Advanced & Decoder Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        GlassmorphicCard(onClick = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hardware Decoder Fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Memory,
                            contentDescription = null,
                            tint = TertiaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Decoder Fallback Safety",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Gracefully recover to software codecs when hardware acceleration fails",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = prefs.hardwareDecoderFallback,
                        onCheckedChange = { viewModel.toggleVideoHardwareDecoderFallback(it) }
                    )
                }

                // Background Audio Playback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Background Audio Playback",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Continue playing video audio track when screen is locked or app minimized",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = prefs.backgroundAudioPlayback,
                        onCheckedChange = { viewModel.toggleVideoBackgroundAudio(it) }
                    )
                }
            }
        }

        // 7. Reset to Defaults
        OutlinedButton(
            onClick = { viewModel.resetVideoPreferences() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.RestartAlt,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reset Video Player Preferences",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Interactive Live Video Mockup Preview
@Composable
private fun VideoPlayerPreviewCard(
    quality: String,
    resizeMode: String,
    subtitleFontSize: Int,
    subtitleOpacity: Float,
    showResolutionBadge: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            Color(0xFF0F172A),
                            Color.Black
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Simulated Video Play icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayCircleFilled,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Top Simulation Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PREVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    if (showResolutionBadge) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = quality.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = resizeMode,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    )
                }
            }

            // Simulated Subtitle at Bottom Center
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = subtitleOpacity))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "♫ Live Subtitle Display Preview ♫",
                    color = Color.White,
                    fontSize = subtitleFontSize.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
