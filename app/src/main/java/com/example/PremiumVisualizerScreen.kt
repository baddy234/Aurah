package com.example

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.PrimaryUltraviolet
import com.example.ui.theme.PrimaryUltravioletVariant
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TertiaryEmerald
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Visualizer Rendering Modes
 */
enum class VisualizerMode(val title: String, val icon: ImageVector, val description: String) {
    NEON_BARS("Neon Bars", Icons.Filled.GraphicEq, "Dynamic spectrum audio frequency pillars with peak-drop caps"),
    CYBER_WAVE("Cyber Waveform", Icons.Filled.Waves, "Continuous flowing multi-harmonic liquid wave ribbons"),
    CIRCULAR_PULSE("Orbital Core", Icons.Filled.RadioButtonChecked, "Radial neon halo with rotating audio-reactive particle spikes"),
    PARTICLE_STARBURST("Cosmic Particles", Icons.Filled.AutoAwesome, "Explosive audio-driven interstellar particle fireworks"),
    HOLOGRAPHIC_ORB("Holo Orb", Icons.Filled.MotionPhotosOn, "Fluid 3D reactive energy sphere with ethereal ambient glow")
}

/**
 * Visualizer Color Schemes
 */
enum class VisualizerTheme(val title: String, val colors: List<Color>, val glowColor: Color) {
    ULTRAVIOLET(
        "Ultraviolet Glow",
        listOf(Color(0xFF8B5CF6), Color(0xFF6366F1), Color(0xFF06B6D4)),
        Color(0x998B5CF6)
    ),
    CYBERPUNK(
        "Cyberpunk Neon",
        listOf(Color(0xFFFF007F), Color(0xFF7928CA), Color(0xFF00F0FF)),
        Color(0x99FF007F)
    ),
    EMERALD_AURORA(
        "Emerald Aurora",
        listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF3B82F6)),
        Color(0x9910B981)
    ),
    SOLAR_FLARE(
        "Solar Flare",
        listOf(Color(0xFFFF4500), Color(0xFFFFA500), Color(0xFFFFD700)),
        Color(0x99FF4500)
    ),
    MATRIX_PHOSPHOR(
        "Matrix Phosphor",
        listOf(Color(0xFF00FF66), Color(0xFF00DD55), Color(0xFF22C55E)),
        Color(0x9900FF66)
    )
}

/**
 * Particle data structure for Particle Starburst mode
 */
private class VisualizerParticle(
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var radius: Float = 3f,
    var alpha: Float = 1f,
    var colorIndex: Int = 0,
    var life: Float = 1f,
    var maxLife: Float = 1f
)

/**
 * Premium Glassmorphic Audio Visualizer Screen & Component
 */
