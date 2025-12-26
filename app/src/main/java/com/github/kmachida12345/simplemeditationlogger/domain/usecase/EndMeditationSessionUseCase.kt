package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import java.time.Instant
import javax.inject.Inject

class EndMeditationSessionUseCase @Inject constructor(
    private val sessionRepository: MeditationSessionRepository,
    private val settingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(
        startTime: Instant,
        actualEndTime: Instant = Instant.now()
    ): Result<Long> {
        return try {
            require(actualEndTime >= startTime) { "End time must be after start time" }
            
            val session = MeditationSession(
                startTime = startTime,
                endTime = actualEndTime,
                isSyncedToHealthConnect = false
            )
            
            val sessionId = sessionRepository.insertSession(session)
            
            // ヘルスコネクト連携が有効なら同期を試みる（将来的にはWorkerで非同期実行）
            val settings = settingsRepository.getSettingsSync()
            if (settings?.isHealthConnectEnabled == true) {
                // TODO: バックグラウンドで同期処理をトリガー
            }
            
            Result.success(sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
