package com.github.kmachida12345.simplemeditationlogger.data.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.MindfulnessSessionRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val PERMISSIONS =
        setOf(
            HealthPermission.getWritePermission(MindfulnessSessionRecord::class)
        )

    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    suspend fun hasPermissions(): Boolean {
        return try {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            PERMISSIONS.all { it in granted }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    fun createPermissionRequestContract() =
        PermissionController.createRequestPermissionResultContract()

    suspend fun writeMeditationSession(
        startTime: Instant,
        endTime: Instant
    ): Result<String> {
        return try {
            if (!isAvailable()) {
                return Result.failure(IllegalStateException("Health Connect is not available"))
            }

            if (!hasPermissions()) {
                return Result.failure(SecurityException("Missing Health Connect permissions"))
            }

            val zoneRules = ZoneId.systemDefault().rules

            val mindfulnessSessionRecord = MindfulnessSessionRecord(
                startTime = startTime,
                startZoneOffset = zoneRules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = zoneRules.getOffset(endTime),
                mindfulnessSessionType = MindfulnessSessionRecord.MINDFULNESS_SESSION_TYPE_MEDITATION,
                title = null,
                notes = null,
                metadata = Metadata.activelyRecorded(
                    clientRecordId = "meditation_${startTime.toEpochMilli()}",
                    clientRecordVersion = 1L,
                    device = Device(type = Device.TYPE_PHONE)
                )
            )
            
            val response = healthConnectClient.insertRecords(
                listOf(mindfulnessSessionRecord)
            )

            val recordId = response.recordIdsList.firstOrNull()
                ?: return Result.failure(IllegalStateException("Health Connect returned no record ID"))
            Result.success(recordId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
