package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import com.github.kmachida12345.simplemeditationlogger.domain.model.HealthConnectSyncStatus
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class EndMeditationSessionUseCase @Inject constructor(
    private val sessionRepository: MeditationSessionRepository,
    private val settingsRepository: AppSettingsRepository,
    private val syncToHealthConnectUseCase: SyncToHealthConnectUseCase
) {
    data class EndSessionResult(
        val session: MeditationSession,
        val syncStatus: HealthConnectSyncStatus
    )

    suspend operator fun invoke(
        startTime: Instant,
        actualEndTime: Instant = Instant.now()
    ): Result<EndSessionResult> {
        return try {
            require(actualEndTime >= startTime) { "End time must be after or equal to start time" }

            val session = MeditationSession(
                startTime = startTime,
                endTime = actualEndTime,
                isSyncedToHealthConnect = false
            )

            val sessionId = sessionRepository.insertSession(session)
            val savedSession = session.copy(id = sessionId.toInt())

            val healthConnectEnabled = settingsRepository.getHealthConnectEnabled().first()
            val syncStatus = if (healthConnectEnabled) {
                syncToHealthConnectUseCase(savedSession).fold(
                    onSuccess = { HealthConnectSyncStatus.SYNCED },
                    onFailure = { HealthConnectSyncStatus.SYNC_FAILED }
                )
            } else {
                HealthConnectSyncStatus.DISABLED
            }

            Result.success(EndSessionResult(savedSession, syncStatus))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
