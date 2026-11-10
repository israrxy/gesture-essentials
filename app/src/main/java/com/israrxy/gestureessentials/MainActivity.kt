package com.israrxy.gestureessentials

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.israrxy.gestureessentials.ui.navigation.AppNavHost
import com.israrxy.gestureessentials.ui.theme.GestureEssentialsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val graph = (application as GestureEssentialsApp).graph

        setContent {
            GestureEssentialsTheme {
                AppNavHost(graph = graph)
            }
        }
    }
}
