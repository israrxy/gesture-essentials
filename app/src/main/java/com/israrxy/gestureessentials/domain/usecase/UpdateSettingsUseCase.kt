package com.israrxy.gestureessentials.domain.usecase

import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.domain.repository.SettingsRepository

class UpdateSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(transform: (AppSettings) -> AppSettings) =
        repository.updateSettings(transform)
}
