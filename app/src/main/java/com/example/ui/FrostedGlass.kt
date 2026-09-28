package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Premium Frosted Glass Colors and Gradients for Aura UI with dynamic light/dark theme toggling
 */
object FrostedGlassDefaults {
    val GlassDarkBase = Color(0xCC131728) // Deep translucent indigo/slate
    val GlassDarkElevated = Color(0xE01A1F36)
    val GlassDarkSubtle = Color(0x80101321)
    
    val GlassLightBase = Color(0xCCFFFFFF) // Frosted translucent white
    val GlassLightElevated = Color(0xEBF8FAFC)
    val GlassLightSubtle = Color(0x99F1F5F9)

    val BorderHighlightTop = Color(0x40FFFFFF) // 25% white specular highlight
    val BorderHighlightBottom = Color(0x0AFFFFFF) // 4% white subtle bottom rim

    val BorderLightTop = Color(0x66FFFFFF)
    val BorderLightBottom = Color(0x1A0F172A)
    
    @Composable
    fun isDarkTheme(): Boolean {
        return MaterialTheme.colorScheme.background.luminance() < 0.5f
    }
    
    @Composable
    fun glassGradient(
        topColor: Color? = null,
        bottomColor: Color? = null
    ): Brush {
        val dark = isDarkTheme()
        val t = topColor ?: if (dark) Color(0xD91E243D) else Color(0xE6FFFFFF)
        val b = bottomColor ?: if (dark) Color(0xB8121526) else Color(0xCCF1F5F9)
        return Brush.verticalGradient(listOf(t, b))
    }
    
    @Composable
    fun glassBorderBrush(
        topColor: Color? = null,
        bottomColor: Color? = null
    ): Brush {
        val dark = isDarkTheme()
        val t = topColor ?: if (dark) BorderHighlightTop else BorderLightTop
        val b = bottomColor ?: if (dark) BorderHighlightBottom else BorderLightBottom
        return Brush.verticalGradient(listOf(t, b))
    }
    
    @Composable
    fun glassBorder(
        width: Dp = 1.dp,
        brush: Brush = glassBorderBrush()
    ): BorderStroke {
        return BorderStroke(width, brush)
    }
}

/**
 * Modifier for applying a frosted glassmorphic styling to any composable with theme awareness.
 */
@Composable
fun Modifier.frostedGlass(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color? = null,
    borderBrush: Brush = FrostedGlassDefaults.glassBorderBrush(),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 8.dp
): Modifier {
    val dark = FrostedGlassDefaults.isDarkTheme()
    val resolvedBg = backgroundColor ?: if (dark) FrostedGlassDefaults.GlassDarkBase else FrostedGlassDefaults.GlassLightBase
    val ambientCol = if (dark) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.1f)
    val spotCol = if (dark) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.15f)

    return this
        .shadow(elevation = elevation, shape = shape, ambientColor = ambientCol, spotColor = spotCol)
        .clip(shape)
        .background(resolvedBg)
        .border(borderWidth, borderBrush, shape)
}

/**
 * Composable container for frosted glass surfaces with specular highlights and optional gradient.
 */
@Composable
fun FrostedGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color? = null,
    backgroundBrush: Brush? = FrostedGlassDefaults.glassGradient(),
    borderBrush: Brush = FrostedGlassDefaults.glassBorderBrush(),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val dark = FrostedGlassDefaults.isDarkTheme()
    val resolvedBg = backgroundColor ?: if (dark) FrostedGlassDefaults.GlassDarkBase else FrostedGlassDefaults.GlassLightBase
    val ambientCol = if (dark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.1f)
    val spotCol = if (dark) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = ambientCol,
                spotColor = spotCol
            )
            .clip(shape)
            .then(
                if (backgroundBrush != null) Modifier.background(backgroundBrush)
                else Modifier.background(resolvedBg)
            )
            .then(
                if (borderWidth > 0.dp) Modifier.border(borderWidth, borderBrush, shape)
                else Modifier
            ),
        content = content
    )
}

/**
 * Frosted Glass Pill / Chip for badges, buttons, and headers.
 */
@Composable
fun FrostedGlassPill(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    backgroundColor: Color? = null,
    borderBrush: Brush = FrostedGlassDefaults.glassBorderBrush(
        topColor = Color.White.copy(alpha = 0.35f),
        bottomColor = Color.White.copy(alpha = 0.1f)
    ),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val dark = FrostedGlassDefaults.isDarkTheme()
    val resolvedBg = backgroundColor ?: if (dark) Color(0x338B5CF6) else Color(0x227C3AED)

    Box(
        modifier = modifier
            .clip(shape)
            .background(resolvedBg)
            .then(
                if (borderWidth > 0.dp) Modifier.border(borderWidth, borderBrush, shape)
                else Modifier
            ),
        content = content
    )
}

/**
 * Animated glow modifier providing subtle pulsating or interactive glow halo.
 */
@Composable
fun Modifier.glowHoverEffect(
    isHoveredOrPressed: Boolean = false,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    glowRadius: Dp = 18.dp
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "AmbientGlowTransition")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AmbientPulse"
    )

    val currentAlpha = if (isHoveredOrPressed) 0.8f else ambientPulse

    return this.drawBehind {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = currentAlpha),
                    glowColor.copy(alpha = currentAlpha * 0.3f),
                    Color.Transparent
                )
            ),
            radius = size.maxDimension * 0.72f,
            center = center
        )
    }
}

/**
 * Animated glowing border that softly pulses along the contours of a shape.
 */
@Composable
fun Modifier.animatedGlowBorder(
    enabled: Boolean = true,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.2.dp
): Modifier {
    if (!enabled) return this

    val infiniteTransition = rememberInfiniteTransition(label = "BorderGlowPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BorderAlpha"
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(
            glowColor.copy(alpha = alpha),
            Color.White.copy(alpha = 0.25f),
            glowColor.copy(alpha = alpha * 0.6f)
        )
    )

    return this.border(borderWidth, animatedBrush, shape)
}

/**
 * Sleek glassmorphic theme toggle button with smooth icon transformation,
 * bounce scale physics, and ambient radial glow.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemeToggleButton(
    isDark: Boolean,
    onToggle: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rotation by animateFloatAsState(
        targetValue = if (isDark) 0f else 180f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow),
        label = "ThemeToggleRotation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "ThemeToggleScale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val dark = FrostedGlassDefaults.isDarkTheme()

    Box(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (dark) Color(0x331F2438) else Color(0x22CBD5E1)
            )
            .border(
                BorderStroke(
                    1.dp,
                    FrostedGlassDefaults.glassBorderBrush(
                        topColor = if (dark) primaryColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.85f),
                        bottomColor = if (dark) Color.White.copy(alpha = 0.1f) else primaryColor.copy(alpha = 0.35f)
                    )
                ),
                CircleShape
            )
            .glowHoverEffect(
                isHoveredOrPressed = isPressed,
                glowColor = if (isDark) primaryColor else Color(0xFFF59E0B)
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle,
                onLongClick = onLongClick
            )
            .testTag("theme_toggle_button"),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isDark,
            animationSpec = tween(280),
            label = "ThemeIconCrossfade"
        ) { darkTheme ->
            if (darkTheme) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = "Switch to Light Theme",
                    tint = primaryColor,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = rotation }
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.LightMode,
                    contentDescription = "Switch to Dark Theme",
                    tint = Color(0xFFD97706),
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = rotation }
                )
            }
        }
    }
}
