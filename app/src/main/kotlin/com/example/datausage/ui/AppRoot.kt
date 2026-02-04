package com.example.datausage.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.datausage.App
import com.example.datausage.ui.screens.home.HomeScreen
import com.example.datausage.ui.screens.settings.SettingsScreen

enum class Screen {
    HOME, SETTINGS
}

@Composable
fun AppRoot(
    app: App,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            app = app,
            onNavigateToSettings = { currentScreen = Screen.SETTINGS }
        )
        Screen.SETTINGS -> SettingsScreen(
            app = app,
            onBack = { currentScreen = Screen.HOME }
        )
    }
}
