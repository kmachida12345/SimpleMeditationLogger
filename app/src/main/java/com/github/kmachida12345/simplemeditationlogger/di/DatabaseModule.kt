package com.github.kmachida12345.simplemeditationlogger.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.kmachida12345.simplemeditationlogger.data.dao.AppSettingsDao
import com.github.kmachida12345.simplemeditationlogger.data.dao.MeditationSessionDao
import com.github.kmachida12345.simplemeditationlogger.data.database.MeditationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Update default meditation time from 15 to 3 minutes
            db.execSQL("UPDATE app_settings SET defaultMeditationMinutes = 3 WHERE id = 1")
        }
    }
    
    @Provides
    @Singleton
    fun provideMeditationDatabase(
        @ApplicationContext context: Context
    ): MeditationDatabase {
        return Room.databaseBuilder(
            context,
            MeditationDatabase::class.java,
            "meditation_database"
        )
        .addMigrations(MIGRATION_1_2)
        .build()
    }
    
    @Provides
    fun provideMeditationSessionDao(
        database: MeditationDatabase
    ): MeditationSessionDao {
        return database.meditationSessionDao()
    }
    
    @Provides
    fun provideAppSettingsDao(
        database: MeditationDatabase
    ): AppSettingsDao {
        return database.appSettingsDao()
    }
}
