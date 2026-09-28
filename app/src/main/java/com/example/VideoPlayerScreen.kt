package com.example
import android.app.PictureInPictureParams
import android.os.Build
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.app.Activity


import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Audiotrack
import android.content.Context
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import android.media.AudioManager
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.ui.FrostedGlassDefaults
import com.example.ui.FrostedGlassSurface
import kotlinx.coroutines.delay

enum class VideoResizeMode(val title: String, val mode: Int) {
    FIT("Fit Screen", AspectRatioFrameLayout.RESIZE_MODE_FIT),
    FILL("Crop / Fill", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
    STRETCH("Stretch 16:9", AspectRatioFrameLayout.RESIZE_MODE_FILL)
}

enum class VideoQuality(val title: String) {
    AUTO("Auto (HQ)"),
    P1080("1080p Full HD"),
    P720("720p HD"),
    P480("480p SD"),
    P360("360p")
}

enum class ScreenOrientationMode(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    AUTO("Auto-Rotate", androidx.compose.material.icons.Icons.Filled.ScreenRotation),
    PORTRAIT_LOCKED("Portrait Locked", androidx.compose.material.icons.Icons.Filled.Smartphone),
    LANDSCAPE_LOCKED("Landscape Locked", androidx.compose.material.icons.Icons.Filled.DesktopWindows)
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerModal(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    player: Player?,
    isPlaying: Boolean,
    progressMs: Long,
    durationMs: Long,
    playbackSpeed: Float,
    isFullscreen: Boolean,
    lockLandscape: Boolean = false,
    onClose: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onOpenVideoSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Handle screen orientation internally
    var orientationMode by remember { 
        mutableStateOf(if (isFullscreen || lockLandscape) ScreenOrientationMode.LANDSCAPE_LOCKED else ScreenOrientationMode.AUTO) 
    }

    DisposableEffect(orientationMode) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = when (orientationMode) {
            ScreenOrientationMode.AUTO -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            ScreenOrientationMode.PORTRAIT_LOCKED -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ScreenOrientationMode.LANDSCAPE_LOCKED -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        onDispose {
            activity?.requestedOrientation = originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false
        )
    ) {
        val view = LocalView.current
        DisposableEffect(view) {
            val dialogWindow = (view.parent as? DialogWindowProvider)?.window
            if (dialogWindow != null) {
                dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
                dialogWindow.setDimAmount(0f)
            }
            onDispose {}
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoPlayerContent(
                track = track,
                displayTitle = displayTitle,
                displayArtist = displayArtist,
                player = player,
                isPlaying = isPlaying,
                progressMs = progressMs,
                durationMs = durationMs,
                playbackSpeed = playbackSpeed,
                isFullscreen = isFullscreen,
                onToggleFullscreen = onToggleFullscreen,
                orientationMode = orientationMode,
                onCycleOrientation = {
                    orientationMode = when (orientationMode) {
                        ScreenOrientationMode.AUTO -> ScreenOrientationMode.PORTRAIT_LOCKED
                        ScreenOrientationMode.PORTRAIT_LOCKED -> ScreenOrientationMode.LANDSCAPE_LOCKED
                        ScreenOrientationMode.LANDSCAPE_LOCKED -> ScreenOrientationMode.AUTO
                    }
                },
                onClose = onClose,
                onPlayPause = onPlayPause,
                onSeek = onSeek,
                onSeekRelative = onSeekRelative,
                onSetPlaybackSpeed = onSetPlaybackSpeed,
                onOpenVideoSettings = onOpenVideoSettings,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerContent(
    track: Track,
    displayTitle: String,
    displayArtist: String,
    player: Player?,
    isPlaying: Boolean,
    progressMs: Long,
    durationMs: Long,
    playbackSpeed: Float,
    isFullscreen: Boolean = true,
    onToggleFullscreen: () -> Unit = {},
    orientationMode: ScreenOrientationMode,
    onCycleOrientation: () -> Unit,
    onClose: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onOpenVideoSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var isControlsLocked by remember { mutableStateOf(false) }
    var currentResizeMode by remember { mutableStateOf(VideoResizeMode.FIT) }
    var currentQuality by remember { mutableStateOf(VideoQuality.AUTO) }
    var selectedSubtitle by remember { mutableStateOf("Off") }
    var selectedAudioTrack by remember { mutableStateOf("Original") }
    var isMuted by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableStateOf(0) }
    var swipeGesturesEnabled by remember { mutableStateOf(true) }

    var isFullyFullscreen by remember { mutableStateOf(isFullscreen) }

    LaunchedEffect(isFullscreen) {
        isFullyFullscreen = isFullscreen
    }

    val view = LocalView.current
    val activity = LocalContext.current as? Activity
    DisposableEffect(view, controlsVisible, isFullyFullscreen) {
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window ?: activity?.window
        if (dialogWindow != null) {
            val insetsController = WindowCompat.getInsetsController(dialogWindow, dialogWindow.decorView)
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isFullyFullscreen && !controlsVisible) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            val dialogWindow = (view.parent as? DialogWindowProvider)?.window ?: activity?.window
            if (dialogWindow != null) {
                val insetsController = WindowCompat.getInsetsController(dialogWindow, dialogWindow.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    var showOptionsModal by remember { mutableStateOf(false) }

    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragProgressMs by remember { mutableLongStateOf(0L) }
    var bufferedFraction by remember { mutableFloatStateOf(0f) }

    var doubleTapRippleSide by remember { mutableStateOf<String?>(null) } // "left" or "right"
    var screenModeNotification by remember { mutableStateOf<String?>(null) }

    // Track buffering progress from player
    LaunchedEffect(player, isPlaying, progressMs) {
        val bufPos = player?.bufferedPosition ?: 0L
        val dur = if (durationMs > 0) durationMs else (player?.duration?.coerceAtLeast(1L) ?: 1L)
        bufferedFraction = (bufPos.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
    }

    LaunchedEffect(isMuted, player) {
        player?.volume = if (isMuted) 0f else 1f
    }

    // Reset double tap animation indicator
    LaunchedEffect(doubleTapRippleSide) {
        if (doubleTapRippleSide != null) {
            delay(650)
            doubleTapRippleSide = null
        }
    }

    // Reset screen size / aspect ratio HUD indicator
    LaunchedEffect(screenModeNotification) {
        if (screenModeNotification != null) {
            delay(1400)
            screenModeNotification = null
        }
    }

    // Auto-hide controls timer (4.5 seconds)
    LaunchedEffect(controlsVisible, isPlaying, isDraggingSlider, showOptionsModal) {
        if (controlsVisible && isPlaying && !isDraggingSlider && !isControlsLocked && !showOptionsModal) {
            delay(4500)
            controlsVisible = false
        }
    }

    val currentEffectiveProgress = if (isDraggingSlider) dragProgressMs else progressMs
    val fraction = if (durationMs > 0) (currentEffectiveProgress.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0D14))
    ) {
        val isWideScreen = maxWidth > 600.dp
        val showFullScreenVideo = isFullyFullscreen || isLandscape

        if (showFullScreenVideo) {
            // ================= FULLSCREEN OVERLAY MODE (PORTRAIT & LANDSCAPE) =================
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Video Surface View spanning full screen
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            this.player = player
                            this.useController = false
                            this.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                            this.setKeepContentOnPlayerReset(true)
                            this.resizeMode = currentResizeMode.mode
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { playerView ->
                        playerView.player = player
                        playerView.resizeMode = currentResizeMode.mode
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Gesture detector layer for full screen
                VideoGestureOverlay(
                    isLocked = isControlsLocked,
                    gesturesEnabled = swipeGesturesEnabled,
                    onToggleControls = { controlsVisible = !controlsVisible },
                    onDoubleTapLeft = {
                        onSeekRelative(-10000L)
                        doubleTapRippleSide = "left"
                    },
                    onDoubleTapRight = {
                        onSeekRelative(10000L)
                        doubleTapRippleSide = "right"
                    }
                )

                // Double tap visual feedback indicator
                DoubleTapIndicator(side = doubleTapRippleSide)

                // Screen size / Aspect ratio change HUD indicator
                AnimatedVisibility(
                    visible = screenModeNotification != null,
                    enter = fadeIn(tween(150)) + scaleIn(tween(150)),
                    exit = fadeOut(tween(250)) + scaleOut(tween(250)),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    FrostedGlassSurface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color.Black.copy(alpha = 0.85f)
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
                            Text(
                                text = "Screen: ${screenModeNotification ?: ""}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Overlay Controls (Top, Center, Bottom Progress Bar & Timeline)
                VideoOnCanvasControls(
                    displayTitle = displayTitle,
                    displayArtist = displayArtist,
                    track = track,
                    currentQuality = currentQuality,
                    controlsVisible = controlsVisible,
                    isControlsLocked = isControlsLocked,
                    showOptionsModal = showOptionsModal,
                    isPlaying = isPlaying,
                    currentEffectiveProgress = currentEffectiveProgress,
                    durationMs = durationMs,
                    fraction = fraction,
                    bufferedFraction = bufferedFraction,
                    isDraggingSlider = isDraggingSlider,
                    orientationMode = orientationMode,
                    isLandscape = isLandscape,
                    currentResizeMode = currentResizeMode,
                    isWideScreen = isWideScreen,
                    isMuted = isMuted,
                    isFullyFullscreen = isFullyFullscreen,
                    onClose = onClose,
                    onCycleOrientation = onCycleOrientation,
                    onLockToggle = { isControlsLocked = !isControlsLocked },
                    onOptionsClick = { showOptionsModal = true },
                    onToggleMute = { isMuted = !isMuted },
                    onToggleFullscreen = {
                        isFullyFullscreen = !isFullyFullscreen
                        onToggleFullscreen()
                    },
                    onSeekRelative = onSeekRelative,
                    onPlayPause = onPlayPause,
                    onSliderChange = { newFraction ->
                        isDraggingSlider = true
                        dragProgressMs = (newFraction * durationMs).toLong()
                    },
                    onSliderFinished = {
                        onSeek((dragProgressMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f))
                        isDraggingSlider = false
                    },
                    onCycleResizeMode = {
                        val nextMode = when (currentResizeMode) {
                            VideoResizeMode.FIT -> VideoResizeMode.FILL
                            VideoResizeMode.FILL -> VideoResizeMode.STRETCH
                            VideoResizeMode.STRETCH -> VideoResizeMode.FIT
                        }
                        currentResizeMode = nextMode
                        screenModeNotification = when (nextMode) {
                            VideoResizeMode.FIT -> "Fit Screen"
                            VideoResizeMode.FILL -> "Fill Screen (Edge-to-Edge)"
                            VideoResizeMode.STRETCH -> "Stretch to Display"
                        }
                    }
                )
            }
        } else {
            // ================= PORTRAIT / STANDARD MODE =================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0D14))
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // TOP CONTROLS FOR PORTRAIT MODE
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = displayArtist,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            isFullyFullscreen = true
                            onToggleFullscreen()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), CircleShape)
                            .testTag("video_expand_fullscreen_button")
                    ) {
                        Icon(Icons.Filled.Fullscreen, contentDescription = "Full Screen", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    IconButton(
                        onClick = { showOptionsModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(Icons.Filled.Tune, contentDescription = "Options", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // 1. CINEMATIC 16:9 VIDEO CANVAS BOX (Framed with Ambient Glow in Portrait)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Dynamic Ambient Glow (Ambilight effect) behind canvas
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(4.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.32f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                    )

                    // 16:9 Canvas Box with smooth corners, subtle specular border, and elevation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // Video Surface View
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    this.player = player
                                    this.useController = false
                                    this.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                                    this.setKeepContentOnPlayerReset(true)
                                    this.resizeMode = currentResizeMode.mode
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            update = { playerView ->
                                playerView.player = player
                                playerView.resizeMode = currentResizeMode.mode
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Fullscreen Badge on Top-Right of video box
                        FrostedGlassSurface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .clickable {
                                    isFullyFullscreen = true
                                    onToggleFullscreen()
                                },
                            shape = CircleShape,
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.Fullscreen, contentDescription = "Full Screen", tint = Color.White, modifier = Modifier.size(16.dp))
                                Text("Full Screen", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Gestures on the video canvas
                        VideoGestureOverlay(
                            isLocked = isControlsLocked,
                    gesturesEnabled = swipeGesturesEnabled,
                            onToggleControls = { controlsVisible = !controlsVisible },
                            onDoubleTapLeft = {
                                onSeekRelative(-10000L)
                                doubleTapRippleSide = "left"
                            },
                            onDoubleTapRight = {
                                onSeekRelative(10000L)
                                doubleTapRippleSide = "right"
                            }
                        )

                        // Double tap visual feedback indicator
                        DoubleTapIndicator(side = doubleTapRippleSide)

                        // Screen size / Aspect ratio change HUD indicator
                        androidx.compose.animation.AnimatedVisibility(
                            visible = screenModeNotification != null,
                            enter = fadeIn(tween(150)) + scaleIn(tween(150)),
                            exit = fadeOut(tween(250)) + scaleOut(tween(250)),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            FrostedGlassSurface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                shape = RoundedCornerShape(20.dp),
                                backgroundColor = Color.Black.copy(alpha = 0.85f)
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
                                    Text(
                                        text = "Screen: ${screenModeNotification ?: ""}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // In-Canvas Controls (Top Header, Center Play/Seek, Bottom Timeframe Slider & Time)
                        VideoOnCanvasControls(
                            displayTitle = displayTitle,
                            displayArtist = displayArtist,
                            track = track,
                            currentQuality = currentQuality,
                            controlsVisible = controlsVisible,
                            isControlsLocked = isControlsLocked,
                            showOptionsModal = showOptionsModal,
                            isPlaying = isPlaying,
                            currentEffectiveProgress = currentEffectiveProgress,
                            durationMs = durationMs,
                            fraction = fraction,
                            bufferedFraction = bufferedFraction,
                            isDraggingSlider = isDraggingSlider,
                            orientationMode = orientationMode,
                            isLandscape = false,
                            currentResizeMode = currentResizeMode,
                            isWideScreen = false,
                            isMuted = isMuted,
                            isFullyFullscreen = isFullyFullscreen,
                            onClose = onClose,
                            onCycleOrientation = onCycleOrientation,
                            onLockToggle = { isControlsLocked = !isControlsLocked },
                            onOptionsClick = { showOptionsModal = true },
                            onToggleMute = { isMuted = !isMuted },
                            onToggleFullscreen = {
                                isFullyFullscreen = true
                                onToggleFullscreen()
                            },
                            onSeekRelative = onSeekRelative,
                            onPlayPause = onPlayPause,
                            onSliderChange = { newFraction ->
                                isDraggingSlider = true
                                dragProgressMs = (newFraction * durationMs).toLong()
                            },
                            onSliderFinished = {
                                onSeek((dragProgressMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f))
                                isDraggingSlider = false
                            },
                            onCycleResizeMode = {
                                val nextMode = when (currentResizeMode) {
                                    VideoResizeMode.FIT -> VideoResizeMode.FILL
                                    VideoResizeMode.FILL -> VideoResizeMode.STRETCH
                                    VideoResizeMode.STRETCH -> VideoResizeMode.FIT
                                }
                                currentResizeMode = nextMode
                                screenModeNotification = nextMode.title
                            }
                        )
                    }
                }

                // PORTRAIT TIMELINE & SCRUBBER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Dual-Layer Scrubber (Buffered Progress + Interactive Track)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Underlay Track for Buffered Stream Progress
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            if (bufferedFraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = bufferedFraction.coerceIn(0f, 1f))
                                        .fillMaxHeight()
                                        .background(Color.White.copy(alpha = 0.30f))
                                )
                            }
                        }

                        // Interactive Slider Layer
                        Slider(
                            value = fraction,
                            onValueChange = { newFraction ->
                                isDraggingSlider = true
                                dragProgressMs = (newFraction * durationMs).toLong()
                            },
                            onValueChangeFinished = {
                                onSeek((dragProgressMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f))
                                isDraggingSlider = false
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth().height(24.dp)
                        )
                    }

                    // Time on Left, Fullscreen & Aspect on Right
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Time elapsed / total duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = formatVideoTime(currentEffectiveProgress),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "/",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.45f)
                            )
                            Text(
                                text = formatVideoTime(durationMs),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.70f)
                            )
                        }

                        // Action buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val context = LocalContext.current
                            val activity = context as? Activity
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                IconButton(
                                    onClick = {
                                        try {
                                            val params = PictureInPictureParams.Builder().build()
                                            activity?.enterPictureInPictureMode(params)
                                        } catch (e: Exception) { }
                                    },
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PictureInPictureAlt,
                                        contentDescription = "Picture in Picture",
                                        tint = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                                    contentDescription = if (isMuted) "Unmute" else "Mute",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val nextMode = when (currentResizeMode) {
                                        VideoResizeMode.FIT -> VideoResizeMode.FILL
                                        VideoResizeMode.FILL -> VideoResizeMode.STRETCH
                                        VideoResizeMode.STRETCH -> VideoResizeMode.FIT
                                    }
                                    currentResizeMode = nextMode
                                    screenModeNotification = nextMode.title
                                },
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(Icons.Filled.AspectRatio, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                            }

                            IconButton(
                                onClick = {
                                    onCycleOrientation()
                                },
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(orientationMode.icon, contentDescription = orientationMode.title, tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = {
                                    isFullyFullscreen = true
                                    onToggleFullscreen()
                                },
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                            ) {
                                Icon(Icons.Filled.Fullscreen, contentDescription = "Full Screen", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // 2. MEDIA DETAILS PANEL (Below the 16:9 Canvas in Portrait)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title & Artist Info Card
                    FrostedGlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color.White.copy(alpha = 0.05f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = displayTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = displayArtist,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = track.resolution ?: currentQuality.title.substringBefore(" "),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Media Tech Specs Tag Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SpecTag(icon = Icons.Filled.HighQuality, text = "HQ Video")
                                SpecTag(icon = Icons.Filled.AspectRatio, text = currentResizeMode.title.substringBefore(" "))
                                SpecTag(icon = Icons.Filled.Speed, text = "${playbackSpeed}x Speed")
                                if (selectedSubtitle != "Off") {
                                    SpecTag(icon = Icons.Filled.Subtitles, text = "CC On")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. SIDE SIDEBAR FOR VIDEO OPTIONS
        AnimatedVisibility(
            visible = showOptionsModal,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showOptionsModal = false },
                contentAlignment = Alignment.CenterEnd
            ) {
                FrostedGlassSurface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 380.dp, min = 280.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                        .padding(start = 12.dp)
                        .navigationBarsPadding(),
                    shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp),
                    backgroundColor = Color.White.copy(alpha = 0.03f),
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // Modal Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = { showOptionsModal = false },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // PLAYBACK SPEED
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Playback Speed",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val topRowSpeeds = listOf(0.5f, 0.75f, 1.0f)
                                val bottomRowSpeeds = listOf(1.25f, 1.5f, 2.0f)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    topRowSpeeds.forEach { speed ->
                                        val isSelected = playbackSpeed == speed
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onSetPlaybackSpeed(speed) }
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f))
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${speed}x",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    bottomRowSpeeds.forEach { speed ->
                                        val isSelected = playbackSpeed == speed
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onSetPlaybackSpeed(speed) }
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f))
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${speed}x",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // QUALITY & SUBTITLES SECTION
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Quality Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        currentQuality = when (currentQuality) {
                                            VideoQuality.AUTO -> VideoQuality.P1080
                                            VideoQuality.P1080 -> VideoQuality.P720
                                            VideoQuality.P720 -> VideoQuality.P480
                                            VideoQuality.P480 -> VideoQuality.P360
                                            VideoQuality.P360 -> VideoQuality.AUTO
                                        }
                                    }
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.HighQuality,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Quality",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = currentQuality.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }

                            // Audio Track Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedAudioTrack = when (selectedAudioTrack) {
                                            "Original" -> "English (Descriptive)"
                                            "English (Descriptive)" -> "Spanish"
                                            "Spanish" -> "Original"
                                            else -> "Original"
                                        }
                                    }
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Audiotrack,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Audio Track",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = selectedAudioTrack,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }

                            // Subtitles Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedSubtitle = when (selectedSubtitle) {
                                            "Off" -> "English (CC)"
                                            "English (CC)" -> "Spanish"
                                            "Spanish" -> "French"
                                            else -> "Off"
                                        }
                                    }
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Subtitles,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Captions",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = selectedSubtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        
                        // SWIPE GESTURES
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Swipe Gestures (Volume/Brightness)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            androidx.compose.material3.Switch(
                                checked = swipeGesturesEnabled,
                                onCheckedChange = { swipeGesturesEnabled = it },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // SLEEP TIMER
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Sleep Timer",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(0, 15, 30, 45, 60).forEach { mins ->
                                    val isSelected = sleepTimerMinutes == mins
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { sleepTimerMinutes = mins }
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f))
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (mins == 0) "Off" else "${mins}m",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }

                        // ALL VIDEO PLAYER SETTINGS BUTTON
                        if (onOpenVideoSettings != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        showOptionsModal = false
                                        onOpenVideoSettings()
                                    }
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Settings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "Advanced Settings",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
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
}

