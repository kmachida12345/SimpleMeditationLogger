package com.github.kmachida12345.simplemeditationlogger.data.repository

import app.cash.turbine.test
import com.github.kmachida12345.simplemeditationlogger.data.dao.MeditationSessionDao
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class MeditationSessionRepositoryImplTest {
    
    private lateinit var dao: MeditationSessionDao
    private lateinit var repository: MeditationSessionRepositoryImpl
    
    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = MeditationSessionRepositoryImpl(dao)
    }
    
    @Test
    fun `getAllSessions returns flow from dao`() = runTest {
        // Given
        val sessions = listOf(createTestSession(1), createTestSession(2))
        every { dao.getAllSessions() } returns flowOf(sessions)
        
        // When & Then
        repository.getAllSessions().test {
            assertEquals(sessions, awaitItem())
            awaitComplete()
        }
    }
    
    @Test
    fun `insertSession calls dao and returns id`() = runTest {
        // Given
        val session = createTestSession(0)
        coEvery { dao.insertSession(session) } returns 123L
        
        // When
        val result = repository.insertSession(session)
        
        // Then
        assertEquals(123L, result)
        coVerify { dao.insertSession(session) }
    }
    
    @Test
    fun `markAsSynced updates session with sync info`() = runTest {
        // Given
        val sessionId = 1
        val recordId = "health_connect_123"
        val session = createTestSession(sessionId)
        coEvery { dao.getSessionById(sessionId) } returns session
        
        // When
        repository.markAsSynced(sessionId, recordId)
        
        // Then
        coVerify {
            dao.updateSession(match {
                it.id == sessionId &&
                it.isSyncedToHealthConnect &&
                it.healthConnectRecordId == recordId &&
                it.syncErrorMessage == null
            })
        }
    }
    
    @Test
    fun `markSyncFailed updates session with error`() = runTest {
        // Given
        val sessionId = 1
        val errorMessage = "Network error"
        val session = createTestSession(sessionId)
        coEvery { dao.getSessionById(sessionId) } returns session
        
        // When
        repository.markSyncFailed(sessionId, errorMessage)
        
        // Then
        coVerify {
            dao.updateSession(match {
                it.id == sessionId &&
                !it.isSyncedToHealthConnect &&
                it.syncErrorMessage == errorMessage
            })
        }
    }
    
    @Test
    fun `getSessionsByDate uses correct date range`() = runTest {
        // Given
        val date = LocalDate.of(2024, 1, 15)
        val sessions = listOf(createTestSession(1))
        every { dao.getSessionsByDateRange(any(), any()) } returns flowOf(sessions)
        
        // When & Then
        repository.getSessionsByDate(date).test {
            assertEquals(sessions, awaitItem())
            awaitComplete()
        }
        
        coVerify { dao.getSessionsByDateRange(any(), any()) }
    }
    
    private fun createTestSession(id: Int): MeditationSession {
        val now = Instant.now()
        return MeditationSession(
            id = id,
            startTime = now,
            endTime = now.plusSeconds(900),
            isSyncedToHealthConnect = false
        )
    }
}
