package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TallyColorScheme = lightColorScheme(
    primary = TallyForestGreen,
    onPrimary = TallySurface,
    primaryContainer = TallyForestGreenLight,
    onPrimaryContainer = TallyForestGreen,
    secondary = TallyTerracotta,
    onSecondary = TallySurface,
    secondaryContainer = TallyTerracottaLight,
    onSecondaryContainer = TallyTerracottaText,
    background = TallyBg,
    onBackground = TallyTextPrimary,
    surface = TallySurface,
    onSurface = TallyTextPrimary,
    surfaceVariant = TallySurfaceVariant,
    onSurfaceVariant = TallyTextSecondary,
    outline = TallyBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TallyColorScheme,
        typography = Typography,
        content = content
    )
}
