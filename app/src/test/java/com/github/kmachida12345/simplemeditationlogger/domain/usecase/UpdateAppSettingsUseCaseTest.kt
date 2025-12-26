package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateAppSettingsUseCaseTest {
    
    private val repository = mockk<AppSettingsRepository>(relaxed = true)
    private val useCase = UpdateAppSettingsUseCase(repository)
    
    @Test
    fun `updateDefaultMeditationMinutes with valid value succeeds`() = runTest {
        // When
        val result = useCase.updateDefaultMeditationMinutes(30)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.updateDefaultMeditationMinutes(30) }
    }
    
    @Test
    fun `updateDefaultMeditationMinutes with zero fails`() = runTest {
        // When
        val result = useCase.updateDefaultMeditationMinutes(0)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
    
    @Test
    fun `updateDefaultMeditationMinutes with too large value fails`() = runTest {
        // When
        val result = useCase.updateDefaultMeditationMinutes(200)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
    
    @Test
    fun `updateHealthConnectEnabled updates repository`() = runTest {
        // When
        val result = useCase.updateHealthConnectEnabled(true)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.updateHealthConnectEnabled(true) }
    }
}
