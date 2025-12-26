package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class SyncToHealthConnectUseCaseTest {
    
    private val repository = mockk<MeditationSessionRepository>(relaxed = true)
    private val useCase = SyncToHealthConnectUseCase(repository)
    
    @Test
    fun `invoke with unsynced session succeeds`() = runTest {
        // Given
        val session = createTestSession(1, isSynced = false)
        coEvery { repository.markAsSynced(any(), any()) } returns Result.success(Unit)
        
        // When
        val result = useCase(session)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.markAsSynced(1, any()) }
    }
    
    @Test
    fun `invoke with already synced session returns success immediately`() = runTest {
        // Given
        val session = createTestSession(1, isSynced = true)
        
        // When
        val result = useCase(session)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { repository.markAsSynced(any(), any()) }
    }
    
    @Test
    fun `invoke marks sync as failed when exception occurs`() = runTest {
        // Given
        val session = createTestSession(1, isSynced = false)
        val error = RuntimeException("Network error")
        coEvery { repository.markAsSynced(any(), any()) } returns Result.failure(error)
        coEvery { repository.markSyncFailed(any(), any()) } returns Result.success(Unit)
        
        // When
        val result = useCase(session)
        
        // Then
        assertTrue(result.isFailure)
        coVerify { repository.markSyncFailed(1, any()) }
    }
    
    @Test
    fun `syncAllUnsynced processes all sessions in parallel`() = runTest {
        // Given
        val sessions = listOf(
            createTestSession(1, isSynced = false),
            createTestSession(2, isSynced = false),
            createTestSession(3, isSynced = false)
        )
        coEvery { repository.getUnsyncedSessions(any()) } returns sessions
        coEvery { repository.markAsSynced(any(), any()) } returns Result.success(Unit)
        
        // When
        val result = useCase.syncAllUnsynced()
        
        // Then
        assertTrue(result.isSuccess)
        val syncResult = result.getOrThrow()
        assertEquals(3, syncResult.successCount)
        assertEquals(0, syncResult.failureCount)
        assertEquals(3, syncResult.totalCount)
    }
    
    @Test
    fun `syncAllUnsynced handles partial failures`() = runTest {
        // Given
        val sessions = listOf(
            createTestSession(1, isSynced = false),
            createTestSession(2, isSynced = false),
            createTestSession(3, isSynced = false)
        )
        coEvery { repository.getUnsyncedSessions(any()) } returns sessions
        coEvery { repository.markAsSynced(1, any()) } returns Result.success(Unit)
        coEvery { repository.markAsSynced(2, any()) } returns Result.failure(RuntimeException("Error"))
        coEvery { repository.markAsSynced(3, any()) } returns Result.success(Unit)
        coEvery { repository.markSyncFailed(any(), any()) } returns Result.success(Unit)
        
        // When
        val result = useCase.syncAllUnsynced()
        
        // Then
        assertTrue(result.isSuccess)
        val syncResult = result.getOrThrow()
        assertEquals(2, syncResult.successCount)
        assertEquals(1, syncResult.failureCount)
    }
    
    @Test
    fun `syncAllUnsynced returns zero counts when no unsynced sessions`() = runTest {
        // Given
        coEvery { repository.getUnsyncedSessions(any()) } returns emptyList()
        
        // When
        val result = useCase.syncAllUnsynced()
        
        // Then
        assertTrue(result.isSuccess)
        val syncResult = result.getOrThrow()
        assertEquals(0, syncResult.successCount)
        assertEquals(0, syncResult.failureCount)
    }
    
    private fun createTestSession(id: Int, isSynced: Boolean): MeditationSession {
        val now = Instant.now()
        return MeditationSession(
            id = id,
            startTime = now.minusSeconds(900),
            endTime = now,
            isSyncedToHealthConnect = isSynced
        )
    }
}
