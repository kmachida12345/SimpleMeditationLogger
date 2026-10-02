package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class UpdateHealthConnectEnabledUseCaseTest {

    private val repository = mockk<AppSettingsRepository>(relaxed = true)
    private val useCase = UpdateHealthConnectEnabledUseCase(repository)

    @Test
    fun `invoke forwards enabled flag to repository`() = runTest {
        // When
        val result = useCase(true)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateHealthConnectEnabled(true) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest {
        // Given
        coEvery { repository.updateHealthConnectEnabled(any()) } throws IOException("disk error")

        // When
        val result = useCase(false)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }
}
