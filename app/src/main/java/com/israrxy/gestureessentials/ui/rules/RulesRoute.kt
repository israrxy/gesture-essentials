package com.israrxy.gestureessentials.ui.rules

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.israrxy.gestureessentials.AppGraph

@Composable
fun RulesRoute(
    graph: AppGraph,
    onBack: () -> Unit,
    onNewRule: () -> Unit,
    onOpenRule: (String) -> Unit,
) {
    val viewModel: RulesViewModel = viewModel(factory = graph.rulesViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RulesScreen(
        state = state,
        onBack = onBack,
        onNewRule = onNewRule,
        onOpenRule = onOpenRule,
        onToggle = viewModel::onToggle,
        onDelete = viewModel::onDelete,
    )
}
