package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryUltraviolet,
    onPrimary = Color.White,
    primaryContainer = PrimaryUltravioletVariant,
    secondary = SecondaryCyan,
    onSecondary = CanvasDefault,
    tertiary = TertiaryEmerald,
    background = CanvasDefault,
    onBackground = TextTitle,
    surface = SurfaceBase,
    onSurface = TextTitle,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = TextArtist,
    error = ErrorRed,
    errorContainer = ErrorContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF7C3AED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = Color(0xFF0891B2),
    onSecondary = Color.White,
    tertiary = Color(0xFF059669),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    error = Color(0xFFDC2626),
    errorContainer = Color(0xFFFEE2E2)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  accentColorIndex: Int = 0,
  content: @Composable () -> Unit,
) {
  val accentColor = when (accentColorIndex) {
    1 -> AccentCyan
    2 -> AccentEmerald
    3 -> AccentRose
    4 -> AccentAmber
    5 -> AccentIndigo
    else -> PrimaryUltraviolet
  }

  val baseColorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val colorScheme = if (!dynamicColor) {
    baseColorScheme.copy(
      primary = accentColor,
      primaryContainer = accentColor.copy(alpha = if (darkTheme) 0.35f else 0.18f)
    )
  } else {
    baseColorScheme
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
