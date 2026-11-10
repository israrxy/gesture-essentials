package com.israrxy.gestureessentials.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.Trigger

/** Parameters for the working triggers exposed by the custom-rule picker. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TriggerOptions(trigger: Trigger, onChange: (Trigger) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        when (trigger) {
            is Trigger.Shake -> listOf(2, 3).forEach { count ->
                FilterChip(
                    selected = trigger.count == count,
                    onClick = { onChange(trigger.copy(count = count)) },
                    label = { Text("$count shakes") },
                )
            }
            is Trigger.Screen -> Trigger.ScreenEvent.entries.forEach { event ->
                FilterChip(
                    selected = trigger.state == event,
                    onClick = { onChange(trigger.copy(state = event)) },
                    label = { Text(event.key.replaceFirstChar { it.uppercase() }) },
                )
            }
            is Trigger.Charger -> ConnectionOptions(trigger.plugged) { onChange(trigger.copy(plugged = it)) }
            is Trigger.Headset -> ConnectionOptions(trigger.plugged) { onChange(trigger.copy(plugged = it)) }
            is Trigger.AppForeground -> OutlinedTextField(
                value = trigger.packageName,
                onValueChange = { onChange(trigger.copy(packageName = it.trim())) },
                label = { Text("App package name") },
                placeholder = { Text("com.example.app") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            else -> Unit
        }
    }
}

@Composable
private fun ConnectionOptions(plugged: Boolean, onChange: (Boolean) -> Unit) {
    listOf(true, false).forEach { connected ->
        FilterChip(
            selected = plugged == connected,
            onClick = { onChange(connected) },
            label = { Text(if (connected) "Connected" else "Disconnected") },
        )
    }
}
