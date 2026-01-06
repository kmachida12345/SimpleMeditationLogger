package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.healthconnect.HealthConnectManager
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import com.github.kmachida12345.simplemeditationlogger.domain.model.MeditationConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SyncToHealthConnectUseCase @Inject constructor(
    private val repository: MeditationSessionRepository,
    private val healthConnectManager: HealthConnectManager
) {
    suspend operator fun invoke(session: MeditationSession): Result<Unit> {
        if (session.isSyncedToHealthConnect) {
            return Result.success(Unit)
        }
        
        val writeResult = healthConnectManager.writeMeditationSession(
            startTime = session.startTime,
            endTime = session.endTime
        )
        
        return writeResult.fold(
            onSuccess = { recordId ->
                repository.markAsSynced(
                    sessionId = session.id,
                    healthConnectRecordId = recordId
                )
            },
            onFailure = { exception ->
                runCatching {
                    repository.markSyncFailed(
                        sessionId = session.id,
                        errorMessage = exception.message ?: "Unknown error"
                    )
                }
                Result.failure(exception)
            }
        )
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
