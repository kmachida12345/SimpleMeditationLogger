package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import javax.inject.Inject

class UpdateDefaultDurationUseCase @Inject constructor(
    private val repository: AppSettingsRepository
) {
    suspend operator fun invoke(durationSeconds: Int): Result<Unit> = try {
        repository.updateDefaultDurationSeconds(durationSeconds)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
