package com.israrxy.gestureessentials.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.israrxy.gestureessentials.AppGraph

@Composable
fun SettingsRoute(
    graph: AppGraph,
    onBack: () -> Unit,
    onNewRule: () -> Unit,
    onEditRule: (String) -> Unit,
    onMoreSettings: () -> Unit,
    showMore: Boolean = false,
) {
    val viewModel: SettingsViewModel = viewModel(factory = graph.settingsViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Coming back from a system page must refresh the permission rows.
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun openSystemSettings(action: String) {
        try {
            context.startActivity(Intent(action))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "This system settings page isn't available on this device.", Toast.LENGTH_LONG).show()
        }
    }

    if (showMore) {
        MoreSettingsScreen(
            state = state,
            onBack = onBack,
            onOpenAccessibility = { openSystemSettings(Settings.ACTION_ACCESSIBILITY_SETTINGS) },
            onOpenDndAccess = { openSystemSettings(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) },
            onOpenBatterySettings = { openSystemSettings(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS) },
            onDefaultCooldownChange = viewModel::onDefaultCooldownChange,
            onThemeModeChange = viewModel::onThemeModeChange,
            onDynamicColorChange = viewModel::onDynamicColorChange,
            onDismissMessage = viewModel::dismissMessage,
        )
    } else {
        SettingsScreen(
            state = state,
            onBack = onBack,
            onOpenAccessibility = { openSystemSettings(Settings.ACTION_ACCESSIBILITY_SETTINGS) },
            onPresetToggle = viewModel::onPresetToggle,
            onCustomRuleToggle = viewModel::onCustomRuleToggle,
            onNewRule = onNewRule,
            onEditRule = onEditRule,
            onMoreSettings = onMoreSettings,
            onShakeSensitivityChange = viewModel::onShakeSensitivityChange,
            onHapticsChange = viewModel::onHapticsChange,
            onDismissMessage = viewModel::dismissMessage,
        )
    }
}
