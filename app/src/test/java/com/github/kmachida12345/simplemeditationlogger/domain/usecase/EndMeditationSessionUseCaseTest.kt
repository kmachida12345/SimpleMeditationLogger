package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import com.github.kmachida12345.simplemeditationlogger.domain.model.HealthConnectSyncStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class EndMeditationSessionUseCaseTest {

    private val sessionRepository = mockk<MeditationSessionRepository>(relaxed = true)
    private val settingsRepository = mockk<AppSettingsRepository>(relaxed = true)
    private val syncToHealthConnectUseCase = mockk<SyncToHealthConnectUseCase>(relaxed = true)
    private val useCase = EndMeditationSessionUseCase(
        sessionRepository,
        settingsRepository,
        syncToHealthConnectUseCase
    )

    @Test
    fun `invoke saves session and returns DISABLED when health connect is off`() = runTest {
        // Given
        val startTime = Instant.now().minusSeconds(900)
        val endTime = Instant.now()
        coEvery { sessionRepository.insertSession(any()) } returns 1L
        every { settingsRepository.getHealthConnectEnabled() } returns flowOf(false)

        // When
        val result = useCase(startTime, endTime)

        // Then
        assertTrue(result.isSuccess)
        val endSessionResult = result.getOrThrow()
        assertEquals(1, endSessionResult.session.id)
        assertEquals(startTime, endSessionResult.session.startTime)
        assertEquals(endTime, endSessionResult.session.endTime)
        assertEquals(HealthConnectSyncStatus.DISABLED, endSessionResult.syncStatus)
        coVerify(exactly = 1) { sessionRepository.insertSession(any()) }
        coVerify(exactly = 0) { syncToHealthConnectUseCase.invoke(any()) }
    }

    @Test
    fun `invoke returns SYNCED when health connect is on and sync succeeds`() = runTest {
        // Given
        val startTime = Instant.now().minusSeconds(900)
        coEvery { sessionRepository.insertSession(any()) } returns 5L
        every { settingsRepository.getHealthConnectEnabled() } returns flowOf(true)
        coEvery { syncToHealthConnectUseCase.invoke(any()) } returns Result.success(Unit)

        // When
        val result = useCase(startTime)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(HealthConnectSyncStatus.SYNCED, result.getOrThrow().syncStatus)
        coVerify(exactly = 1) { syncToHealthConnectUseCase.invoke(match { it.id == 5 }) }
    }

    @Test
    fun `invoke returns SYNC_FAILED but overall success when sync fails`() = runTest {
        // Given
        val startTime = Instant.now().minusSeconds(900)
        coEvery { sessionRepository.insertSession(any()) } returns 1L
        every { settingsRepository.getHealthConnectEnabled() } returns flowOf(true)
        coEvery { syncToHealthConnectUseCase.invoke(any()) } returns
            Result.failure(RuntimeException("sync error"))

        // When
        val result = useCase(startTime)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(HealthConnectSyncStatus.SYNC_FAILED, result.getOrThrow().syncStatus)
    }

    @Test
    fun `invoke with end time before start time fails`() = runTest {
        // Given
        val startTime = Instant.now()
        val endTime = startTime.minusSeconds(100)

        // When
        val result = useCase(startTime, endTime)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { sessionRepository.insertSession(any()) }
    }
}
