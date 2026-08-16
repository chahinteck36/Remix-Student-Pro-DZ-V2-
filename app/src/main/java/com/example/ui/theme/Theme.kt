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
    primary = AcademicPrimaryDark,
    onPrimary = AcademicOnPrimaryDark,
    primaryContainer = AcademicPrimaryContainerDark,
    onPrimaryContainer = AcademicOnPrimaryContainerDark,
    secondary = AcademicSecondaryDark,
    onSecondary = AcademicOnSecondaryDark,
    secondaryContainer = AcademicSecondaryContainerDark,
    onSecondaryContainer = AcademicOnSecondaryContainerDark,
    tertiary = AcademicTertiaryDark,
    onTertiary = AcademicOnTertiaryDark,
    tertiaryContainer = AcademicTertiaryContainerDark,
    onTertiaryContainer = AcademicOnTertiaryContainerDark,
    background = AcademicBackgroundDark,
    onBackground = AcademicOnBackgroundDark,
    surface = AcademicSurfaceDark,
    onSurface = AcademicOnSurfaceDark,
    surfaceVariant = AcademicSurfaceVariantDark,
    onSurfaceVariant = AcademicOnSurfaceVariantDark,
    outline = AcademicOutlineDark,
    outlineVariant = AcademicOutlineVariantDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AcademicPrimaryLight,
    onPrimary = AcademicOnPrimaryLight,
    primaryContainer = AcademicPrimaryContainerLight,
    onPrimaryContainer = AcademicOnPrimaryContainerLight,
    secondary = AcademicSecondaryLight,
    onSecondary = AcademicOnSecondaryLight,
    secondaryContainer = AcademicSecondaryContainerLight,
    onSecondaryContainer = AcademicOnSecondaryContainerLight,
    tertiary = AcademicTertiaryLight,
    onTertiary = AcademicOnTertiaryLight,
    tertiaryContainer = AcademicTertiaryContainerLight,
    onTertiaryContainer = AcademicOnTertiaryContainerLight,
    background = AcademicBackgroundLight,
    onBackground = AcademicOnBackgroundLight,
    surface = AcademicSurfaceLight,
    onSurface = AcademicOnSurfaceLight,
    surfaceVariant = AcademicSurfaceVariantLight,
    onSurfaceVariant = AcademicOnSurfaceVariantLight,
    outline = AcademicOutlineLight,
    outlineVariant = AcademicOutlineVariantLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Use custom themed colors by default for strong branding
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

