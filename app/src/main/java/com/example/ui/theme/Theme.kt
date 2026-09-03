package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = OceanBlueDark,
    onPrimary = NaturalTextPrimary,
    primaryContainer = OceanBlueOnContainer,
    onPrimaryContainer = OceanBlueContainer,
    secondary = NaturalSlateContainer,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardBorderDark,
    onBackground = NaturalCanvasBackground,
    onSurface = NaturalCanvasBackground,
    outline = CardBorderDark,
    error = StatusOverdue,
    errorContainer = StatusOverdueContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = OceanBluePrimary,
    onPrimary = OceanBlueOnPrimary,
    primaryContainer = OceanBlueContainer,
    onPrimaryContainer = OceanBlueOnContainer,
    secondary = NaturalSlate,
    secondaryContainer = NaturalSlateContainer,
    onSecondaryContainer = NaturalSlateOnContainer,
    background = NaturalCanvasBackground,
    surface = NaturalCanvasSurface,
    surfaceVariant = NaturalSurfaceVariant,
    onBackground = NaturalTextPrimary,
    onSurface = NaturalTextPrimary,
    onSurfaceVariant = NaturalTextSecondary,
    outline = NaturalBorderOutline,
    outlineVariant = NaturalOutlineVariant,
    error = StatusOverdue,
    errorContainer = StatusOverdueContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep Natural Tones consistent and distinctive across all devices
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
