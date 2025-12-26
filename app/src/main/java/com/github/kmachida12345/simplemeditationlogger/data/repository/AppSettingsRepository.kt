package com.github.kmachida12345.simplemeditationlogger.data.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getDefaultDurationSeconds(): Flow<Int>
    fun getHealthConnectEnabled(): Flow<Boolean>
    suspend fun updateDefaultDurationSeconds(seconds: Int)
    suspend fun updateHealthConnectEnabled(enabled: Boolean)
}
