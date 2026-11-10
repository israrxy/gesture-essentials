package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.observeSettings()
}
