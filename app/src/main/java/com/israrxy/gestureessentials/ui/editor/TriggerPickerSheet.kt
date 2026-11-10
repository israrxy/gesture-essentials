package com.israrxy.gestureessentials.ui.editor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.ui.catalog.TriggerCatalog
import com.israrxy.gestureessentials.ui.components.ListRow
import com.israrxy.gestureessentials.ui.components.SheetHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriggerPickerSheet(
    onPick: (Trigger) -> Unit,
    onDismiss: () -> Unit,
) {
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
                    title = "When should this run?",
                    subtitle = "Pick the gesture or event that starts the rule.",
                )
            }
            items(items = TriggerCatalog.entries, key = { entry -> entry.type.key }) { entry ->
                ListRow(
                    title = entry.label,
                    subtitle = entry.description,
                    onClick = { onPick(entry.create()) },
                )
            }
        }
    }
}
