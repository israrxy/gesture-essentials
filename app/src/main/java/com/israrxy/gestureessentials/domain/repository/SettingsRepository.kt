package com.israrxy.gestureessentials.domain.repository

import com.israrxy.gestureessentials.core.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings)
}
