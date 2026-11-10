package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.domain.RuleAssignments
import com.israrxy.gestureessentials.domain.repository.RulesRepository

class SetPresetEnabledUseCase(private val repository: RulesRepository) {
    suspend operator fun invoke(preset: ShortcutPreset, enabled: Boolean, cooldownMs: Long) {
        repository.updateRules { rules ->
            val existing = rules.firstOrNull { it.id == preset.ruleId }
            if (existing == null && !enabled) return@updateRules rules
            val rule = existing ?: preset.newRule(cooldownMs, System.currentTimeMillis())
            RuleAssignments.save(rules, rule.copy(enabled = enabled))
        }
    }
}
