package com.github.kmachida12345.simplemeditationlogger.data.repository

import com.github.kmachida12345.simplemeditationlogger.data.dao.AppSettingsDao
import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(
    private val dao: AppSettingsDao
) : AppSettingsRepository {
    
    override fun getSettings(): Flow<AppSettings?> {
        return dao.getSettings()
    }
    
    override suspend fun getSettingsSync(): AppSettings? {
        return dao.getSettingsSync()
    }
    
    override suspend fun updateDefaultMeditationMinutes(minutes: Int) {
        val currentSettings = dao.getSettingsSync() ?: AppSettings()
        dao.updateSettings(currentSettings.copy(defaultMeditationMinutes = minutes))
    }
    
    override suspend fun updateHealthConnectEnabled(enabled: Boolean) {
        val currentSettings = dao.getSettingsSync() ?: AppSettings()
        dao.updateSettings(currentSettings.copy(isHealthConnectEnabled = enabled))
    }
    
    override suspend fun initializeDefaultSettings() {
        if (dao.getSettingsSync() == null) {
            dao.insertSettings(AppSettings())
        }
    }
}
