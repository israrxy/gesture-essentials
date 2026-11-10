package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.domain.RuleAssignments
import com.israrxy.gestureessentials.domain.repository.RulesRepository

class SetRuleEnabledUseCase(private val repository: RulesRepository) {
    suspend operator fun invoke(id: String, enabled: Boolean) = repository.updateRules { rules ->
        val rule = rules.firstOrNull { it.id == id } ?: return@updateRules rules
        RuleAssignments.save(rules, rule.copy(enabled = enabled))
    }
}