@Composable
fun PremiumVisualizerScreen(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    isPlaying: Boolean,
    progressMs: Long,
    durationMs: Long,
    isEqEnabled: Boolean,
    eqBands: List<EqBand>,
    onPlayPause: () -> Unit,
    visualizerMode: VisualizerMode,
    visualizerTheme: VisualizerTheme,
    visualizerSensitivity: Float,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    var touchRipplePoint by remember { mutableStateOf<Offset?>(null) }

    // Smooth Audio Energy & FFT Simulation Engine
    // Generates 32 continuous frequency spectrum bands informed by playback time, eq bands, and music rhythm
    val bandCount = 32
    val spectrumValues = remember { FloatArray(bandCount) }
    val peakValues = remember { FloatArray(bandCount) }
    val phaseOffsets = remember { FloatArray(bandCount) { Random.nextFloat() * 2f * PI.toFloat() } }

    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_infinite")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_time"
    )

    // Pulsing background glow intensity
    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow"
    )

    // Frame update loop for smooth 60fps simulation
    LaunchedEffect(isPlaying, progressMs, visualizerSensitivity, isEqEnabled, eqBands) {
        val seed = abs(track.id.hashCode() % 1000)
        while (isActive) {
            val currentTimeSec = (progressMs / 1000.0) + (System.currentTimeMillis() % 1000) / 1000.0
            val speedFactor = if (isPlaying) 1.0 else 0.15

            // Compute virtual bass/mid/treble factors from EQ bands
            val bassBoost = if (isEqEnabled && eqBands.isNotEmpty()) {
                (eqBands.getOrNull(0)?.level?.toFloat() ?: 0f) / 1500f * 0.4f
            } else 0f
            val trebleBoost = if (isEqEnabled && eqBands.size >= 5) {
                (eqBands.getOrNull(4)?.level?.toFloat() ?: 0f) / 1500f * 0.3f
            } else 0f

            for (i in 0 until bandCount) {
                if (!isPlaying) {
                    // Gentle breathing idle wave
                    val idle = (sin(currentTimeSec * 1.5 + i * 0.2).toFloat() * 0.5f + 0.5f) * 0.12f + 0.04f
                    spectrumValues[i] = spectrumValues[i] * 0.85f + idle * 0.15f
                } else {
                    val freq = (i + 1) * 0.4
                    val rhythm = sin(currentTimeSec * 5.0 * (1.0 + (seed % 5) * 0.1) + phaseOffsets[i])
                    val harmonic = cos(currentTimeSec * 3.2 + i * 0.5)
                    val subBass = if (i < 6) sin(currentTimeSec * 9.0) * (0.4 + bassBoost) else 0.0
                    val highAir = if (i > 22) cos(currentTimeSec * 12.0) * (0.25 + trebleBoost) else 0.0

                    var rawEnergy = (rhythm * 0.4 + harmonic * 0.3 + subBass + highAir + 0.6).toFloat()
                    rawEnergy = (rawEnergy * visualizerSensitivity).coerceIn(0.05f, 1.0f)

                    // Exponential frequency damping for realistic audio spectrum shape
                    val eqWeight = 1.0f - (i.toFloat() / bandCount) * 0.35f
                    val targetVal = rawEnergy * eqWeight

                    // Attack & Decay smoothing
                    val currentVal = spectrumValues[i]
                    if (targetVal > currentVal) {
                        spectrumValues[i] = currentVal + (targetVal - currentVal) * 0.45f
                    } else {
                        spectrumValues[i] = currentVal - (currentVal - targetVal) * 0.18f
                    }

                    // Peak meter drop logic
                    if (spectrumValues[i] > peakValues[i]) {
                        peakValues[i] = spectrumValues[i]
                    } else {
                        peakValues[i] = (peakValues[i] - 0.015f).coerceAtLeast(spectrumValues[i])
                    }
                }
            }
            delay(16) // ~60 fps
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF080912))
            .border(
                BorderStroke(1.dp, BorderGlass),
                RoundedCornerShape(24.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        touchRipplePoint = offset
                        
                    }
                )
            }
    ) {
        // Multi-Layer Glassmorphic Ambient Glow Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val glowRadius = size.minDimension * 0.45f
                val activeColors = visualizerTheme.colors

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            visualizerTheme.glowColor.copy(alpha = if (isPlaying) ambientGlow * 0.6f else 0.18f),
                            activeColors.getOrElse(1) { Color.Transparent }.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = centerOffset,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = centerOffset
                )
            }
        }

        // Main Visualizer Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp, top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            when (visualizerMode) {
                VisualizerMode.NEON_BARS -> {
                    drawNeonBars(
                        spectrum = spectrumValues,
                        peaks = peakValues,
                        theme = visualizerTheme,
                        isPlaying = isPlaying,
                        time = animTime
                    )
                }
                VisualizerMode.CYBER_WAVE -> {
                    drawCyberWave(
                        spectrum = spectrumValues,
                        theme = visualizerTheme,
                        isPlaying = isPlaying,
                        time = animTime
                    )
                }
                VisualizerMode.CIRCULAR_PULSE -> {
                    drawCircularPulse(
                        spectrum = spectrumValues,
                        theme = visualizerTheme,
                        isPlaying = isPlaying,
                        time = animTime
                    )
                }
                VisualizerMode.PARTICLE_STARBURST -> {
                    drawParticleStarburst(
                        spectrum = spectrumValues,
                        theme = visualizerTheme,
                        isPlaying = isPlaying,
                        time = animTime
                    )
                }
                VisualizerMode.HOLOGRAPHIC_ORB -> {
                    drawHolographicOrb(
                        spectrum = spectrumValues,
                        theme = visualizerTheme,
                        isPlaying = isPlaying,
                        time = animTime
                    )
                }
            }

            // Draw interactive touch ripple if triggered
            touchRipplePoint?.let { pt ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            visualizerTheme.colors[0].copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = pt,
                        radius = 120.dp.toPx()
                    ),
                    radius = 120.dp.toPx(),
                    center = pt
                )
            }
        }
    }
}


