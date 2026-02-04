package com.example.datausage.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Clean Sky Blue Light Theme
private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = SkyBlueDark,
    secondary = SkyBlueDark,
    onSecondary = Color.White,
    background = WhiteBackground,
    onBackground = TextBlack,
    surface = OffWhiteSurface,
    onSurface = TextBlack,
    error = ErrorRed
)

// Dark Theme (Dark Blue background to keep "Sky/Space" feel, as "White" in dark mode is blinding)
// But to strictly follow "Tone dominan putih dan biru langit", we might prioritize Light mode.
// We will make Dark mode a "Night Sky" version.
private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueLight,
    onPrimary = SkyBlueDark,
    primaryContainer = SkyBlueDark,
    onPrimaryContainer = SkyBlueLight,
    background = Color(0xFF102027), // Dark Blue Grey
    onBackground = Color(0xFFE1E2E1),
    surface = Color(0xFF1C2830),
    onSurface = Color(0xFFE1E2E1)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamic color to force our Sky Blue branding
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // White text on Blue status bar
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
