package com.israrxy.gestureessentials.data.repository

import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.data.datastore.SettingsDataSource
import com.israrxy.gestureessentials.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataSource: SettingsDataSource,
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = dataSource.observeSettings()

    override suspend fun updateSettings(transform: (AppSettings) -> AppSettings) =
        dataSource.update(transform)
}
