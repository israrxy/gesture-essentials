package com.israrxy.gestureessentials.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.domain.summary
import com.israrxy.gestureessentials.ui.catalog.ActionCatalog
import com.israrxy.gestureessentials.ui.components.ScreenHeader
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onPresetToggle: (ShortcutPreset, Boolean) -> Unit,
    onCustomRuleToggle: (String, Boolean) -> Unit,
    onNewRule: () -> Unit,
    onEditRule: (String) -> Unit,
    onMoreSettings: () -> Unit,
    onShakeSensitivityChange: (Int) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenHeader(title = "Gesture Essentials", onBack = onBack)
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                if (state.isLoading) {
                    item("loading") { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                }
                if (!state.isLoading && !state.serviceEnabled) {
                    item("service") {
                        PreferenceAction(
                            title = "Enable accessibility service",
                            subtitle = "Enable Gesture Essentials to run your shortcuts",
                            onClick = onOpenAccessibility,
                        )
                    }
                }
                state.message?.let { message ->
                    item("message") {
                        SettingsMessage(message, state.messageIsError, onDismissMessage)
                    }
                }
                item("presets_heading") { PreferenceHeading("Common shortcuts") }
                items(state.presets, key = { it.preset.ruleId }) { row ->
                    PreferenceToggle(
                        title = row.preset.title,
                        subtitle = row.unavailableReason ?: row.preset.description,
                        checked = row.enabled,
                        enabled = state.canChange && (row.enabled || row.unavailableReason == null),
                        onCheckedChange = { onPresetToggle(row.preset, it) },
                    )
                }
                item("custom_heading") {
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                    PreferenceHeading("Custom rules")
                }
                if (!state.isLoading && state.customRules.isEmpty()) {
                    item("empty") {
                        Text(
                            "Create a gesture with your own actions.",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(state.customRules, key = { it.rule.id }) { row ->
                    CustomRulePreference(
                        state = row,
                        canChange = state.canChange,
                        onEdit = { onEditRule(row.rule.id) },
                        onToggle = { onCustomRuleToggle(row.rule.id, it) },
                    )
                }
                item("new_rule") {
                    TextButton(
                        onClick = onNewRule,
                        enabled = state.canChange,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    ) { Text("Add custom rule") }
                }
                item("preferences_heading") {
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                    PreferenceHeading("Preferences")
                }
                item("sensitivity") {
                    ShakeSensitivityPreference(
                        value = state.settings.shakeSensitivity,
                        enabled = state.canChange,
                        onChange = onShakeSensitivityChange,
                    )
                }
                item("haptics") {
                    PreferenceToggle(
                        title = "Haptic feedback",
                        subtitle = "A short vibration when a rule runs",
                        checked = state.settings.hapticsEnabled,
                        onCheckedChange = onHapticsChange,
                        enabled = state.canChange,
                    )
                }
                item("more") {
                    PreferenceAction(
                        title = "More settings",
                        subtitle = "Appearance, cooldown, permissions and about",
                        onClick = onMoreSettings,
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomRulePreference(
    state: CustomRuleUiState,
    canChange: Boolean,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rule = state.rule
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 72.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PreferenceText(
            title = rule.name,
            subtitle = if (!rule.enabled && state.ownerName != null) {
                "Inactive — “${state.ownerName}” uses this gesture"
            } else {
                "${rule.trigger.summary()} → ${rule.actions.joinToString(" → ", transform = ActionCatalog::summary)}"
            },
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onEdit) { Text("Edit") }
        Switch(
            checked = rule.enabled,
            onCheckedChange = onToggle,
            enabled = canChange,
            modifier = Modifier.semantics { contentDescription = "Enable ${rule.name}" },
        )
    }
}

@Composable
private fun ShakeSensitivityPreference(
    value: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by rememberSaveable(value) { mutableFloatStateOf(value.toFloat()) }
    val label = when {
        draft <= 35 -> "Low"
        draft <= 70 -> "Medium"
        else -> "High"
    }
    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Shake sensitivity", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Text("$label · ${draft.roundToInt()}%", style = MaterialTheme.typography.labelLarge)
        }
        Slider(
            value = draft,
            onValueChange = { draft = it },
            onValueChangeFinished = { onChange(draft.roundToInt()) },
            valueRange = 0f..100f,
            steps = 19,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Shake sensitivity" },
        )
        Text(
            "Lower values need firmer, deliberate shakes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
