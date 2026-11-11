package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.domain.RuleAssignments
import com.israrxy.gestureessentials.domain.repository.RulesRepository

class SetPresetEnabledUseCase(private val repository: RulesRepository) {

    /** Returns the names of rules switched off because they used the same gesture. */
    suspend operator fun invoke(
        preset: ShortcutPreset,
        enabled: Boolean,
        cooldownMs: Long,
    ): List<String> {
        var disabled = emptyList<String>()
        repository.updateRules { rules ->
            val existing = rules.firstOrNull { it.id == preset.ruleId }
            if (existing == null && !enabled) return@updateRules rules
            val rule = existing ?: preset.newRule(cooldownMs, System.currentTimeMillis())
            val result = RuleAssignments.save(rules, rule.copy(enabled = enabled))
            disabled = result.disabled.map { it.name }
            result.rules
        }
        return disabled
    }
}
