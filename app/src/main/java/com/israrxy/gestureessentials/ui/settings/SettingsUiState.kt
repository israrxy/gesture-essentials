package com.israrxy.gestureessentials.ui.settings

import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.ShortcutPreset

/** Everything the Settings screen renders. Produced by [SettingsViewModel]. */
data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val presets: List<PresetUiState> = emptyList(),
    val customRules: List<CustomRuleUiState> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val message: String? = null,
    val serviceEnabled: Boolean = false,
    val dndAccess: Boolean = false,
    val batteryUnrestricted: Boolean = false,
    val versionName: String = "",
) {
    val canChange: Boolean get() = !isLoading && !isSaving
}

data class PresetUiState(
    val preset: ShortcutPreset,
    val enabled: Boolean,
    val unavailableReason: String? = null,
)

data class CustomRuleUiState(
    val rule: Rule,
    val conflictName: String? = null,
)
