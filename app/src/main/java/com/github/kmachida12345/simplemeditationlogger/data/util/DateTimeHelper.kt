package com.github.kmachida12345.simplemeditationlogger.data.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

object DateTimeHelper {
    fun getStartOfDay(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Instant {
        return date.atStartOfDay(zoneId).toInstant()
    }
    
    fun getEndOfDay(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Instant {
        return date.plusDays(1).atStartOfDay(zoneId).toInstant()
    }
    
    fun getRetryThreshold(minutesAgo: Long = 5): Instant {
        return Instant.now().minusSeconds(minutesAgo * 60)
    }
    
    fun formatSessionTime(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val zdt = ZonedDateTime.ofInstant(instant, zoneId)
        return "${zdt.year}年${zdt.monthValue}月${zdt.dayOfMonth}日"
    }
}
