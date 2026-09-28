package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.FrostedGlassDefaults
import com.example.ui.FrostedGlassPill
import com.example.ui.FrostedGlassSurface
import com.example.ui.animatedGlowBorder
import com.example.ui.glowHoverEffect
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentUltraviolet

/**
 * Clean, decluttered Shadcn-style Modal for Theme & Appearance toggling
 * featuring glassmorphic blurs, specular highlights, and interactive glow animations.
 */
@Composable
fun ThemeModalDialog(
    state: MediaPlayerState,
    viewModel: MediaPlayerViewModel,
    onDismiss: () -> Unit
) {
    val dark = FrostedGlassDefaults.isDarkTheme()
    val scrollState = rememberScrollState()

    val accentOptions = listOf(
        Triple("Ultraviolet", AccentUltraviolet, 0),
        Triple("Cyan Glow", AccentCyan, 1),
        Triple("Emerald", AccentEmerald, 2),
        Triple("Rose Flame", AccentRose, 3),
        Triple("Amber Sun", AccentAmber, 4),
        Triple("Electric Indigo", AccentIndigo, 5)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FrostedGlassSurface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 520.dp)
                .padding(vertical = 24.dp)
                .testTag("theme_modal_dialog"),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = if (dark) FrostedGlassDefaults.GlassDarkElevated else FrostedGlassDefaults.GlassLightElevated,
            borderBrush = FrostedGlassDefaults.glassBorderBrush(
                topColor = if (dark) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.9f),
                bottomColor = if (dark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.08f)
            ),
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header badge + Close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FrostedGlassPill(
                        modifier = Modifier.padding(bottom = 4.dp),
                        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = if (dark) 0.22f else 0.12f),
                        borderBrush = FrostedGlassDefaults.glassBorderBrush(
                            topColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            bottomColor = Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "APPEARANCE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("theme_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Theme Dialog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Theme & Appearance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose a display canvas and custom glow accent that suits your environment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mode Selection Options (Shadcn style cards)
                Text(
                    text = "Display Mode",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThemeModeCard(
                        title = "Dark Canvas",
                        subtitle = "High-contrast deep obsidian and ultraviolet hues for low-light listening.",
                        icon = Icons.Filled.DarkMode,
                        isSelected = state.themeMode == "Dark",
                        onClick = { viewModel.setThemeMode("Dark") },
                        testTag = "theme_mode_dark_button"
                    )

                    ThemeModeCard(
                        title = "Light Canvas",
                        subtitle = "Frosted bright luminescence with crisp readability for daytime clarity.",
                        icon = Icons.Filled.LightMode,
                        isSelected = state.themeMode == "Light",
                        onClick = { viewModel.setThemeMode("Light") },
                        testTag = "theme_mode_light_button"
                    )

                    ThemeModeCard(
                        title = "System Default",
                        subtitle = "Harmoniously syncs with your device's global dark/light theme setting.",
                        icon = Icons.Filled.BrightnessAuto,
                        isSelected = state.themeMode == "System",
                        onClick = { viewModel.setThemeMode("System") },
                        testTag = "theme_mode_system_button"
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Accent Color Palette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Glow Accent",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = accentOptions.getOrNull(state.accentColorIndex)?.first ?: "Ultraviolet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    accentOptions.forEach { (_, color, index) ->
                        val isSelected = state.accentColorIndex == index
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()

                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.85f else if (isSelected) 1.15f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
                            label = "AccentScale"
                        )

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(color.copy(alpha = if (isSelected) 1f else 0.7f))
                                .then(
                                    if (isSelected) {
                                        Modifier
                                            .border(2.5.dp, Color.White, CircleShape)
                                            .glowHoverEffect(isHoveredOrPressed = true, glowColor = color)
                                    } else {
                                        Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    }
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { viewModel.setAccentColor(index) }
                                )
                                .testTag("theme_accent_color_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected Accent",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dynamic Color Toggle (Material You)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (dark) Color(0x331E243A) else Color(0x22CBD5E1)
                        )
                        .border(
                            1.dp,
                            FrostedGlassDefaults.glassBorderBrush(
                                topColor = if (dark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.7f),
                                bottomColor = Color.Transparent
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Material You Dynamic Colors",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Extract system hues from wallpaper on Android 12+",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = state.dynamicColor,
                            onCheckedChange = { viewModel.setDynamicColor(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("theme_dynamic_color_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .animatedGlowBorder(
                            enabled = true,
                            glowColor = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .testTag("theme_apply_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Apply & Done",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Selectable Theme Mode Card with glassmorphic depth, active border glow,
 * and clear typography inspired by Shadcn card design.
 */
@Composable
private fun ThemeModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val dark = FrostedGlassDefaults.isDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "ModeCardScale"
    )

    val cardBg = if (isSelected) {
        primaryColor.copy(alpha = if (dark) 0.22f else 0.12f)
    } else {
        if (dark) Color(0x401A1F33) else Color(0x33F1F5F9)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .then(
                if (isSelected) {
                    Modifier.animatedGlowBorder(
                        enabled = true,
                        glowColor = primaryColor,
                        shape = RoundedCornerShape(16.dp),
                        borderWidth = 1.5.dp
                    )
                } else {
                    Modifier.border(
                        1.dp,
                        FrostedGlassDefaults.glassBorderBrush(
                            topColor = if (dark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.7f),
                            bottomColor = Color.Transparent
                        ),
                        RoundedCornerShape(16.dp)
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) primaryColor else (if (dark) Color(0x332D3748) else Color(0x22CBD5E1))
                    )
                    .then(
                        if (isSelected) Modifier.glowHoverEffect(isHoveredOrPressed = true, glowColor = primaryColor)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                    )

                    if (isSelected) {
                        FrostedGlassPill(
                            backgroundColor = primaryColor.copy(alpha = 0.25f),
                            borderWidth = 0.8.dp
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            // Radio/Check circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) primaryColor else Color.Transparent
                    )
                    .border(
                        BorderStroke(
                            2.dp,
                            if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
