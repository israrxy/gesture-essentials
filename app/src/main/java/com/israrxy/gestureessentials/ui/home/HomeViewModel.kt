package com.israrxy.gestureessentials.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.data.system.ServiceStatusRepository
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    appInfo: AppInfo,
    observeRules: ObserveRulesUseCase,
    private val serviceStatus: ServiceStatusRepository,
) : ViewModel() {

    private val serviceEnabled = MutableStateFlow(serviceStatus.isEnabled())

    val uiState: StateFlow<HomeUiState> = combine(observeRules(), serviceEnabled) { rules, enabled ->
        HomeUiState(
            versionName = appInfo.versionName,
            serviceEnabled = enabled,
            ruleCount = rules.size,
            activeRuleCount = rules.count { it.enabled },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(
            versionName = appInfo.versionName,
            serviceEnabled = serviceEnabled.value,
        ),
    )

    /** Called from the route on ON_RESUME: the user may have just toggled it. */
    fun refreshServiceStatus() {
        serviceEnabled.value = serviceStatus.isEnabled()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.EnableService -> refreshServiceStatus()
            HomeAction.OpenRules -> Unit
        }
    }
}
