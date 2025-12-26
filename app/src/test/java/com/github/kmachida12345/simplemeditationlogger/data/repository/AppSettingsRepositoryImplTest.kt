package com.github.kmachida12345.simplemeditationlogger.data.repository

import app.cash.turbine.test
import com.github.kmachida12345.simplemeditationlogger.data.dao.AppSettingsDao
import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AppSettingsRepositoryImplTest {
    
    private lateinit var dao: AppSettingsDao
    private lateinit var repository: AppSettingsRepositoryImpl
    
    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = AppSettingsRepositoryImpl(dao)
    }
    
    @Test
    fun `getSettings returns flow from dao`() = runTest {
        // Given
        val settings = AppSettings(defaultMeditationMinutes = 20)
        every { dao.getSettings() } returns flowOf(settings)
        
        // When & Then
        repository.getSettings().test {
            assertEquals(settings, awaitItem())
            awaitComplete()
        }
    }
    
    @Test
    fun `updateDefaultMeditationMinutes updates only that field`() = runTest {
        // Given
        val existingSettings = AppSettings(
            defaultMeditationMinutes = 15,
            isHealthConnectEnabled = true
        )
        coEvery { dao.getSettingsSync() } returns existingSettings
        
        // When
        repository.updateDefaultMeditationMinutes(30)
        
        // Then
        coVerify {
            dao.updateSettings(match {
                it.defaultMeditationMinutes == 30 &&
                it.isHealthConnectEnabled == true
            })
        }
    }
    
    @Test
    fun `updateHealthConnectEnabled updates only that field`() = runTest {
        // Given
        val existingSettings = AppSettings(
            defaultMeditationMinutes = 15,
            isHealthConnectEnabled = false
        )
        coEvery { dao.getSettingsSync() } returns existingSettings
        
        // When
        repository.updateHealthConnectEnabled(true)
        
        // Then
        coVerify {
            dao.updateSettings(match {
                it.defaultMeditationMinutes == 15 &&
                it.isHealthConnectEnabled == true
            })
        }
    }
    
    @Test
    fun `initializeDefaultSettings inserts when not exists`() = runTest {
        // Given
        coEvery { dao.getSettingsSync() } returns null
        
        // When
        repository.initializeDefaultSettings()
        
        // Then
        coVerify { dao.insertSettings(any()) }
    }
    
    @Test
    fun `initializeDefaultSettings does nothing when already exists`() = runTest {
        // Given
        coEvery { dao.getSettingsSync() } returns AppSettings()
        
        // When
        repository.initializeDefaultSettings()
        
        // Then
        coVerify(exactly = 0) { dao.insertSettings(any()) }
    }
}
