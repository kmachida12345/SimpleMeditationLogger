package com.github.kmachida12345.simplemeditationlogger.data.database

import androidx.room.Database
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
}
