package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import javax.inject.Inject

class UpdateAppSettingsUseCase @Inject constructor(
    private val repository: AppSettingsRepository
) {
    suspend fun updateDefaultMeditationMinutes(minutes: Int): Result<Unit> {
        return try {
            require(minutes > 0) { "Duration must be positive" }
            require(minutes <= 180) { "Duration must be 180 minutes or less" }
            
            repository.updateDefaultMeditationMinutes(minutes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateHealthConnectEnabled(enabled: Boolean): Result<Unit> {
        return try {
            repository.updateHealthConnectEnabled(enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
