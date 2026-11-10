package com.israrxy.gestureessentials.ui.editor

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Trigger

data class EditorUiState(
    val ruleId: String? = null,
    val name: String = "",
    val trigger: Trigger = Trigger.Shake(),
    val actions: List<ActionSpec> = emptyList(),
    val isLoading: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
) {
    /** A rule needs at least one action; the name is optional (auto-derived). */
    val canSave: Boolean get() = !isLoading && !saved && actions.isNotEmpty()
}
