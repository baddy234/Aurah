package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    var isPulsing by remember { mutableStateOf(false) }
    var decodedText by remember { mutableStateOf("") }
    
    val targetText = "AURA"
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#\$%&*"

    LaunchedEffect(Unit) {
        isVisible = true
        
        // Cyber-decode animation
        val duration = 1500L
        val step = 50L
        val steps = (duration / step).toInt()
        
        for (i in 0..steps) {
            val progress = i.toFloat() / steps
            val numDecoded = (progress * targetText.length).toInt()
            
            val builder = StringBuilder()
            for (j in targetText.indices) {
                if (j < numDecoded) {
                    builder.append(targetText[j])
                } else {
                    builder.append(chars.random())
                }
            }
            decodedText = builder.toString()
            delay(step)
        }
        decodedText = targetText
        
        isPulsing = true
        delay(1200) // Hold to show the final decoded text and glow
        isVisible = false
        delay(500)
        onSplashFinished()
    }

    val scale by animateFloatAsState(
        targetValue = if (isPulsing) 1.08f else 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "pulse_scale"
    )
    
    val tracking by animateFloatAsState(
        targetValue = if (isPulsing) 14f else 4f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "tracking_expansion"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Aura Brand Logo with scaling pulse
                AuraBrandLogo(
                    modifier = Modifier.scale(scale),
                    logoSize = 120.dp,
                    isVideoTheme = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Futuristic Decoding Text with tracking and shadow glow
                Text(
                    text = decodedText,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = if (isPulsing) 0.8f else 0.3f),
                            offset = Offset(0f, 0f),
                            blurRadius = if (isPulsing) 25f else 10f
                        )
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = tracking.sp,
                    modifier = Modifier.alpha(if (decodedText.isNotEmpty()) 1f else 0f)
                )
            }
        }
    }
}
