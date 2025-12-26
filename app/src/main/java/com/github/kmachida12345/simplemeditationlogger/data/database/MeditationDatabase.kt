package com.github.kmachida12345.simplemeditationlogger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.kmachida12345.simplemeditationlogger.data.dao.AppSettingsDao
import com.github.kmachida12345.simplemeditationlogger.data.dao.MeditationSessionDao
import com.github.kmachida12345.simplemeditationlogger.data.entity.AppSettings
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession

@Database(
    entities = [MeditationSession::class, AppSettings::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MeditationDatabase : RoomDatabase() {
    abstract fun meditationSessionDao(): MeditationSessionDao
    abstract fun appSettingsDao(): AppSettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: MeditationDatabase? = null
        
        // TODO: Replace with Hilt dependency injection
        fun getDatabase(context: Context): MeditationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeditationDatabase::class.java,
                    "meditation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
