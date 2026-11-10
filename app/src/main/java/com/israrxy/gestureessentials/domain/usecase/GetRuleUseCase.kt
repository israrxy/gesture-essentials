package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.domain.repository.RulesRepository

class GetRuleUseCase(private val repository: RulesRepository) {
    suspend operator fun invoke(id: String): Rule? = repository.getRule(id)
}
