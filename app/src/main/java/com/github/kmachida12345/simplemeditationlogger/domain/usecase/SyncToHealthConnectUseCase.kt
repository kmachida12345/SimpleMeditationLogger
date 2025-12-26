package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import com.github.kmachida12345.simplemeditationlogger.domain.model.MeditationConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SyncToHealthConnectUseCase @Inject constructor(
    private val repository: MeditationSessionRepository
) {
    suspend operator fun invoke(session: MeditationSession): Result<Unit> {
        if (session.isSyncedToHealthConnect) {
            return Result.success(Unit)
        }
        
        // TODO: Health Connect SDK統合時に実装
        // モック: 実際にはHealth Connect APIを呼ぶ
        val mockHealthConnectRecordId = "mock_${session.id}_${System.currentTimeMillis()}"
        
        val syncResult = repository.markAsSynced(
            sessionId = session.id,
            healthConnectRecordId = mockHealthConnectRecordId
        )
        
        return syncResult.onFailure { exception ->
            // markSyncFailed自体が失敗しても例外を投げない（ログのみ）
            runCatching {
                repository.markSyncFailed(
                    sessionId = session.id,
                    errorMessage = exception.message ?: "Unknown error"
                )
            }
        }
    }
    
    suspend fun syncAllUnsynced(
        retryAfterMinutes: Long = MeditationConstants.DEFAULT_SYNC_RETRY_MINUTES
    ): Result<SyncResult> {
        return try {
            val unsyncedSessions = repository.getUnsyncedSessions(retryAfterMinutes)
            
            if (unsyncedSessions.isEmpty()) {
                return Result.success(SyncResult(successCount = 0, failureCount = 0))
            }
            
            val results = coroutineScope {
                unsyncedSessions.map { session ->
                    async {
                        invoke(session)
                    }
                }.awaitAll()
            }
            
            val successCount = results.count { it.isSuccess }
            val failureCount = results.count { it.isFailure }
            
            Result.success(SyncResult(successCount, failureCount))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    data class SyncResult(
        val successCount: Int,
        val failureCount: Int
    ) {
        val totalCount: Int get() = successCount + failureCount
    }
}
