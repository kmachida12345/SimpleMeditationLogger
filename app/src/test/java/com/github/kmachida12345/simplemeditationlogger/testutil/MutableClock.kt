package com.github.kmachida12345.simplemeditationlogger.testutil

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

/** テストから任意に進められる固定クロック */
class MutableClock(
    private var current: Instant,
    private val zone: ZoneId = ZoneOffset.UTC
) : Clock() {

    override fun getZone(): ZoneId = zone

    override fun withZone(zone: ZoneId): Clock = MutableClock(current, zone)

    override fun instant(): Instant = current

    fun advanceBy(duration: Duration) {
        current = current.plus(duration)
    }
}
