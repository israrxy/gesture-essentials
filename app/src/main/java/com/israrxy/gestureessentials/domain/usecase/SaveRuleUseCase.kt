package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.domain.RuleAssignments
import com.israrxy.gestureessentials.domain.repository.RulesRepository

class SaveRuleUseCase(private val repository: RulesRepository) {
    suspend operator fun invoke(rule: Rule) = repository.updateRules { rules ->
        RuleAssignments.save(rules, rule)
    }
}
