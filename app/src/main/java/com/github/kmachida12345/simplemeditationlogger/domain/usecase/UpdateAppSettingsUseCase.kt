package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.domain.model.MeditationConstants
import javax.inject.Inject

class UpdateAppSettingsUseCase @Inject constructor(
    private val repository: AppSettingsRepository
) {
    suspend fun updateDefaultMeditationMinutes(minutes: Int): Result<Unit> {
        return try {
            require(minutes >= MeditationConstants.MIN_MEDITATION_MINUTES) { 
                "Duration must be at least ${MeditationConstants.MIN_MEDITATION_MINUTES} minute" 
            }
            require(minutes <= MeditationConstants.MAX_MEDITATION_MINUTES) { 
                "Duration must be ${MeditationConstants.MAX_MEDITATION_MINUTES} minutes or less" 
            }
            
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
