package com.example.communityeventmanagement.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryModern,
    onPrimary = BackgroundDark,
    primaryContainer = PrimaryModern.copy(alpha = 0.15f),
    onPrimaryContainer = PrimaryLight,
    
    secondary = SecondaryModern,
    onSecondary = BackgroundDark,
    secondaryContainer = SecondaryModern.copy(alpha = 0.15f),
    onSecondaryContainer = Color.White,
    
    tertiary = AccentModern,
    
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceDark.copy(alpha = 0.8f),
    
    outline = OutlineDark,
    outlineVariant = OutlineDark.copy(alpha = 0.2f)
)

@Composable
fun CommunityEventManagementTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
