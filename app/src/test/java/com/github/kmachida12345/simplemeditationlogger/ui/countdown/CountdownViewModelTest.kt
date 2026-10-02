package com.github.kmachida12345.simplemeditationlogger.ui.countdown

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.github.kmachida12345.simplemeditationlogger.data.device.VibrationHelper
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.domain.model.HealthConnectSyncStatus
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.EndMeditationSessionUseCase
import com.github.kmachida12345.simplemeditationlogger.testutil.MainDispatcherRule
import com.github.kmachida12345.simplemeditationlogger.testutil.MutableClock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class CountdownViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val startInstant = Instant.parse("2026-01-01T00:00:00Z")

    private val endMeditationSessionUseCase = mockk<EndMeditationSessionUseCase>()
    private val vibrationHelper = mockk<VibrationHelper>(relaxed = true)
    private lateinit var clock: MutableClock

    @Before
    fun setup() {
        clock = MutableClock(startInstant)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        stubSaveSuccess(HealthConnectSyncStatus.DISABLED)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private fun createViewModel(durationSeconds: Int = 600): CountdownViewModel {
        return CountdownViewModel(
            savedStateHandle = SavedStateHandle(mapOf("durationSeconds" to durationSeconds)),
            endMeditationSessionUseCase = endMeditationSessionUseCase,
            vibrationHelper = vibrationHelper,
            clock = clock
        )
    }

    private fun stubSaveSuccess(syncStatus: HealthConnectSyncStatus) {
        coEvery { endMeditationSessionUseCase.invoke(any(), any()) } answers {
            val start = firstArg<Instant>()
            val end = secondArg<Instant>()
            Result.success(
                EndMeditationSessionUseCase.EndSessionResult(
                    session = MeditationSession(id = 1, startTime = start, endTime = end),
                    syncStatus = syncStatus
                )
            )
        }
    }

    @Test
    fun `onEnd twice saves session only once`() = runTest {
        // Given
        val viewModel = createViewModel()
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(60))

        // When
        viewModel.onEnd()
        viewModel.onEnd()
        runCurrent()

        // Then
        coVerify(exactly = 1) { endMeditationSessionUseCase.invoke(any(), any()) }
    }

    @Test
    fun `paused time is excluded from recorded duration`() = runTest {
        // Given: 60秒瞑想 → 300秒一時停止 → 30秒瞑想
        val viewModel = createViewModel()
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(60))
        viewModel.onPauseResume() // pause
        clock.advanceBy(Duration.ofSeconds(300))
        viewModel.onPauseResume() // resume
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(30))

        // When
        viewModel.onEnd()
        runCurrent()

        // Then: 記録は開始時刻+実瞑想時間(90秒)
        coVerify(exactly = 1) {
            endMeditationSessionUseCase.invoke(startInstant, startInstant.plusSeconds(90))
        }
        assertEquals(90, viewModel.uiState.value.actualDurationSeconds)
    }

    @Test
    fun `ending while paused excludes in-progress pause`() = runTest {
        // Given: 60秒瞑想 → 120秒一時停止したまま終了
        val viewModel = createViewModel()
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(60))
        viewModel.onPauseResume() // pause
        clock.advanceBy(Duration.ofSeconds(120))

        // When
        viewModel.onEnd()
        runCurrent()

        // Then
        coVerify(exactly = 1) {
            endMeditationSessionUseCase.invoke(startInstant, startInstant.plusSeconds(60))
        }
        assertEquals(60, viewModel.uiState.value.actualDurationSeconds)
    }

    @Test
    fun `sync status is propagated to ui state`() = runTest {
        // Given
        stubSaveSuccess(HealthConnectSyncStatus.SYNCED)
        val viewModel = createViewModel()
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(60))

        // When
        viewModel.onEnd()
        runCurrent()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isCompleted)
        assertTrue(state.sessionSaved)
        assertEquals(HealthConnectSyncStatus.SYNCED, state.syncStatus)
    }

    @Test
    fun `save failure is reported in ui state`() = runTest {
        // Given
        coEvery { endMeditationSessionUseCase.invoke(any(), any()) } returns
            Result.failure(RuntimeException("db error"))
        val viewModel = createViewModel()
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(60))

        // When
        viewModel.onEnd()
        runCurrent()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isCompleted)
        assertFalse(state.sessionSaved)
        assertEquals(60, state.actualDurationSeconds)
    }

    @Test
    fun `natural completion vibrates and saves once`() = runTest {
        // Given: 2秒タイマー
        val viewModel = createViewModel(durationSeconds = 2)
        runCurrent()

        // When: 時計を満了後まで進め、tickループを再開させる
        clock.advanceBy(Duration.ofSeconds(3))
        advanceTimeBy(1100)
        runCurrent()

        // Then
        verify(exactly = 1) { vibrationHelper.vibrateOnCompletion() }
        coVerify(exactly = 1) { endMeditationSessionUseCase.invoke(any(), any()) }
        assertTrue(viewModel.uiState.value.isCompleted)
        assertEquals(0, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun `manual end does not vibrate`() = runTest {
        // Given
        val viewModel = createViewModel()
        runCurrent()
        clock.advanceBy(Duration.ofSeconds(60))

        // When
        viewModel.onEnd()
        runCurrent()

        // Then
        verify(exactly = 0) { vibrationHelper.vibrateOnCompletion() }
    }
}
