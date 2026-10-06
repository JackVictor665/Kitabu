package com.example.kitabu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DeepNavy,
    secondary = MutedSage,
    tertiary = VibrantCoral,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite,
    error = RoseRed
)

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    secondary = MutedSage,
    tertiary = VibrantCoral,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = DeepNavy,
    onSurface = DeepNavy,
    error = RoseRed
)

@Composable
fun KitabuTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
