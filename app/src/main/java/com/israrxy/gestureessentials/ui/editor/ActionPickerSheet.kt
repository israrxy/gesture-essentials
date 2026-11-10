package com.israrxy.gestureessentials.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.ui.catalog.ActionCatalog
import com.israrxy.gestureessentials.ui.catalog.ActionEntry
import com.israrxy.gestureessentials.ui.catalog.ParamKind
import com.israrxy.gestureessentials.ui.components.ListRow
import com.israrxy.gestureessentials.ui.components.SheetHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPickerSheet(
    onPick: (ActionSpec) -> Unit,
    onDismiss: () -> Unit,
) {
    var pending by remember { mutableStateOf<ActionEntry?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 520.dp),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            item {
                SheetHeader(
                    title = "What should it do?",
                    subtitle = "Steps run in the order you add them.",
                )
            }
            items(items = ActionCatalog.entries, key = { entry -> entry.type.key }) { entry ->
                ListRow(
                    title = entry.label,
                    subtitle = entry.description,
                    onClick = {
                        if (entry.paramKind == ParamKind.NONE) {
                            onPick(entry.create(""))
                        } else {
                            pending = entry
                        }
                    },
                )
            }
        }
    }

    val entry = pending
    if (entry != null) {
        ParamDialog(
            entry = entry,
            onDismiss = { pending = null },
            onConfirm = { value ->
                pending = null
                onPick(entry.create(value))
            },
        )
    }
}

@Composable
private fun ParamDialog(
    entry: ActionEntry,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(entry.label) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(entry.paramLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (entry.paramKind == ParamKind.NUMBER) {
                        KeyboardType.Number
                    } else {
                        KeyboardType.Uri
                    },
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(value.trim()) },
                enabled = value.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
