package com.github.kmachida12345.simplemeditationlogger.ui.countdown

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.data.device.VibrationHelper
import com.github.kmachida12345.simplemeditationlogger.domain.model.HealthConnectSyncStatus
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.EndMeditationSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CountdownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val endMeditationSessionUseCase: EndMeditationSessionUseCase,
    private val vibrationHelper: VibrationHelper,
    private val clock: Clock
) : ViewModel() {

    private val durationSeconds: Int = savedStateHandle["durationSeconds"] ?: 180

    private val _uiState = MutableStateFlow(CountdownUiState())
    val uiState: StateFlow<CountdownUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Instant? = null
    private var endTime: Instant? = null
    private var pausedAtSeconds: Int = 0
    private var pauseStartedAt: Instant? = null
    private var accumulatedPause: Duration = Duration.ZERO
    private var saveRequested = false

    init {
        startTimer()
    }

    private fun startTimer() {
        if (startTime == null) {
            startTime = Instant.now(clock)
            endTime = startTime!!.plusSeconds(durationSeconds.toLong())
        } else if (pausedAtSeconds > 0) {
            // 一時停止から再開: 停止していた時間を積算し、締切を停止分だけ後ろへずらす
            pauseStartedAt?.let {
                accumulatedPause += Duration.between(it, Instant.now(clock))
                pauseStartedAt = null
            }
            endTime = Instant.now(clock).plusSeconds(pausedAtSeconds.toLong())
        }

        val targetEndTime = endTime ?: return

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (Instant.now(clock).isBefore(targetEndTime)) {
                val remaining = Duration.between(Instant.now(clock), targetEndTime).seconds
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = remaining.toInt().coerceAtLeast(0),
                    isPaused = false
                )
                delay(1.seconds)
            }
            // タイマー終了
            _uiState.value = _uiState.value.copy(remainingSeconds = 0)
            onTimerComplete()
        }
    }

    fun onPauseResume() {
        if (_uiState.value.isPaused) {
            startTimer()
        } else {
            timerJob?.cancel()
            pausedAtSeconds = _uiState.value.remainingSeconds
            pauseStartedAt = Instant.now(clock)
            _uiState.value = _uiState.value.copy(isPaused = true)
        }
    }

    fun onEnd() {
        timerJob?.cancel()
        saveMeditationSession()
    }

    private fun onTimerComplete() {
        vibrationHelper.vibrateOnCompletion()
        saveMeditationSession()
    }

    private fun saveMeditationSession() {
        // タイマー満了と終了ボタンの競合による二重保存を防ぐ(両呼び出し元はメインディスパッチャ)
        if (saveRequested) return
        saveRequested = true

        viewModelScope.launch {
            val start = startTime ?: return@launch
            val now = Instant.now(clock)
            // 一時停止中の時間は瞑想時間から除外する(停止したまま終了した場合も含む)
            val pausedTotal = accumulatedPause +
                (pauseStartedAt?.let { Duration.between(it, now) } ?: Duration.ZERO)
            val meditated = (Duration.between(start, now) - pausedTotal)
                .let { if (it.isNegative) Duration.ZERO else it }
            val recordEndTime = start.plus(meditated)

            endMeditationSessionUseCase(start, recordEndTime)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isCompleted = true,
                        actualDurationSeconds = meditated.seconds.toInt(),
                        sessionSaved = true,
                        syncStatus = result.syncStatus
                    )
                }
                .onFailure { error ->
                    Log.e("CountdownViewModel", "Failed to save session", error)
                    _uiState.value = _uiState.value.copy(
                        isCompleted = true,
                        actualDurationSeconds = meditated.seconds.toInt(),
                        sessionSaved = false
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class CountdownUiState(
    val remainingSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val actualDurationSeconds: Int = 0,
    val sessionSaved: Boolean = true,
    val syncStatus: HealthConnectSyncStatus = HealthConnectSyncStatus.DISABLED
)
