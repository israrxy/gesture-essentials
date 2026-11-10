package com.israrxy.gestureessentials.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Constraints
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.domain.summary
import com.israrxy.gestureessentials.domain.usecase.GetRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.SaveRuleUseCase
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditorViewModel(
    private val ruleId: String?,
    private val getRule: GetRuleUseCase,
    private val saveRule: SaveRuleUseCase,
) : ViewModel() {

    /** Fields the editor must preserve but does not edit yet (M4). */
    private var original: Rule? = null

    private val _uiState = MutableStateFlow(
        EditorUiState(isNew = ruleId == null, isLoading = ruleId != null),
    )
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        if (ruleId != null) {
            viewModelScope.launch {
                val rule = getRule(ruleId)
                if (rule == null) {
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    original = rule
                    _uiState.value = EditorUiState(
                        ruleId = rule.id,
                        name = rule.name,
                        trigger = rule.trigger,
                        actions = rule.actions,
                        isLoading = false,
                        isNew = false,
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }

    fun onTriggerChange(trigger: Trigger) = _uiState.update { it.copy(trigger = trigger) }

    fun onAddAction(action: ActionSpec) = _uiState.update { it.copy(actions = it.actions + action) }

    fun onRemoveAction(index: Int) = _uiState.update { state ->
        state.copy(actions = state.actions.filterIndexed { i, _ -> i != index })
    }

    fun onSave() {
        val state = _uiState.value
        if (!state.canSave) return

        val existing = original
        val rule = Rule(
            id = existing?.id ?: UUID.randomUUID().toString(),
            name = state.name.trim().ifBlank { state.trigger.summary() },
            trigger = state.trigger,
            actions = state.actions,
            enabled = existing?.enabled ?: true,
            constraints = existing?.constraints ?: Constraints(),
            cooldownMs = existing?.cooldownMs ?: 1_000L,
            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
        )

        viewModelScope.launch {
            saveRule(rule)
            _uiState.update { it.copy(saved = true) }
        }
    }
}
