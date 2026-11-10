package com.israrxy.gestureessentials.ui.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.domain.usecase.DeleteRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import com.israrxy.gestureessentials.domain.usecase.SetRuleEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RulesViewModel(
    observeRules: ObserveRulesUseCase,
    private val setRuleEnabled: SetRuleEnabledUseCase,
    private val deleteRule: DeleteRuleUseCase,
) : ViewModel() {

    val uiState: StateFlow<RulesUiState> = observeRules()
        .map { rules -> RulesUiState(rules = rules, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RulesUiState(),
        )

    fun onToggle(id: String, enabled: Boolean) {
        viewModelScope.launch { setRuleEnabled(id, enabled) }
    }

    fun onDelete(id: String) {
        viewModelScope.launch { deleteRule(id) }
    }
}
