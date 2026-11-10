package com.israrxy.gestureessentials.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Semantic seed palette. Components must only use MaterialTheme.colorScheme
// tokens, never these raw values.
private val Accent = Color(0xFF6750A4)
private val AccentContainer = Color(0xFFE9DDFF)
private val AccentSoft = Color(0xFFCFBCFF)

internal val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AccentContainer,
    onPrimaryContainer = Color(0xFF22005D),
    secondary = Color(0xFF625B71),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1E192B),
    tertiary = Color(0xFF7D5260),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454E),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
)

internal val DarkColors = darkColorScheme(
    primary = AccentSoft,
    onPrimary = Color(0xFF3A1E7A),
    primaryContainer = Color(0xFF4E3585),
    onPrimaryContainer = AccentContainer,
    secondary = Color(0xFFCCC2DC),
    secondaryContainer = Color(0xFF3F3A4A),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    // Near-black background for AMOLED panels.
    background = Color(0xFF08060C),
    onBackground = Color(0xFFE6E1E9),
    surface = Color(0xFF08060C),
    onSurface = Color(0xFFE6E1E9),
    surfaceVariant = Color(0xFF2B2831),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF948F99),
    outlineVariant = Color(0xFF3B3742),
)
