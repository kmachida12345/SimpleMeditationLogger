package com.github.kmachida12345.simplemeditationlogger.data.repository

import com.github.kmachida12345.simplemeditationlogger.data.dao.MeditationSessionDao
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.util.DateTimeHelper
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class MeditationSessionRepositoryImpl @Inject constructor(
    private val dao: MeditationSessionDao
) : MeditationSessionRepository {
    
    override fun getAllSessions(): Flow<List<MeditationSession>> {
        return dao.getAllSessions()
    }
    
    override suspend fun getSessionById(id: Int): MeditationSession? {
        return dao.getSessionById(id)
    }
    
    override fun getSessionsByDate(date: LocalDate): Flow<List<MeditationSession>> {
        val startOfDay = DateTimeHelper.getStartOfDay(date).toEpochMilli()
        val endOfDay = DateTimeHelper.getEndOfDay(date).toEpochMilli()
        return dao.getSessionsByDateRange(startOfDay, endOfDay)
    }
    
    override suspend fun getUnsyncedSessions(retryAfterMinutes: Long): List<MeditationSession> {
        val retryThreshold = DateTimeHelper.getRetryThreshold(retryAfterMinutes).toEpochMilli()
        return dao.getUnsyncedSessions(retryThreshold)
    }
    
    override suspend fun insertSession(session: MeditationSession): Long {
        return dao.insertSession(session)
    }
    
    override suspend fun updateSession(session: MeditationSession) {
        dao.updateSession(session)
    }
    
    override suspend fun deleteSession(session: MeditationSession) {
        dao.deleteSession(session)
    }
    
    override suspend fun markAsSynced(sessionId: Int, healthConnectRecordId: String): Result<Unit> {
        return try {
            val session = dao.getSessionById(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found: $sessionId"))
            
            dao.updateSession(
                session.copy(
                    isSyncedToHealthConnect = true,
                    healthConnectRecordId = healthConnectRecordId,
                    syncErrorMessage = null,
                    lastSyncAttemptTime = Instant.now()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markSyncFailed(sessionId: Int, errorMessage: String): Result<Unit> {
        return try {
            val session = dao.getSessionById(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found: $sessionId"))
            
            dao.updateSession(
                session.copy(
                    isSyncedToHealthConnect = false,
                    syncErrorMessage = errorMessage,
                    lastSyncAttemptTime = Instant.now()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
