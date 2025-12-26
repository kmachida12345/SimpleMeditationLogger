package com.github.kmachida12345.simplemeditationlogger.data.repository

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MeditationSessionRepository {
    fun getAllSessions(): Flow<List<MeditationSession>>
    
    suspend fun getSessionById(id: Int): MeditationSession?
    
    fun getSessionsByDate(date: LocalDate): Flow<List<MeditationSession>>
    
    suspend fun getUnsyncedSessions(retryAfterMinutes: Long): List<MeditationSession>
    
    suspend fun insertSession(session: MeditationSession): Long
    
    suspend fun updateSession(session: MeditationSession)
    
    suspend fun deleteSession(session: MeditationSession)
    
    suspend fun markAsSynced(sessionId: Int, healthConnectRecordId: String): Result<Unit>
    
    suspend fun markSyncFailed(sessionId: Int, errorMessage: String): Result<Unit>
}
