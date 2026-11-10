package com.israrxy.gestureessentials.ui.home

import androidx.lifecycle.ViewModel
import com.israrxy.gestureessentials.core.model.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(appInfo: AppInfo) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(versionName = appInfo.versionName))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onAction(action: HomeAction) {
        // Wired to the runtime and rule store in M1/M2.
        when (action) {
            HomeAction.EnableService, HomeAction.OpenRules -> Unit
        }
    }
}
