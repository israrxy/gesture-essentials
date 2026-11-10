package com.israrxy.gestureessentials.domain.repository

import com.israrxy.gestureessentials.core.model.Rule
import kotlinx.coroutines.flow.Flow

interface RulesRepository {
    fun observeRules(): Flow<List<Rule>>
    suspend fun getRule(id: String): Rule?
    suspend fun delete(id: String)
    /** The transform and write are serialized with all other rule mutations. */
    suspend fun updateRules(transform: (List<Rule>) -> List<Rule>)
}
