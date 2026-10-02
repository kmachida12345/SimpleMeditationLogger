package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.healthconnect.HealthConnectManager
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class SyncToHealthConnectUseCaseTest {

    private val repository = mockk<MeditationSessionRepository>(relaxed = true)
    private val healthConnectManager = mockk<HealthConnectManager>(relaxed = true)
    private val useCase = SyncToHealthConnectUseCase(repository, healthConnectManager)

    @Before
    fun setup() {
        coEvery { repository.markAsSynced(any(), any()) } returns Result.success(Unit)
        coEvery { repository.markSyncFailed(any(), any()) } returns Result.success(Unit)
    }

    @Test
    fun `invoke skips already synced session`() = runTest {
        // Given
        val session = createTestSession(1).copy(isSyncedToHealthConnect = true)

        // When
        val result = useCase(session)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { healthConnectManager.writeMeditationSession(any(), any()) }
    }

    @Test
    fun `invoke marks session synced with record id returned by health connect`() = runTest {
        // Given
        val session = createTestSession(1)
        coEvery {
            healthConnectManager.writeMeditationSession(session.startTime, session.endTime)
        } returns Result.success("hc-record-id")

        // When
        val result = useCase(session)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.markAsSynced(1, "hc-record-id") }
    }

    @Test
    fun `invoke marks sync failed and returns failure when write fails`() = runTest {
        // Given
        val session = createTestSession(1)
        coEvery {
            healthConnectManager.writeMeditationSession(any(), any())
        } returns Result.failure(SecurityException("Missing permissions"))

        // When
        val result = useCase(session)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.markSyncFailed(1, "Missing permissions") }
        coVerify(exactly = 0) { repository.markAsSynced(any(), any()) }
    }

    @Test
    fun `syncAllUnsynced returns zero counts when nothing to sync`() = runTest {
        // Given
        coEvery { repository.getUnsyncedSessions(any()) } returns emptyList()

        // When
        val result = useCase.syncAllUnsynced()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().totalCount)
    }

    @Test
    fun `syncAllUnsynced counts successes and failures`() = runTest {
        // Given
        val success = createTestSession(1)
        val failure = createTestSession(2)
        coEvery { repository.getUnsyncedSessions(any()) } returns listOf(success, failure)
        coEvery {
            healthConnectManager.writeMeditationSession(success.startTime, success.endTime)
        } returns Result.success("hc-record-id")
        coEvery {
            healthConnectManager.writeMeditationSession(failure.startTime, failure.endTime)
        } returns Result.failure(RuntimeException("write error"))

        // When
        val result = useCase.syncAllUnsynced()

        // Then
        assertTrue(result.isSuccess)
        val syncResult = result.getOrThrow()
        assertEquals(1, syncResult.successCount)
        assertEquals(1, syncResult.failureCount)
        assertEquals(2, syncResult.totalCount)
    }

    private fun createTestSession(id: Int): MeditationSession {
        val start = Instant.parse("2026-01-01T00:00:00Z").plusSeconds(id * 3600L)
        return MeditationSession(
            id = id,
            startTime = start,
            endTime = start.plusSeconds(600),
            isSyncedToHealthConnect = false
        )
    }
}
