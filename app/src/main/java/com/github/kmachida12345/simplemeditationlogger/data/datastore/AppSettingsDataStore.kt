package com.github.kmachida12345.simplemeditationlogger.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class AppSettingsDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        private val DEFAULT_DURATION_SECONDS = intPreferencesKey("default_duration_seconds")
        private val HEALTH_CONNECT_ENABLED = booleanPreferencesKey("health_connect_enabled")
        
        const val DEFAULT_DURATION_SECONDS_VALUE = 180 // 3 minutes
    }

    val defaultDurationSeconds: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DEFAULT_DURATION_SECONDS] ?: DEFAULT_DURATION_SECONDS_VALUE
        }

    val healthConnectEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HEALTH_CONNECT_ENABLED] ?: false
        }

    suspend fun updateDefaultDurationSeconds(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_DURATION_SECONDS] = seconds
        }
    }

    suspend fun updateHealthConnectEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HEALTH_CONNECT_ENABLED] = enabled
        }
    }
}
