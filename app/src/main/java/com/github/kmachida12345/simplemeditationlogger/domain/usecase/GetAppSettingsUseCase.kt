package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppSettingsUseCase @Inject constructor(
    private val repository: AppSettingsRepository
) {
    operator fun invoke(): Flow<AppSettings?> {
        return repository.getSettings()
    }
    
    suspend fun initialize() {
        repository.initializeDefaultSettings()
    }
}
