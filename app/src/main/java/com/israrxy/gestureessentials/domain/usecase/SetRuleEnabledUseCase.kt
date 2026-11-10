package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.domain.repository.RulesRepository

class SetRuleEnabledUseCase(private val repository: RulesRepository) {
    suspend operator fun invoke(id: String, enabled: Boolean) = repository.setEnabled(id, enabled)
}
