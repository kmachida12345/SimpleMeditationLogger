package com.github.kmachida12345.simplemeditationlogger.data.repository

import com.github.kmachida12345.simplemeditationlogger.data.datastore.AppSettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(
    private val dataStore: AppSettingsDataStore
) : AppSettingsRepository {
    
    override fun getDefaultDurationSeconds(): Flow<Int> = 
        dataStore.defaultDurationSeconds
    
    override fun getHealthConnectEnabled(): Flow<Boolean> = 
        dataStore.healthConnectEnabled
    
    override suspend fun updateDefaultDurationSeconds(seconds: Int) {
        require(seconds > 0) { "Duration must be positive" }
        dataStore.updateDefaultDurationSeconds(seconds)
    }
    
    override suspend fun updateHealthConnectEnabled(enabled: Boolean) {
        dataStore.updateHealthConnectEnabled(enabled)
    }
}
