package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateDefaultDurationUseCaseTest {

    private val repository = mockk<AppSettingsRepository>(relaxed = true)
    private val useCase = UpdateDefaultDurationUseCase(repository)

    @Test
    fun `invoke forwards duration to repository`() = runTest {
        // When
        val result = useCase(300)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateDefaultDurationSeconds(300) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest {
        // Given
        coEvery {
            repository.updateDefaultDurationSeconds(any())
        } throws IllegalArgumentException("Duration must be positive")

        // When
        val result = useCase(0)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
