package com.example

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.PrimaryUltraviolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TertiaryEmerald
import androidx.compose.animation.animateColorAsState

/**
 * Dynamic artwork gradient background generator based on current playing audio file cover colors.
 * smoothly animates color changes and adds a flowing gradient effect.
 */
@Composable
fun getArtworkBackgroundGradient(coverColorStr: String?, isDarkTheme: Boolean = true): Brush {
    val targetBaseColor = remember(coverColorStr) {
        try {
            if (!coverColorStr.isNullOrBlank()) {
                Color(android.graphics.Color.parseColor("#$coverColorStr"))
            } else {
                Color(0xFF6366F1)
            }
        } catch (_: Exception) {
            Color(0xFF6366F1)
        }
    }

    val animatedBaseColor by androidx.compose.animation.animateColorAsState(
        targetValue = targetBaseColor,
        animationSpec = tween(durationMillis = 1500, easing = androidx.compose.animation.core.LinearOutSlowInEasing),
        label = "baseColor"
    )

    val topColor = animatedBaseColor.copy(alpha = if (isDarkTheme) 0.5f else 0.25f)
    val midColor = animatedBaseColor.copy(alpha = if (isDarkTheme) 0.15f else 0.08f)
    val bottomColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)

    val infiniteTransition = rememberInfiniteTransition(label = "gradientFlow")
    
    val animatedX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "xFlow"
    )
    
    val animatedY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yFlow"
    )

    return Brush.radialGradient(
        colors = listOf(topColor, midColor, bottomColor),
        center = Offset(200f + animatedX * 800f, -200f + animatedY * 600f),
        radius = 2200f + animatedX * 400f
    )
}

/**
 * Modern, styled default album artwork when no image is available or image loading fails.
 */
@Composable
fun DefaultAlbumArt(
    modifier: Modifier = Modifier,
    coverColorStr: String? = null,
    iconSize: Dp = 24.dp
) {
    val baseColor = try {
        if (!coverColorStr.isNullOrBlank()) {
            Color(android.graphics.Color.parseColor("#$coverColorStr"))
        } else {
            PrimaryUltraviolet
        }
    } catch (_: Exception) {
        PrimaryUltraviolet
    }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            baseColor.copy(alpha = 0.95f),
            baseColor.copy(alpha = 0.55f),
            Color(0xFF0F172A)
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 400f)
    )

    Box(
        modifier = modifier
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Subtle concentric vinyl grooves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = minOf(size.width, size.height) / 2f

            if (maxRadius > 15f) {
                // Outer groove
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = maxRadius * 0.85f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Mid groove
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = maxRadius * 0.65f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Inner groove
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = maxRadius * 0.45f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Center music icon inside a translucent pill/circle
        Box(
            modifier = Modifier
                .size(iconSize * 1.6f)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = "Default Music Artwork",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * Resilient track thumbnail that loads remote/local artwork and automatically falls back to default artwork.
 */
/**
 * Resilient track thumbnail that loads remote/local artwork and automatically falls back to curated/default artwork.
 */
@Composable
fun TrackThumbnail(
    artworkUrl: String?,
    coverColorStr: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    isVideo: Boolean = false,
    title: String? = null,
    artist: String? = null
) {
    var hasError by remember(artworkUrl) { mutableStateOf(false) }
    val effectiveUrl = remember(artworkUrl, title, artist, isVideo) {
        if (!artworkUrl.isNullOrBlank()) artworkUrl
        else if (isVideo) {
            CoverImageProvider.getVideoFallbackCover(title ?: "Video", artist ?: "Local Video")
        } else if (!title.isNullOrBlank() || !artist.isNullOrBlank()) {
            CoverImageProvider.getFallbackCover(title ?: "Audio", artist ?: "Music")
        } else null
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        if (!effectiveUrl.isNullOrBlank() && !hasError) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(effectiveUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Track Artwork",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { hasError = true }
            )
        } else {
            DefaultAlbumArt(
                modifier = Modifier.fillMaxSize(),
                coverColorStr = coverColorStr,
                iconSize = size * 0.45f
            )
        }
        if (isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = "Video",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.4f)
                )
            }
        }
    }
}

/**
 * High quality circular or rounded Artist Avatar that loads artist portrait photos.
 */
@Composable
fun ArtistAvatar(
    artistName: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: Shape = CircleShape,
    firstTrackArtwork: String? = null
) {
    var hasError by remember(artistName) { mutableStateOf(false) }
    val imageUrl = remember(artistName, firstTrackArtwork) {
        CoverImageProvider.getArtistCover(artistName, firstTrackArtwork)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank() && !hasError) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Artist Photo for $artistName",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { hasError = true }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = artistName.take(1).uppercase(),
                    style = if (size > 60.dp) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Large, high-fidelity artwork card for the Now Playing Carousel.
 */
@Composable
fun NowPlayingArtworkCard(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    artworkUri: String?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    var hasError by remember(artworkUri) { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .shadow(16.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val effectiveArtwork = remember(artworkUri, displayTitle, displayArtist) {
                if (!artworkUri.isNullOrBlank()) artworkUri
                else CoverImageProvider.getFallbackCover(displayTitle, displayArtist)
            }
            if (!effectiveArtwork.isNullOrBlank() && !hasError) {
                // Full high quality artwork
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(effectiveArtwork)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Album Artwork for $displayTitle",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onError = { hasError = true }
                )
            } else {
                // Default high quality Vinyl Album Cover Art
                DefaultAlbumArt(
                    modifier = Modifier.fillMaxSize(),
                    coverColorStr = track.coverColorStr,
                    iconSize = 72.dp
                )
            }

            // Playing status disc badge in top right (minimal and clean)
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = "Playing",
                        tint = TertiaryEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