// -------------------------------------------------------------------------------------------------
// RENDERING ENGINES
// -------------------------------------------------------------------------------------------------

/**
 * 1. Neon Spectrum Bars with Dynamic Top Dropping Peaks and Gradient Glow
 */
private fun DrawScope.drawNeonBars(
    spectrum: FloatArray,
    peaks: FloatArray,
    theme: VisualizerTheme,
    isPlaying: Boolean,
    time: Float
) {
    val barCount = spectrum.size
    val totalSpacing = (barCount - 1) * 4.dp.toPx()
    val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(3.dp.toPx())
    val maxHeight = size.height * 0.78f
    val baseLineY = size.height * 0.88f

    val barBrush = Brush.verticalGradient(
        colors = listOf(
            theme.colors[0],
            theme.colors.getOrElse(1) { theme.colors[0] },
            theme.colors.getOrElse(2) { theme.colors[0] }.copy(alpha = 0.35f)
        ),
        startY = baseLineY - maxHeight,
        endY = baseLineY
    )

    for (i in 0 until barCount) {
        val energy = spectrum[i]
        val barHeight = (energy * maxHeight).coerceAtLeast(4.dp.toPx())
        val x = i * (barWidth + 4.dp.toPx())
        val topY = baseLineY - barHeight

        // Draw main bar body with rounded top
        drawRoundRect(
            brush = barBrush,
            topLeft = Offset(x, topY),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
        )

        // Draw outer neon reflection line
        drawLine(
            color = Color.White.copy(alpha = (energy * 0.6f).coerceIn(0.1f, 0.8f)),
            start = Offset(x + barWidth / 2f, topY + 2.dp.toPx()),
            end = Offset(x + barWidth / 2f, topY + (barHeight * 0.4f)),
            strokeWidth = (barWidth * 0.3f).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )

        // Peak indicator floating cap
        val peakEnergy = peaks[i]
        val peakY = baseLineY - (peakEnergy * maxHeight) - 3.dp.toPx()
        drawRoundRect(
            color = Color.White.copy(alpha = 0.85f),
            topLeft = Offset(x, peakY),
            size = Size(barWidth, 3.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Subtle bottom baseline mirror reflection
        val mirrorHeight = barHeight * 0.22f
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    theme.colors[0].copy(alpha = 0.25f),
                    Color.Transparent
                ),
                startY = baseLineY,
                endY = baseLineY + mirrorHeight
            ),
            topLeft = Offset(x, baseLineY + 2.dp.toPx()),
            size = Size(barWidth, mirrorHeight),
            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
        )
    }

    // Baseline grid separator
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                theme.colors[0].copy(alpha = 0.4f),
                Color.White.copy(alpha = 0.7f),
                theme.colors[0].copy(alpha = 0.4f),
                Color.Transparent
            )
        ),
        start = Offset(0f, baseLineY),
        end = Offset(size.width, baseLineY),
        strokeWidth = 1.5.dp.toPx()
    )
}

/**
 * 2. Cyber Waveform (Multi-harmonic Bezier Ribbon Waves)
 */
