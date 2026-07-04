package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VaultColorScheme = lightColorScheme(
    primary = TakaGold,
    secondary = CalmSage,
    tertiary = WarmRust,
    background = DeepVault,
    surface = VaultSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = VaultLine,
    surfaceVariant = VaultSurface2
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We strictly enforce the premium dark vault theme
    dynamicColor: Boolean = false, // Disable system dynamic color to preserve the specific, hand-crafted branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VaultColorScheme,
        typography = Typography,
        content = content
    )
}
