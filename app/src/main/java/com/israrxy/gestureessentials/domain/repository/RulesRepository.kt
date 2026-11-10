package com.israrxy.gestureessentials.domain.repository

import com.israrxy.gestureessentials.core.model.Rule
import kotlinx.coroutines.flow.Flow

interface RulesRepository {
    fun observeRules(): Flow<List<Rule>>
    suspend fun getRule(id: String): Rule?
    suspend fun save(rule: Rule)
    suspend fun delete(id: String)
    suspend fun setEnabled(id: String, enabled: Boolean)
}
