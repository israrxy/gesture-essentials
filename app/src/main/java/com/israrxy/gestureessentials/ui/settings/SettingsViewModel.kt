package com.israrxy.gestureessentials.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.data.system.ServiceStatusRepository
import com.israrxy.gestureessentials.data.system.SystemAccessRepository
import com.israrxy.gestureessentials.domain.RuleAssignments
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import com.israrxy.gestureessentials.domain.usecase.ObserveSettingsUseCase
import com.israrxy.gestureessentials.domain.usecase.SetPresetEnabledUseCase
import com.israrxy.gestureessentials.domain.usecase.SetRuleEnabledUseCase
import com.israrxy.gestureessentials.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    appInfo: AppInfo,
    observeSettings: ObserveSettingsUseCase,
    observeRules: ObserveRulesUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val setPresetEnabled: SetPresetEnabledUseCase,
    private val setRuleEnabled: SetRuleEnabledUseCase,
    private val serviceStatus: ServiceStatusRepository,
    private val systemAccess: SystemAccessRepository,
) : ViewModel() {

    private val access = MutableStateFlow(readAccess())
    private val edit = MutableStateFlow(PendingEdit())

    val uiState: StateFlow<SettingsUiState> = combine(
        observeSettings(), observeRules(), access, edit,
    ) { storedSettings, rules, status, pending ->
        val settings = pending.settings ?: storedSettings
        SettingsUiState(
            settings = settings,
            presets = ShortcutPreset.entries.map { preset ->
                val saved = rules.firstOrNull { it.id == preset.ruleId }
                val candidate = saved ?: preset.newRule(settings.defaultCooldownMs, 0)
                PresetUiState(
                    preset = preset,
                    enabled = if (pending.ruleId == preset.ruleId) pending.enabled else candidate.enabled,
                    unavailableReason = unavailableReason(preset, status),
                )
            },
            customRules = rules.filter { ShortcutPreset.fromRuleId(it.id) == null }.map { rule ->
                CustomRuleUiState(
                    rule = if (pending.ruleId == rule.id) rule.copy(enabled = pending.enabled) else rule,
                    ownerName = RuleAssignments.conflictFor(rule, rules)?.name,
                )
            },
            isLoading = false,
            isSaving = pending.isSaving,
            message = pending.message,
            messageIsError = pending.messageIsError,
            serviceEnabled = status.serviceEnabled,
            dndAccess = status.dndAccess,
            batteryUnrestricted = status.batteryUnrestricted,
            versionName = appInfo.versionName,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(versionName = appInfo.versionName),
    )

    fun refreshStatus() {
        access.value = readAccess()
    }

    fun onPresetToggle(preset: ShortcutPreset, enabled: Boolean) {
        val row = uiState.value.presets.firstOrNull { it.preset == preset } ?: return
        if (enabled && row.unavailableReason != null) return
        val cooldown = uiState.value.settings.defaultCooldownMs
        change(PendingEdit(ruleId = preset.ruleId, enabled = enabled)) {
            takenOver(setPresetEnabled(preset, enabled, cooldown))
        }
    }

    fun onCustomRuleToggle(id: String, enabled: Boolean) {
        change(PendingEdit(ruleId = id, enabled = enabled)) {
            takenOver(setRuleEnabled(id, enabled))
        }
    }

    fun onThemeModeChange(mode: AppSettings.ThemeMode) = update { it.copy(themeMode = mode) }

    fun onDynamicColorChange(enabled: Boolean) = update { it.copy(dynamicColor = enabled) }

    fun onShakeSensitivityChange(value: Int) =
        update { it.copy(shakeSensitivity = value.coerceIn(0, 100)) }

    fun onHapticsChange(enabled: Boolean) = update { it.copy(hapticsEnabled = enabled) }

    fun onDefaultCooldownChange(ms: Long) =
        update { it.copy(defaultCooldownMs = ms.coerceIn(0, 60_000)) }

    fun dismissMessage() {
        edit.value = edit.value.copy(message = null)
    }

    private fun update(transform: (AppSettings) -> AppSettings) {
        change(PendingEdit(settings = transform(uiState.value.settings))) {
            updateSettings(transform)
            null
        }
    }

    /** A gesture has one owner; name whatever was switched off to make room. */
    private fun takenOver(disabledNames: List<String>): String? = when (disabledNames.size) {
        0 -> null
        1 -> "Turned off “${disabledNames.single()}” because only one rule can use that gesture."
        else -> "Turned off ${disabledNames.size} rules that used the same gesture."
    }

    private fun change(pending: PendingEdit, block: suspend () -> String?) {
        if (edit.value.isSaving || uiState.value.isLoading) return
        edit.value = pending.copy(isSaving = true)
        viewModelScope.launch {
            try {
                val message = block()
                edit.value = PendingEdit(message = message)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                edit.value = PendingEdit(
                    message = "Couldn't save the change. Please try again.",
                    messageIsError = true,
                )
            }
        }
    }

    private fun readAccess() = AccessState(
        serviceEnabled = serviceStatus.isEnabled(),
        dndAccess = systemAccess.hasDndAccess(),
        batteryUnrestricted = systemAccess.isIgnoringBatteryOptimizations(),
        hasShakeSensor = systemAccess.hasShakeSensor(),
        hasFlashlight = systemAccess.hasFlashlight(),
        supportsScreenshot = systemAccess.supportsScreenshot(),
    )

    private fun unavailableReason(preset: ShortcutPreset, access: AccessState): String? = when {
        preset.trigger is Trigger.Shake && !access.hasShakeSensor -> "No shake sensor on this device"
        preset == ShortcutPreset.SHAKE_SCREENSHOT && !access.supportsScreenshot ->
            "Requires Android 11 or newer"
        preset != ShortcutPreset.SHAKE_SCREENSHOT && !access.hasFlashlight ->
            "No flashlight on this device"
        else -> null
    }
}

private data class PendingEdit(
    val isSaving: Boolean = false,
    val ruleId: String? = null,
    val enabled: Boolean = false,
    val settings: AppSettings? = null,
    val message: String? = null,
    val messageIsError: Boolean = false,
)

private data class AccessState(
    val serviceEnabled: Boolean,
    val dndAccess: Boolean,
    val batteryUnrestricted: Boolean,
    val hasShakeSensor: Boolean,
    val hasFlashlight: Boolean,
    val supportsScreenshot: Boolean,
)
