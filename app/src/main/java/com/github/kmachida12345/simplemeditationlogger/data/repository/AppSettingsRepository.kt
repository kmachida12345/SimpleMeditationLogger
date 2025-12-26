package com.github.kmachida12345.simplemeditationlogger.data.repository

import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getSettings(): Flow<AppSettings?>
    
    suspend fun getSettingsSync(): AppSettings?
    
    suspend fun updateDefaultMeditationMinutes(minutes: Int)
    
    suspend fun updateHealthConnectEnabled(enabled: Boolean)
    
    suspend fun initializeDefaultSettings()
}