private fun DrawScope.drawCyberWave(
    spectrum: FloatArray,
    theme: VisualizerTheme,
    isPlaying: Boolean,
    time: Float
) {
    val centerY = size.height * 0.52f
    val amplitude = size.height * 0.34f
    val points = 80
    val stepX = size.width / (points - 1)

    // Draw 3 layers of harmonic fluid wave ribbons
    val layerAlphas = listOf(0.9f, 0.55f, 0.3f)
    val layerPhases = listOf(0.0f, 1.4f, 2.8f)
    val layerScales = listOf(1.0f, 0.75f, 0.5f)

    for (layer in 0..2) {
        val path = Path()
        val fillPath = Path()
        fillPath.moveTo(0f, size.height)

        for (p in 0 until points) {
            val progress = p.toFloat() / (points - 1)
            val bandIdx = ((progress * (spectrum.size - 1)).toInt()).coerceIn(0, spectrum.size - 1)
            val audioVal = spectrum[bandIdx]

            // Harmonic wave formula
            val wave1 = sin(progress * 4.0 * PI + (time * 0.05f) + layerPhases[layer])
            val wave2 = cos(progress * 8.0 * PI - (time * 0.03f))
            val envelope = sin(progress * PI) // dampen at ends
            val yOffset = (wave1 * 0.6 + wave2 * 0.4) * audioVal * amplitude * layerScales[layer] * envelope

            val x = p * stepX
            val y = (centerY + yOffset).toFloat()

            if (p == 0) {
                path.moveTo(x, y)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        // Filled gradient area under wave
        val waveColor = theme.colors.getOrElse(layer) { theme.colors[0] }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    waveColor.copy(alpha = layerAlphas[layer] * 0.28f),
                    Color.Transparent
                ),
                startY = centerY - amplitude,
                endY = size.height
            )
        )

        // Glowing stroke line
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    theme.colors[0].copy(alpha = layerAlphas[layer] * 0.5f),
                    Color.White.copy(alpha = layerAlphas[layer]),
                    theme.colors.last().copy(alpha = layerAlphas[layer] * 0.5f)
                )
            ),
            style = Stroke(
                width = (3.5f - layer).dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * 3. Circular Orbital Core with Radial Particle Rays
 */
private fun DrawScope.drawCircularPulse(
    spectrum: FloatArray,
    theme: VisualizerTheme,
    isPlaying: Boolean,
    time: Float
) {
    val center = Offset(size.width / 2f, size.height * 0.48f)
    val baseRadius = size.minDimension * 0.22f
    val maxRayLength = size.minDimension * 0.22f
    val rayCount = 64

    val rotationAngle = (time * 12f) % 360f

    rotate(rotationAngle, pivot = center) {
        // Draw Radial Spikes
        for (i in 0 until rayCount) {
            val angleDeg = (i.toFloat() / rayCount) * 360f
            val angleRad = Math.toRadians(angleDeg.toDouble())

            val spectrumIdx = (abs(i - rayCount / 2) % spectrum.size)
            val energy = spectrum[spectrumIdx]
            val rayLength = (energy * maxRayLength).coerceAtLeast(6.dp.toPx())

            val innerX = (center.x + cos(angleRad) * baseRadius).toFloat()
            val innerY = (center.y + sin(angleRad) * baseRadius).toFloat()
            val outerX = (center.x + cos(angleRad) * (baseRadius + rayLength)).toFloat()
            val outerY = (center.y + sin(angleRad) * (baseRadius + rayLength)).toFloat()

            val color = if (i % 2 == 0) theme.colors[0] else theme.colors.getOrElse(1) { theme.colors[0] }

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        color,
                        Color.Transparent
                    ),
                    start = Offset(innerX, innerY),
                    end = Offset(outerX, outerY)
                ),
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

    // Inner Glowing Core Sphere
    val avgBass = (spectrum[0] + spectrum[1] + spectrum[2]) / 3f
    val coreRadius = baseRadius * (0.85f + avgBass * 0.25f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.85f),
                theme.colors[0].copy(alpha = 0.7f),
                theme.colors.getOrElse(1) { theme.colors[0] }.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = center,
            radius = coreRadius
        ),
        radius = coreRadius,
        center = center
    )

    // Inner Orbit Ring
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = baseRadius * 0.92f,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
}

