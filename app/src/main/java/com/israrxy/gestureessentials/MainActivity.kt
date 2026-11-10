package com.israrxy.gestureessentials

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.israrxy.gestureessentials.ui.home.HomeScreen
import com.israrxy.gestureessentials.ui.home.HomeViewModel
import com.israrxy.gestureessentials.ui.theme.GestureEssentialsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val graph = (application as GestureEssentialsApp).graph

        setContent {
            GestureEssentialsTheme {
                val homeViewModel: HomeViewModel = viewModel(factory = graph.homeViewModelFactory)
                val state by homeViewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(state = state, onAction = homeViewModel::onAction)
            }
        }
    }
}
