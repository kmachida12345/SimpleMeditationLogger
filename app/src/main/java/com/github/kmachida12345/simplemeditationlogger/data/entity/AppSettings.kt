package com.github.kmachida12345.simplemeditationlogger.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 1,
    val defaultMeditationMinutes: Int = 15,
    val isHealthConnectEnabled: Boolean = false
)
