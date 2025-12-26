package com.github.kmachida12345.simplemeditationlogger.data.dao

import androidx.room.*
import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettings?>
    
    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettingsSync(): AppSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)
    
    @Update
    suspend fun updateSettings(settings: AppSettings)
}
