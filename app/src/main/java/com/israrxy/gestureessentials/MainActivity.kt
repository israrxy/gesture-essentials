package com.israrxy.gestureessentials

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.ui.navigation.AppNavHost
import com.israrxy.gestureessentials.ui.theme.GestureEssentialsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val graph = (application as GestureEssentialsApp).graph

        setContent {
            val settings by graph.settingsState.collectAsStateWithLifecycle()
            val darkTheme = when (settings.themeMode) {
                AppSettings.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppSettings.ThemeMode.LIGHT -> false
                AppSettings.ThemeMode.DARK -> true
            }

            GestureEssentialsTheme(
                darkTheme = darkTheme,
                dynamicColor = settings.dynamicColor,
            ) {
                AppNavHost(graph = graph)
            }
        }
    }
}
