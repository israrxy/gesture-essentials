package com.israrxy.gestureessentials.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.ui.components.ScreenHeader
import com.israrxy.gestureessentials.ui.components.SectionCard
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenDndAccess: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onShakeSensitivityChange: (Int) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onDefaultCooldownChange: (Long) -> Unit,
    onThemeModeChange: (AppSettings.ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ScreenHeader(
                title = "Settings",
                subtitle = "Stored on this device",
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PermissionsSection(
                    state = state,
                    onOpenAccessibility = onOpenAccessibility,
                    onOpenDndAccess = onOpenDndAccess,
                    onOpenBatterySettings = onOpenBatterySettings,
                )
                GesturesSection(
                    state = state,
                    onShakeSensitivityChange = onShakeSensitivityChange,
                    onHapticsChange = onHapticsChange,
                    onDefaultCooldownChange = onDefaultCooldownChange,
                )
                AppearanceSection(
                    state = state,
                    onThemeModeChange = onThemeModeChange,
                    onDynamicColorChange = onDynamicColorChange,
                )
                AboutSection(versionName = state.versionName)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PermissionsSection(
    state: SettingsUiState,
    onOpenAccessibility: () -> Unit,
    onOpenDndAccess: () -> Unit,
    onOpenBatterySettings: () -> Unit,
) {
    SectionCard(title = "Permissions") {
        StatusRow(
            title = "Accessibility service",
            detail = if (state.serviceEnabled) {
                "On — gestures are live"
            } else {
                "Off — no gesture can fire"
            },
            ok = state.serviceEnabled,
            actionLabel = if (state.serviceEnabled) "Manage" else "Enable",
            onAction = onOpenAccessibility,
        )
        HorizontalDivider()
        StatusRow(
            title = "Do Not Disturb access",
            detail = if (state.dndAccess) {
                "Granted — DND actions work"
            } else {
                "Not granted — DND actions do nothing"
            },
            ok = state.dndAccess,
            actionLabel = if (state.dndAccess) "Manage" else "Grant",
            onAction = onOpenDndAccess,
        )
        HorizontalDivider()
        StatusRow(
            title = "Battery optimization",
            detail = if (state.batteryUnrestricted) {
                "Unrestricted — the service can stay alive"
            } else {
                "Optimized — Android may stop the service"
            },
            ok = state.batteryUnrestricted,
            actionLabel = if (state.batteryUnrestricted) "Manage" else "Fix",
            onAction = onOpenBatterySettings,
        )
        Text(
            text = "Android does not let apps grant these to themselves. Each button opens the matching system page; that page is the only place the switch exists.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GesturesSection(
    state: SettingsUiState,
    onShakeSensitivityChange: (Int) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onDefaultCooldownChange: (Long) -> Unit,
) {
    SectionCard(title = "Gestures") {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Shake sensitivity",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${state.settings.shakeSensitivity}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Slider(
                value = state.settings.shakeSensitivity.toFloat(),
                onValueChange = { onShakeSensitivityChange(it.roundToInt()) },
                valueRange = 0f..100f,
                steps = 19,
                modifier = Modifier.fillMaxWidth(),
            )
            Row {
                Text(
                    text = "Firm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Light",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "Higher values trigger on lighter shakes. Applies to every shake rule.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider()

        ToggleRow(
            title = "Haptic feedback",
            subtitle = "Vibrate briefly when a rule runs",
            checked = state.settings.hapticsEnabled,
            onCheckedChange = onHapticsChange,
        )

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Default cooldown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "New rules wait this long before they can fire again. Existing rules keep their own value.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                COOLDOWN_OPTIONS.forEach { (ms, label) ->
                    FilterChip(
                        selected = state.settings.defaultCooldownMs == ms,
                        onClick = { onDefaultCooldownChange(ms) },
                        label = { Text(label) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    state: SettingsUiState,
    onThemeModeChange: (AppSettings.ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
) {
    val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    SectionCard(title = "Appearance") {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppSettings.ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.settings.themeMode == mode,
                        onClick = { onThemeModeChange(mode) },
                        label = { Text(mode.label()) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        HorizontalDivider()

        ToggleRow(
            title = "Dynamic color",
            subtitle = if (dynamicSupported) {
                "Use colors from your wallpaper"
            } else {
                "Needs Android 12 or newer"
            },
            checked = dynamicSupported && state.settings.dynamicColor,
            enabled = dynamicSupported,
            onCheckedChange = onDynamicColorChange,
        )
    }
}

@Composable
private fun AboutSection(versionName: String) {
    SectionCard(title = "About") {
        Text(
            text = "Gesture Essentials $versionName",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Rules and settings stay on this device. The app has no internet permission and never reads screen content.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Volume keys are never intercepted — they stay system sound controls.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatusRow(
    title: String,
    detail: String,
    ok: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = if (ok) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
        TextButton(onClick = onAction) {
            Text(actionLabel)
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
        )
    }
}

private fun AppSettings.ThemeMode.label(): String = when (this) {
    AppSettings.ThemeMode.SYSTEM -> "System"
    AppSettings.ThemeMode.LIGHT -> "Light"
    AppSettings.ThemeMode.DARK -> "Dark"
}

private val COOLDOWN_OPTIONS = listOf(
    500L to "0.5 s",
    1_000L to "1 s",
    2_000L to "2 s",
    5_000L to "5 s",
)
