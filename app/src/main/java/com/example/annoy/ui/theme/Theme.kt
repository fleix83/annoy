package com.example.annoy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GrayscaleColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnPrimaryDark,
    surface = SurfaceDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    outline = OutlineDark,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = OnSurfaceDark,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = OnSurfaceDark
)

@Composable
fun ScreenBrakeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GrayscaleColorScheme,
        typography = AppTypography,
        content = content
    )
}
