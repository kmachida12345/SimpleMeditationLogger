package com.github.kmachida12345.simplemeditationlogger.di

import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.AppSettingsRepositoryImpl
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindMeditationSessionRepository(
        impl: MeditationSessionRepositoryImpl
    ): MeditationSessionRepository
    
    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(
        impl: AppSettingsRepositoryImpl
    ): AppSettingsRepository
}
