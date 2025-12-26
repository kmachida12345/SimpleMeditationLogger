package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import javax.inject.Inject

class SyncToHealthConnectUseCase @Inject constructor(
    private val repository: MeditationSessionRepository
) {
    suspend operator fun invoke(session: MeditationSession): Result<Unit> {
        return try {
            // TODO: Health Connect SDK統合時に実装
            // 現在はモック実装
            
            if (session.isSyncedToHealthConnect) {
                return Result.success(Unit)
            }
            
            // モック: 実際にはHealth Connect APIを呼ぶ
            val mockHealthConnectRecordId = "mock_${session.id}_${System.currentTimeMillis()}"
            
            repository.markAsSynced(
                sessionId = session.id,
                healthConnectRecordId = mockHealthConnectRecordId
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            repository.markSyncFailed(
                sessionId = session.id,
                errorMessage = e.message ?: "Unknown error"
            )
            Result.failure(e)
        }
    }
    
    suspend fun syncAllUnsynced(retryAfterMinutes: Long = 5): Result<Int> {
        return try {
            val unsyncedSessions = repository.getUnsyncedSessions(retryAfterMinutes)
            var successCount = 0
            
            unsyncedSessions.forEach { session ->
                val result = invoke(session)
                if (result.isSuccess) {
                    successCount++
                }
            }
            
            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
