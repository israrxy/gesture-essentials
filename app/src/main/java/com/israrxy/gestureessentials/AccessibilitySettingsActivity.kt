package com.israrxy.gestureessentials

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israrxy.gestureessentials.ui.navigation.AppNavHost
import com.israrxy.gestureessentials.ui.theme.GestureEssentialsTheme

/** Preferences entry advertised to Android Settings by the accessibility service. */
class AccessibilitySettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val graph = (application as GestureEssentialsApp).graph

        // Always show the same preferences, independent of the caller's intent.
        setContent {
            val settings by graph.settingsState.collectAsStateWithLifecycle()
            GestureEssentialsTheme(settings = settings) {
                AppNavHost(
                    graph = graph,
                    onFinish = { finish() },
                )
            }
        }
    }
}
