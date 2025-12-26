package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class EndMeditationSessionUseCaseTest {
    
    private val sessionRepository = mockk<MeditationSessionRepository>(relaxed = true)
    private val settingsRepository = mockk<AppSettingsRepository>(relaxed = true)
    private val useCase = EndMeditationSessionUseCase(sessionRepository, settingsRepository)
    
    @Test
    fun `invoke saves session to repository and returns session`() = runTest {
        // Given
        val startTime = Instant.now().minusSeconds(900)
        val endTime = Instant.now()
        coEvery { sessionRepository.insertSession(any()) } returns 1L
        coEvery { settingsRepository.getSettingsSync() } returns AppSettings()
        
        // When
        val result = useCase(startTime, endTime)
        
        // Then
        assertTrue(result.isSuccess)
        val session = result.getOrThrow()
        assertEquals(1, session.id)
        assertEquals(startTime, session.startTime)
        assertEquals(endTime, session.endTime)
        coVerify(exactly = 1) { sessionRepository.insertSession(any()) }
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
    }
    
    @Test
    fun `invoke with health connect enabled checks settings`() = runTest {
        // Given
        val startTime = Instant.now().minusSeconds(900)
        val endTime = Instant.now()
        coEvery { sessionRepository.insertSession(any()) } returns 1L
        coEvery { settingsRepository.getSettingsSync() } returns AppSettings(
            isHealthConnectEnabled = true
        )
        
        // When
        val result = useCase(startTime, endTime)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { settingsRepository.getSettingsSync() }
    }
}