/**
 * Clean, Decluttered In-Canvas Overlay Controls (Header, Center Playback, Bottom Timeline Scrubber)
 * Anchored directly to the video canvas bounds with minimal clutter and glassmorphic surfaces.
 */
@Composable
private fun BoxScope.VideoOnCanvasControls(
    displayTitle: String,
    displayArtist: String,
    track: Track,
    currentQuality: VideoQuality,
    controlsVisible: Boolean,
    isControlsLocked: Boolean,
    showOptionsModal: Boolean,
    isPlaying: Boolean,
    currentEffectiveProgress: Long,
    durationMs: Long,
    fraction: Float,
    bufferedFraction: Float = 0f,
    isDraggingSlider: Boolean = false,
    orientationMode: ScreenOrientationMode,
    isLandscape: Boolean,
    currentResizeMode: VideoResizeMode,
    isWideScreen: Boolean,
    isMuted: Boolean,
    isFullyFullscreen: Boolean = true,
    onClose: () -> Unit,
    onCycleOrientation: () -> Unit,
    onLockToggle: () -> Unit,
    onOptionsClick: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFullscreen: () -> Unit = {},
    onSeekRelative: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onSliderFinished: () -> Unit,
    onCycleResizeMode: () -> Unit
) {
    // 1. TOP HEADER OVERLAY (Back, Clean Title & Options)
    AnimatedVisibility(
        visible = controlsVisible && !isControlsLocked && !showOptionsModal,
        enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it },
        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it },
        modifier = Modifier.align(Alignment.TopCenter)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    )
                )
                .displayCutoutPadding()
                .statusBarsPadding()
                .padding(
                    horizontal = if (isLandscape) 24.dp else 14.dp,
                    vertical = if (isLandscape) 12.dp else 8.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .padding(start = 6.dp, end = 8.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                        .testTag("video_close_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = displayArtist,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fullscreen / Windowed toggle button
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                            .testTag("video_fullscreen_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isFullyFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                            contentDescription = if (isFullyFullscreen) "Exit Full Screen" else "Full Screen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onLockToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LockOpen,
                            contentDescription = "Lock Screen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOptionsClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                            .testTag("video_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Options",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // 2. SCREEN LOCK FLOATING PILL (When controls are locked)
    if (isControlsLocked) {
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .displayCutoutPadding()
                .then(if (isLandscape) Modifier.statusBarsPadding() else Modifier)
                .padding(if (isLandscape) 24.dp else 16.dp)
        ) {
            FrostedGlassSurface(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onLockToggle() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = CircleShape,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Unlock Controls",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Controls Locked",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }

    // 3. CENTER PLAYBACK ACTIONS (Spacious & Clean Frosted Glass)
    AnimatedVisibility(
        visible = controlsVisible && !isControlsLocked && !showOptionsModal,
        enter = fadeIn(tween(200)) + scaleIn(tween(200)),
        exit = fadeOut(tween(200)) + scaleOut(tween(200)),
        modifier = Modifier.align(Alignment.Center)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 44.dp else 28.dp)
        ) {
            // Rewind 10s
            IconButton(
                onClick = { onSeekRelative(-10000L) },
                modifier = Modifier
                    .size(if (isLandscape) 52.dp else 46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Replay10,
                    contentDescription = "Rewind 10s",
                    tint = Color.White,
                    modifier = Modifier.size(if (isLandscape) 26.dp else 22.dp)
                )
            }

            // Center Primary Play/Pause Button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(if (isLandscape) 70.dp else 64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, Color.White.copy(alpha = 0.38f), CircleShape)
                    .testTag("video_play_pause_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(if (isLandscape) 38.dp else 34.dp)
                )
            }

            // Fast Forward 10s
            IconButton(
                onClick = { onSeekRelative(10000L) },
                modifier = Modifier
                    .size(if (isLandscape) 52.dp else 46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Forward10,
                    contentDescription = "Forward 10s",
                    tint = Color.White,
                    modifier = Modifier.size(if (isLandscape) 26.dp else 22.dp)
                )
            }
        }
    }

    // 4. PERSISTENT MINI ACCENT PROGRESS LINE (When controls are hidden)
    AnimatedVisibility(
        visible = !controlsVisible && !isControlsLocked && !showOptionsModal,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .displayCutoutPadding()
                .navigationBarsPadding()
                .height(2.5.dp)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            // Mini buffered line
            if (bufferedFraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = bufferedFraction)
                        .height(2.5.dp)
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
            // Mini played line
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = fraction)
                    .height(2.5.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }

    // 5. STUDIO-GRADE BOTTOM TIMELINE & SCRUBBER CONTROLS BAR
    AnimatedVisibility(
        visible = controlsVisible && !isControlsLocked && !showOptionsModal,
        enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it },
        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it },
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
                .displayCutoutPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = if (isLandscape) 28.dp else 16.dp,
                    vertical = 0.dp
                )
                .padding(bottom = if (isLandscape) 44.dp else 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLandscape) 12.dp else 4.dp)
            ) {
                // Floating Scrub Time Preview Bubble (Displays when dragging)
                androidx.compose.animation.AnimatedVisibility(
                    visible = isDraggingSlider,
                    enter = fadeIn(tween(120)) + scaleIn(tween(120)),
                    exit = fadeOut(tween(180)) + scaleOut(tween(180)),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = if (isLandscape) 10.dp else 4.dp)
                ) {
                    FrostedGlassSurface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = Color.Black.copy(alpha = 0.88f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${formatVideoTime(currentEffectiveProgress)} / ${formatVideoTime(durationMs)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Dual-Layer Scrubber (Buffered Progress + Interactive Track)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Underlay Track for Buffered Stream Progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                    ) {
                        if (bufferedFraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = bufferedFraction.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(Color.White.copy(alpha = 0.40f))
                            )
                        }
                    }

                    // Interactive Slider Layer with Glowing Thumb
                    Slider(
                        value = fraction,
                        onValueChange = onSliderChange,
                        onValueChangeFinished = onSliderFinished,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .testTag("video_timeline_slider")
                    )
                }

                // Clean Decluttered Row: Time on Left, Fullscreen & Aspect on Right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Time elapsed / total duration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatVideoTime(currentEffectiveProgress),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "/",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.45f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatVideoTime(durationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.70f),
                            fontSize = 12.sp
                        )
                    }

                    // Action buttons on Right (Screen Size & Fullscreen)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val context = LocalContext.current
                        val activity = context as? Activity
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            IconButton(
                                onClick = {
                                    try {
                                        val params = PictureInPictureParams.Builder().build()
                                        activity?.enterPictureInPictureMode(params)
                                    } catch (e: Exception) { }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PictureInPictureAlt,
                                    contentDescription = "Picture in Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        // Screen Size / Aspect Ratio Toggle Button
                        IconButton(
                            onClick = onCycleResizeMode,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("video_aspect_ratio_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AspectRatio,
                                contentDescription = "Screen Size (${currentResizeMode.title})",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Orientation Toggle Button
                        IconButton(
                            onClick = {
                                onCycleOrientation()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("video_orientation_button")
                        ) {
                            Icon(
                                imageVector = orientationMode.icon,
                                contentDescription = orientationMode.title,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Fullscreen Toggle Button
                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.28f), CircleShape)
                                .testTag("video_fullscreen_button")
                        ) {
                            Icon(
                                imageVector = if (isFullyFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                contentDescription = if (isFullyFullscreen) "Exit Fullscreen" else "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Gestures overlay for single-tap controls toggle and double-tap seek
 */
@Composable
private fun VideoGestureOverlay(
    isLocked: Boolean,
    gesturesEnabled: Boolean,
    onToggleControls: () -> Unit,
    onDoubleTapLeft: () -> Unit,
    onDoubleTapRight: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var showIndicator by remember { mutableStateOf(false) }
    var indicatorType by remember { mutableStateOf("volume") }
    var indicatorValue by remember { mutableFloatStateOf(0f) }

    var startDragValue by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalWidth = maxWidth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isLocked) {
                    detectTapGestures(
                        onTap = {
                            onToggleControls()
                        },
                        onDoubleTap = { offset ->
                            if (!isLocked) {
                                if (offset.x < size.width * 0.45f) {
                                    onDoubleTapLeft()
                                } else if (offset.x > size.width * 0.55f) {
                                    onDoubleTapRight()
                                } else {
                                    onToggleControls()
                                }
                            }
                        }
                    )
                }
                .pointerInput(isLocked, gesturesEnabled) {
                    if (isLocked || !gesturesEnabled) return@pointerInput
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            showIndicator = true
                            if (offset.x > size.width / 2f) {
                                // Right side: Volume
                                indicatorType = "volume"
                                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                                val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                                startDragValue = if (maxVol > 0) curVol / maxVol else 0f
                            } else {
                                // Left side: Brightness
                                indicatorType = "brightness"
                                val window = activity?.window
                                val params = window?.attributes
                                startDragValue = params?.screenBrightness?.takeIf { it >= 0f } ?: 0.5f
                            }
                            indicatorValue = startDragValue
                        },
                        onDragEnd = {
                            showIndicator = false
                        },
                        onDragCancel = {
                            showIndicator = false
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            // Calculate change (dragging up is negative dragAmount, which means increase value)
                            // size.height is the total pixels. Let's make a full screen swipe equal to 100% change
                            val delta = -(dragAmount / size.height) * 1.5f // 1.5 multiplier for sensitivity
                            var newValue = (startDragValue + delta).coerceIn(0f, 1f)
                            startDragValue = newValue
                            indicatorValue = newValue

                            if (indicatorType == "volume") {
                                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                val newVol = (newValue * maxVol).toInt()
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                            } else {
                                val window = activity?.window
                                if (window != null) {
                                    val params = window.attributes
                                    params.screenBrightness = newValue
                                    window.attributes = params
                                }
                            }
                        }
                    )
                }
        )
        
        // Indicator
        androidx.compose.animation.AnimatedVisibility(
            visible = showIndicator,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(250)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            FrostedGlassSurface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.Black.copy(alpha = 0.6f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (indicatorType == "volume") {
                            if (indicatorValue == 0f) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp
                        } else {
                            Icons.Filled.LightMode
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .width(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction = indicatorValue)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}


/**
 * Visual double-tap seeking ripple indicator
 */
@Composable
private fun DoubleTapIndicator(side: String?) {
    if (side == null) return
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = if (side == "left") Alignment.CenterStart else Alignment.CenterEnd
    ) {
        FrostedGlassSurface(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clip(CircleShape)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = CircleShape,
            backgroundColor = Color.Black.copy(alpha = 0.7f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (side == "left") Icons.Filled.Replay10 else Icons.Filled.Forward10,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (side == "left") "-10s" else "+10s",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Small tech specification tag pill
 */
@Composable
private fun SpecTag(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp
        )
    }
}

private fun formatVideoTime(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format(java.util.Locale.US, "%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    }
}
