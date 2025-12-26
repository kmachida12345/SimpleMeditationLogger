package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import java.time.Instant
import javax.inject.Inject

class StartMeditationSessionUseCase @Inject constructor(
    private val repository: MeditationSessionRepository
) {
    suspend operator fun invoke(durationMinutes: Int): Result<MeditationSession> {
        return try {
            require(durationMinutes > 0) { "Duration must be positive" }
            
            val startTime = Instant.now()
            val endTime = startTime.plusSeconds(durationMinutes * 60L)
            
            val session = MeditationSession(
                startTime = startTime,
                endTime = endTime,
                isSyncedToHealthConnect = false
            )
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
