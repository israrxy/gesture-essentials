package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.domain.repository.RulesRepository
import kotlinx.coroutines.flow.Flow

class ObserveRulesUseCase(private val repository: RulesRepository) {
    operator fun invoke(): Flow<List<Rule>> = repository.observeRules()
}
