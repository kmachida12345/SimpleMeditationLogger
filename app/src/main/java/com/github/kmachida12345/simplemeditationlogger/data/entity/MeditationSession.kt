package com.github.kmachida12345.simplemeditationlogger.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Entity(
    tableName = "meditation_sessions",
    indices = [Index(value = ["startTime"])]
)
data class MeditationSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Instant,
    val endTime: Instant,
    val isSyncedToHealthConnect: Boolean = false,
    val healthConnectRecordId: String? = null,
    val syncErrorMessage: String? = null,
    val lastSyncAttemptTime: Instant? = null
)

val MeditationSession.durationMinutes: Int
    get() = ChronoUnit.MINUTES.between(startTime, endTime).toInt()

fun MeditationSession.getLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): java.time.LocalDate {
    return startTime.atZone(zoneId).toLocalDate()
}
