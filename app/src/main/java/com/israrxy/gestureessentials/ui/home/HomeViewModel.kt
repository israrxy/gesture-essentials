package com.israrxy.gestureessentials.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    appInfo: AppInfo,
    observeRules: ObserveRulesUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = observeRules()
        .map { rules ->
            HomeUiState(
                versionName = appInfo.versionName,
                ruleCount = rules.size,
                activeRuleCount = rules.count { it.enabled },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(versionName = appInfo.versionName),
        )

    fun onAction(action: HomeAction) {
        // Service enablement is wired up in M2, when the accessibility
        // service and the trigger bus actually exist.
        when (action) {
            HomeAction.EnableService, HomeAction.OpenRules -> Unit
        }
    }
}
