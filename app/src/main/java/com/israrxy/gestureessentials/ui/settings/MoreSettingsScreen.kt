package com.israrxy.gestureessentials.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.ui.components.ScreenHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoreSettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenDndAccess: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onDefaultCooldownChange: (Long) -> Unit,
    onThemeModeChange: (AppSettings.ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Scaffold(modifier = modifier) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenHeader(title = "More settings", onBack = onBack)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
                state.message?.let { SettingsMessage(it, onDismissMessage) }
                PreferenceHeading("Appearance")
                Text("Theme", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.bodyLarge)
                FlowRow(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppSettings.ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = state.settings.themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            enabled = state.canChange,
                            label = { Text(mode.label()) },
                        )
                    }
                }
                PreferenceToggle(
                    title = "Wallpaper colors",
                    subtitle = if (dynamicSupported) "Use your system's dynamic colors" else "Requires Android 12 or newer",
                    checked = dynamicSupported && state.settings.dynamicColor,
                    enabled = dynamicSupported && state.canChange,
                    onCheckedChange = onDynamicColorChange,
                )
                HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                PreferenceHeading("Rule defaults")
                PreferenceText(
                    title = "Default cooldown",
                    subtitle = "Time between runs of a new rule. Existing rules keep their saved cooldown.",
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                FlowRow(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    COOLDOWNS.forEach { (ms, label) ->
                        FilterChip(
                            selected = state.settings.defaultCooldownMs == ms,
                            onClick = { onDefaultCooldownChange(ms) },
                            enabled = state.canChange,
                            label = { Text(label) },
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                PreferenceHeading("Permissions")
                PreferenceAction(
                    title = "Accessibility service",
                    subtitle = if (state.serviceEnabled) "Enabled in Android Settings" else "Off — tap to enable",
                    onClick = onOpenAccessibility,
                )
                PreferenceAction(
                    title = "Do Not Disturb access",
                    subtitle = if (state.dndAccess) "Granted" else "Needed for Do Not Disturb actions",
                    onClick = onOpenDndAccess,
                )
                PreferenceAction(
                    title = "Battery optimization",
                    subtitle = if (state.batteryUnrestricted) "Exempt from Android battery optimization"
                    else "Using Android battery optimization",
                    onClick = onOpenBatterySettings,
                )
                HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                PreferenceHeading("About")
                PreferenceText(
                    title = "Gesture Essentials ${state.versionName}",
                    subtitle = "Rules and preferences are stored on this device. Screenshots are saved to Pictures/GestureEssentials.",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
        }
    }
}

private fun AppSettings.ThemeMode.label(): String = when (this) {
    AppSettings.ThemeMode.SYSTEM -> "System"
    AppSettings.ThemeMode.LIGHT -> "Light"
    AppSettings.ThemeMode.DARK -> "Dark"
}

private val COOLDOWNS = listOf(500L to "0.5 s", 1_000L to "1 s", 2_000L to "2 s", 5_000L to "5 s")
