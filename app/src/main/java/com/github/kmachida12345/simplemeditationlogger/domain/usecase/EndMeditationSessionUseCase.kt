package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

class EndMeditationSessionUseCase @Inject constructor(
    private val sessionRepository: MeditationSessionRepository,
    private val settingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(
        startTime: Instant,
        actualEndTime: Instant = Instant.now()
    ): Result<MeditationSession> {
        return try {
            require(actualEndTime >= startTime) { "End time must be after or equal to start time" }
            
            val session = MeditationSession(
                startTime = startTime,
                endTime = actualEndTime,
                isSyncedToHealthConnect = false
            )
            
            val sessionId = sessionRepository.insertSession(session)
            val savedSession = session.copy(id = sessionId.toInt())
            
            // ヘルスコネクト連携が有効なら同期を試みる（将来的にはWorkerで非同期実行）
            val healthConnectEnabled = settingsRepository.getHealthConnectEnabled().first()
            if (healthConnectEnabled) {
                // TODO: バックグラウンドで同期処理をトリガー（WorkManager）
            }
            
            Result.success(savedSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
