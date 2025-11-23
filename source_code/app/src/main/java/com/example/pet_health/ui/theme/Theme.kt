package com.example.pet_health.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

// Màu sáng
private val LightColors = lightColorScheme(
    primary = PinkMedium,
    secondary = PinkSoft,
    background = PinkLight,
    surface = White,
    onPrimary = White,
    onSecondary = Black,
    onBackground = Black
)

// Màu tối (dark mode)
private val DarkColors = darkColorScheme(
    primary = PinkDark,
    secondary = PinkSoft,
    background = Black,
    surface = PinkDark,
    onPrimary = White,
    onSecondary = White,
    onBackground = White
)

// Giao diện chủ đề
@Composable
fun PetHealthTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}