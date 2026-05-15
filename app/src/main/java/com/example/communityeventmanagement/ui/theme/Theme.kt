package com.example.communityeventmanagement.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
    outlineVariant = OutlineDark.copy(alpha = 0.2f),
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryModern,
    onPrimary = Color.White,
    primaryContainer = PrimaryModern.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryModern,

    secondary = SecondaryModern,
    onSecondary = Color.White,
    secondaryContainer = SecondaryModern.copy(alpha = 0.1f),

    background = Color(0xFFF8FAFC), // Slate 50
    onBackground = BackgroundDark,

    surface = Color.White,
    onSurface = BackgroundDark,
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    onSurfaceVariant = Color(0xFF475569), // Slate 500

    outline = Color(0xFF94A3B8), // Slate 400
    outlineVariant = Color(0xFFE2E8F0), // Slate 200
)

@Composable
fun CommunityEventManagementTheme(
    themeMode: String = "AUTO",
    darkTheme: Boolean = when(themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    },
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
