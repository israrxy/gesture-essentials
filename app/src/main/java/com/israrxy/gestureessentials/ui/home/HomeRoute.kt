package com.israrxy.gestureessentials.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.israrxy.gestureessentials.AppGraph

@Composable
fun HomeRoute(
    graph: AppGraph,
    onOpenRules: () -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(factory = graph.homeViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                HomeAction.OpenRules -> onOpenRules()
                HomeAction.EnableService -> viewModel.onAction(action)
            }
        },
    )
}
