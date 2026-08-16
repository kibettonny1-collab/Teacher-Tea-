package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = SurfaceCard,
    primaryContainer = RoyalBlueLight,
    onPrimaryContainer = NavyPrimary,
    secondary = RoyalBlue,
    onSecondary = SurfaceCard,
    secondaryContainer = RoyalBlueLight,
    onSecondaryContainer = NavyPrimary,
    tertiary = ThaiGold,
    onTertiary = NavyDark,
    tertiaryContainer = ThaiGoldContainer,
    onTertiaryContainer = NavyDark,
    background = BackgroundLight,
    onBackground = TextInk,
    surface = SurfaceCard,
    onSurface = TextInk,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextMuted,
    outline = BorderLine,
    error = CoralRed,
    onError = SurfaceCard,
    errorContainer = CoralRedLight,
    onErrorContainer = CoralRed
)

private val DarkColorScheme = darkColorScheme(
    primary = ThaiGold,
    onPrimary = NavyDark,
    primaryContainer = NavySecondary,
    onPrimaryContainer = ThaiGoldLight,
    secondary = RoyalBlue,
    onSecondary = SurfaceCard,
    secondaryContainer = DarkCard,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = ThaiGoldLight,
    onTertiary = NavyDark,
    tertiaryContainer = DarkCard,
    onTertiaryContainer = ThaiGoldLight,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = CoralRed,
    onError = SurfaceCard,
    errorContainer = DarkCard,
    onErrorContainer = CoralRed
)

@Composable
fun ClassCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
