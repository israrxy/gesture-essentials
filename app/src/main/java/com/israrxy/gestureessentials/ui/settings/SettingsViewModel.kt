package com.israrxy.gestureessentials.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.data.system.ServiceStatusRepository
import com.israrxy.gestureessentials.data.system.SystemAccessRepository
import com.israrxy.gestureessentials.domain.usecase.ObserveSettingsUseCase
import com.israrxy.gestureessentials.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    appInfo: AppInfo,
    private val observeSettings: ObserveSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val serviceStatus: ServiceStatusRepository,
    private val systemAccess: SystemAccessRepository,
) : ViewModel() {

    private val serviceEnabled = MutableStateFlow(serviceStatus.isEnabled())
    private val dndAccess = MutableStateFlow(systemAccess.hasDndAccess())
    private val batteryUnrestricted = MutableStateFlow(systemAccess.isIgnoringBatteryOptimizations())

    val uiState: StateFlow<SettingsUiState> = combine(
        observeSettings(),
        serviceEnabled,
        dndAccess,
        batteryUnrestricted,
    ) { settings, service, dnd, battery ->
        SettingsUiState(
            settings = settings,
            serviceEnabled = service,
            dndAccess = dnd,
            batteryUnrestricted = battery,
            versionName = appInfo.versionName,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(
            serviceEnabled = serviceEnabled.value,
            dndAccess = dndAccess.value,
            batteryUnrestricted = batteryUnrestricted.value,
            versionName = appInfo.versionName,
        ),
    )

    /** Called on ON_RESUME: the user may have just returned from a system page. */
    fun refreshStatus() {
        serviceEnabled.value = serviceStatus.isEnabled()
        dndAccess.value = systemAccess.hasDndAccess()
        batteryUnrestricted.value = systemAccess.isIgnoringBatteryOptimizations()
    }

    fun onThemeModeChange(mode: AppSettings.ThemeMode) = update { it.copy(themeMode = mode) }

    fun onDynamicColorChange(enabled: Boolean) = update { it.copy(dynamicColor = enabled) }

    fun onShakeSensitivityChange(value: Int) =
        update { it.copy(shakeSensitivity = value.coerceIn(0, 100)) }

    fun onHapticsChange(enabled: Boolean) = update { it.copy(hapticsEnabled = enabled) }

    fun onDefaultCooldownChange(ms: Long) =
        update { it.copy(defaultCooldownMs = ms.coerceIn(0, 60_000)) }

    private fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch { updateSettings(transform) }
    }
}
