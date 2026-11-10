package com.israrxy.gestureessentials.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.ui.catalog.ActionCatalog
import com.israrxy.gestureessentials.ui.catalog.TriggerCatalog
import com.israrxy.gestureessentials.ui.components.ScreenHeader
import com.israrxy.gestureessentials.ui.components.SectionCard

@Composable
fun EditorScreen(
    state: EditorUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onTriggerChange: (Trigger) -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onAddAction: (ActionSpec) -> Unit,
    onRemoveAction: (Int) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTriggerPicker by rememberSaveable { mutableStateOf(false) }
    var showActionPicker by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
        ) {
            ScreenHeader(
                title = if (state.isNew) "New rule" else "Edit rule",
                onBack = onBack,
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    state.error?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        label = { Text("Rule name") },
                        placeholder = { Text("Optional — named after the trigger") },
                        singleLine = true,
                        enabled = !state.isSaving && !state.isMissing,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().toggleable(
                            value = state.enabled,
                            role = Role.Switch,
                            enabled = !state.isSaving && !state.isMissing,
                            onValueChange = onEnabledChange,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Enable rule", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = state.enabled,
                            onCheckedChange = null,
                            enabled = !state.isSaving && !state.isMissing,
                        )
                    }

                    SectionCard(title = "When") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = TriggerCatalog.entry(state.trigger.type).label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = TriggerCatalog.summary(state.trigger),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            TextButton(onClick = { showTriggerPicker = true }) {
                                Text("Change")
                            }
                        }
                        TriggerOptions(trigger = state.trigger, onChange = onTriggerChange)
                    }

                    SectionCard(title = "Then") {
                        if (state.actions.isEmpty()) {
                            Text(
                                text = "No actions yet. Add at least one step.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        state.actions.forEachIndexed { index, action ->
                            ActionRow(
                                index = index,
                                action = action,
                                onRemove = { onRemoveAction(index) },
                            )
                        }
                        TextButton(onClick = { showActionPicker = true }) {
                            Text("Add action")
                        }
                    }
                    if (!state.isNew && !state.isMissing) {
                        TextButton(onClick = onDelete, enabled = !state.isSaving) {
                            Text("Delete rule", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Button(
                    onClick = onSave,
                    enabled = state.canSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Text(if (state.isNew) "Save rule" else "Save changes")
                }
            }
        }
    }

    if (showTriggerPicker) {
        TriggerPickerSheet(
            onPick = { trigger ->
                onTriggerChange(trigger)
                showTriggerPicker = false
            },
            onDismiss = { showTriggerPicker = false },
        )
    }

    if (showActionPicker) {
        ActionPickerSheet(
            onPick = { action ->
                onAddAction(action)
                showActionPicker = false
            },
            onDismiss = { showActionPicker = false },
        )
    }
}

@Composable
private fun ActionRow(
    index: Int,
    action: ActionSpec,
    onRemove: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${index + 1}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 10.dp),
        )
        Text(
            text = ActionCatalog.summary(action),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove action",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
