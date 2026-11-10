package com.israrxy.gestureessentials.ui.settings

import com.israrxy.gestureessentials.core.model.AppSettings

/** Everything the Settings screen renders. Produced by [SettingsViewModel]. */
data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val serviceEnabled: Boolean = false,
    val dndAccess: Boolean = false,
    val batteryUnrestricted: Boolean = false,
    val versionName: String = "",
)
