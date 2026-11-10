package com.israrxy.gestureessentials.ui.rules

import com.israrxy.gestureessentials.core.model.Rule

data class RulesUiState(
    val rules: List<Rule> = emptyList(),
    val isLoading: Boolean = true,
)
