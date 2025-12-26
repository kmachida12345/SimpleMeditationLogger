package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import javax.inject.Inject

class UpdateHealthConnectEnabledUseCase @Inject constructor(
    private val repository: AppSettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = try {
        repository.updateHealthConnectEnabled(enabled)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
