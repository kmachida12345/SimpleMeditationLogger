package com.github.kmachida12345.simplemeditationlogger.data.dao

import androidx.room.*
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationSessionDao {
    @Query("SELECT * FROM meditation_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<MeditationSession>>
    
    @Query("SELECT * FROM meditation_sessions WHERE id = :id")
    suspend fun getSessionById(id: Int): MeditationSession?
    
    @Query("SELECT * FROM meditation_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay ORDER BY startTime DESC")
    fun getSessionsByDateRange(startOfDay: Long, endOfDay: Long): Flow<List<MeditationSession>>
    
    @Query("SELECT * FROM meditation_sessions WHERE isSyncedToHealthConnect = 0 AND (lastSyncAttemptTime IS NULL OR lastSyncAttemptTime < :retryAfter)")
    suspend fun getUnsyncedSessions(retryAfter: Long): List<MeditationSession>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: MeditationSession): Long
    
    @Update
    suspend fun updateSession(session: MeditationSession)
    
    @Delete
    suspend fun deleteSession(session: MeditationSession)
}
