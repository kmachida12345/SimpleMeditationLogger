package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class StartMeditationSessionUseCaseTest {
    
    private val repository = mockk<com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository>(relaxed = true)
    private val useCase = StartMeditationSessionUseCase(repository)
    
    @Test
    fun `invoke with valid duration creates session with correct time`() = runTest {
        // Given
        val durationMinutes = 15
        val beforeStart = Instant.now()
        
        // When
        val result = useCase(durationMinutes)
        
        // Then
        assertTrue(result.isSuccess)
        val session = result.getOrThrow()
        
        assertTrue(session.startTime >= beforeStart)
        assertEquals(
            durationMinutes * 60L,
            session.endTime.epochSecond - session.startTime.epochSecond
        )
        assertFalse(session.isSyncedToHealthConnect)
    }
    
    @Test
    fun `invoke with zero duration fails`() = runTest {
        // When
        val result = useCase(0)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
    
    @Test
    fun `invoke with negative duration fails`() = runTest {
        // When
        val result = useCase(-5)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
