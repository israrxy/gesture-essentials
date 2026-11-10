package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.domain.repository.RulesRepository

class DeleteRuleUseCase(private val repository: RulesRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}
