package com.israrxy.gestureessentials.ui.editor

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Trigger

data class EditorUiState(
    val ruleId: String? = null,
    val name: String = "",
    val trigger: Trigger = Trigger.Shake(),
    val actions: List<ActionSpec> = emptyList(),
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isMissing: Boolean = false,
    val error: String? = null,
    val isNew: Boolean = true,
    val saved: Boolean = false,
) {
    /** A rule needs at least one action; the name is optional (auto-derived). */
    val canSave: Boolean get() = !isLoading && !isSaving && !isMissing && !saved &&
        actions.isNotEmpty() && (trigger !is Trigger.AppForeground || trigger.packageName.isNotBlank())
}
