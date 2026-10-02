package com.github.kmachida12345.simplemeditationlogger.data.repository

import app.cash.turbine.test
import com.github.kmachida12345.simplemeditationlogger.data.datastore.AppSettingsDataStore
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AppSettingsRepositoryImplTest {

    private val dataStore = mockk<AppSettingsDataStore>(relaxed = true)
    private val repository = AppSettingsRepositoryImpl(dataStore)

    @Test
    fun `getDefaultDurationSeconds passes through datastore flow`() = runTest {
        // Given
        every { dataStore.defaultDurationSeconds } returns flowOf(300)

        // When & Then
        repository.getDefaultDurationSeconds().test {
            assertEquals(300, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getHealthConnectEnabled passes through datastore flow`() = runTest {
        // Given
        every { dataStore.healthConnectEnabled } returns flowOf(true)

        // When & Then
        repository.getHealthConnectEnabled().test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `updateDefaultDurationSeconds forwards to datastore`() = runTest {
        // When
        repository.updateDefaultDurationSeconds(600)

        // Then
        coVerify(exactly = 1) { dataStore.updateDefaultDurationSeconds(600) }
    }

    @Test
    fun `updateDefaultDurationSeconds rejects non-positive values`() = runTest {
        // When
        try {
            repository.updateDefaultDurationSeconds(0)
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Then
            assertTrue(e.message!!.contains("positive"))
        }
        coVerify(exactly = 0) { dataStore.updateDefaultDurationSeconds(any()) }
    }

    @Test
    fun `updateHealthConnectEnabled forwards to datastore`() = runTest {
        // When
        repository.updateHealthConnectEnabled(true)

        // Then
        coVerify(exactly = 1) { dataStore.updateHealthConnectEnabled(true) }
    }
}
