package com.example.communityeventmanagement.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.communityeventmanagement.domain.model.ThemeMode

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ThemePreviews

private val DarkColorScheme = darkColorScheme(
    primary = Ube,
    onPrimary = Color.White,
    primaryContainer = AmericanBlue,
    onPrimaryContainer = CadetGrey,
    
    secondary = CadetGrey,
    onSecondary = Color.White,
    secondaryContainer = AmericanBlue,
    onSecondaryContainer = Color.White,
    
    tertiary = AccentModern,
    
    background = ChineseBlack,
    onBackground = CadetGrey,
    
    surface = AmericanBlue,
    onSurface = Color.White,
    surfaceVariant = AmericanBlue,
    onSurfaceVariant = CadetGrey,
    
    outline = CoolGrey,
    outlineVariant = CoolGrey,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryModern,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryModern,

    secondary = SecondaryModern,
    onSecondary = Color.White,
    secondaryContainer = PrimaryLight,

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondaryLight,

    outline = DividerLight,
    outlineVariant = DividerLight,
)

@Composable
fun CommunityEventManagementTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    darkTheme: Boolean = when(themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        else -> isSystemInDarkTheme()
    },
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}

