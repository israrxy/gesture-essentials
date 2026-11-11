package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.domain.RuleAssignments
import com.israrxy.gestureessentials.domain.repository.RulesRepository

class SetRuleEnabledUseCase(private val repository: RulesRepository) {

    /** Returns the names of rules switched off because they used the same gesture. */
    suspend operator fun invoke(id: String, enabled: Boolean): List<String> {
        var disabled = emptyList<String>()
        repository.updateRules { rules ->
            val rule = rules.firstOrNull { it.id == id } ?: return@updateRules rules
            val result = RuleAssignments.save(rules, rule.copy(enabled = enabled))
            disabled = result.disabled.map { it.name }
            result.rules
        }
        return disabled
    }
}
