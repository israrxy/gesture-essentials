package com.israrxy.gestureessentials.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.israrxy.gestureessentials.AppGraph

@Composable
fun EditorRoute(
    graph: AppGraph,
    ruleId: String?,
    onBack: () -> Unit,
) {
    val factory = remember(ruleId) { graph.editorViewModelFactory(ruleId) }
    val viewModel: EditorViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    EditorScreen(
        state = state,
        onBack = onBack,
        onNameChange = viewModel::onNameChange,
        onTriggerChange = viewModel::onTriggerChange,
        onAddAction = viewModel::onAddAction,
        onRemoveAction = viewModel::onRemoveAction,
        onSave = viewModel::onSave,
    )
}
