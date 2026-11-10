package com.israrxy.gestureessentials.data.repository

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.data.datastore.RulesDataSource
import com.israrxy.gestureessentials.domain.repository.RulesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RulesRepositoryImpl(
    private val dataSource: RulesDataSource,
) : RulesRepository {

    private val mutex = Mutex()

    override fun observeRules(): Flow<List<Rule>> = dataSource.observeRules()
        .map { rules -> rules.sortedByDescending { it.createdAt } }

    override suspend fun getRule(id: String): Rule? =
        dataSource.readRules().firstOrNull { it.id == id }

    override suspend fun save(rule: Rule) = mutate { rules ->
        rules.filterNot { it.id == rule.id } + rule
    }

    override suspend fun delete(id: String) = mutate { rules ->
        rules.filterNot { it.id == id }
    }

    override suspend fun setEnabled(id: String, enabled: Boolean) = mutate { rules ->
        rules.map { if (it.id == id) it.copy(enabled = enabled) else it }
    }

    /** Read-modify-write is serialised so concurrent toggles cannot drop edits. */
    private suspend fun mutate(block: (List<Rule>) -> List<Rule>) = mutex.withLock {
        val updated = block(dataSource.readRules())
        dataSource.writeRules(updated)
    }
}
