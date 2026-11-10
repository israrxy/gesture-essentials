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

    override suspend fun delete(id: String) = updateRules { rules ->
        rules.filterNot { it.id == id }
    }

    /** Read-modify-write is serialised so concurrent toggles cannot drop edits. */
    override suspend fun updateRules(transform: (List<Rule>) -> List<Rule>) = mutex.withLock {
        val updated = transform(dataSource.readRules())
        dataSource.writeRules(updated)
    }
}