/**
 * 4. Particle Starburst Fireworks
 */
private fun DrawScope.drawParticleStarburst(
    spectrum: FloatArray,
    theme: VisualizerTheme,
    isPlaying: Boolean,
    time: Float
) {
    val center = Offset(size.width / 2f, size.height * 0.48f)
    val totalParticles = 72
    val avgEnergy = spectrum.average().toFloat()

    for (i in 0 until totalParticles) {
        val angleRad = (i.toFloat() / totalParticles) * 2 * PI + (time * 0.01)
        val bandIdx = i % spectrum.size
        val energy = spectrum[bandIdx]

        // Expanding distance based on sine and energy
        val dist = (size.minDimension * 0.12f) + (energy * size.minDimension * 0.36f) * (0.7f + sin(time * 0.1 + i).toFloat() * 0.3f)
        val x = (center.x + cos(angleRad) * dist).toFloat()
        val y = (center.y + sin(angleRad) * dist).toFloat()
        val particleRadius = (2.5f + energy * 4.5f).dp.toPx()

        val color = theme.colors[i % theme.colors.size]

        // Particle Point
        drawCircle(
            color = color.copy(alpha = (0.4f + energy * 0.6f).coerceIn(0.2f, 1.0f)),
            radius = particleRadius,
            center = Offset(x, y)
        )

        // Trailing streak toward center
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                start = Offset(x, y),
                end = center
            ),
            start = Offset(x, y),
            end = Offset((center.x + cos(angleRad) * (dist * 0.7f)).toFloat(), (center.y + sin(angleRad) * (dist * 0.7f)).toFloat()),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Central pulsing reactor
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                theme.colors[0].copy(alpha = 0.8f),
                Color.Transparent
            ),
            center = center,
            radius = 28.dp.toPx() * (1f + avgEnergy * 0.4f)
        ),
        radius = 28.dp.toPx() * (1f + avgEnergy * 0.4f),
        center = center
    )
}

/**
 * 5. Holographic 3D Energy Orb
 */
private fun DrawScope.drawHolographicOrb(
    spectrum: FloatArray,
    theme: VisualizerTheme,
    isPlaying: Boolean,
    time: Float
) {
    val center = Offset(size.width / 2f, size.height * 0.48f)
    val maxRadius = size.minDimension * 0.32f
    val ringCount = 8

    val bass = (spectrum[0] + spectrum[1]) / 2f
    val rotation = (time * 15f) % 360f

    for (r in 0 until ringCount) {
        val radiusFraction = (r + 1).toFloat() / ringCount
        val radius = maxRadius * radiusFraction
        val ringEnergy = spectrum[(r * 3) % spectrum.size]

        val ringWidth = radius * (1.0f + ringEnergy * 0.35f)
        val ringHeight = radius * (0.4f + bass * 0.25f)

        val ringAngle = rotation * (if (r % 2 == 0) 1f else -1f) + (r * 22f)

        rotate(ringAngle, pivot = center) {
            drawOval(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        theme.colors[0].copy(alpha = 0.85f),
                        theme.colors.getOrElse(1) { Color.White }.copy(alpha = 0.4f),
                        theme.colors.last().copy(alpha = 0.9f),
                        theme.colors[0].copy(alpha = 0.85f)
                    ),
                    center = center
                ),
                topLeft = Offset(center.x - ringWidth, center.y - ringHeight),
                size = Size(ringWidth * 2f, ringHeight * 2f),
                style = Stroke(
                    width = (2.5f + ringEnergy * 2f).dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }

    // Glowing core nucleus
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                theme.colors[0].copy(alpha = 0.9f),
                theme.colors.last().copy(alpha = 0.4f),
                Color.Transparent
            ),
            center = center,
            radius = maxRadius * 0.42f * (1f + bass * 0.3f)
        ),
        radius = maxRadius * 0.42f * (1f + bass * 0.3f),
        center = center
    )
}
