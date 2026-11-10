package com.israrxy.gestureessentials.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Constraints
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.domain.summary
import com.israrxy.gestureessentials.domain.RuleConflictException
import com.israrxy.gestureessentials.domain.usecase.DeleteRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.GetRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.SaveRuleUseCase
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditorViewModel(
    private val ruleId: String?,
    private val getRule: GetRuleUseCase,
    private val saveRule: SaveRuleUseCase,
    private val deleteRule: DeleteRuleUseCase,
    /** Global preference; existing rules keep their saved value. */
    private val defaultCooldownMs: () -> Long = { 1_000L },
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
                val rule = try {
                    getRule(ruleId)
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    _uiState.update { it.copy(isLoading = false, isMissing = true, error = "Couldn't load this rule.") }
                    return@launch
                }
                if (rule == null) {
                    _uiState.update { it.copy(isLoading = false, isMissing = true, error = "This rule no longer exists.") }
                } else {
                    original = rule
                    _uiState.value = EditorUiState(
                        ruleId = rule.id,
                        name = rule.name,
                        trigger = rule.trigger,
                        actions = rule.actions,
                        enabled = rule.enabled,
                        isLoading = false,
                        isNew = false,
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }

    fun onTriggerChange(trigger: Trigger) = _uiState.update { it.copy(trigger = trigger, error = null) }

    fun onEnabledChange(enabled: Boolean) = _uiState.update { it.copy(enabled = enabled, error = null) }

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
            enabled = state.enabled,
            constraints = existing?.constraints ?: Constraints(),
            cooldownMs = existing?.cooldownMs ?: defaultCooldownMs(),
            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
        )

        persist { saveRule(rule) }
    }

    fun onDelete() {
        val id = original?.id ?: return
        persist { deleteRule(id) }
    }

    private fun persist(block: suspend () -> Unit) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                block()
                _uiState.update { it.copy(saved = true, isSaving = false) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = if (error is RuleConflictException) error.message
                        else "Couldn't save the change. Please try again.",
                    )
                }
            }
        }
    }
}
