package com.github.kmachida12345.simplemeditationlogger.ui.countdown

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.EndMeditationSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CountdownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val endMeditationSessionUseCase: EndMeditationSessionUseCase
) : ViewModel() {
    
    private val durationMinutes: Int = savedStateHandle["durationMinutes"] ?: 3
    
    private val _uiState = MutableStateFlow(CountdownUiState())
    val uiState: StateFlow<CountdownUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    private var startTime: Instant? = null
    private var endTime: Instant? = null
    private var pausedAtSeconds: Int = 0
    
    init {
        startTimer()
    }
    
    private fun startTimer() {
        if (startTime == null) {
            startTime = Instant.now()
            endTime = startTime!!.plusSeconds(durationMinutes * 60L)
        } else if (pausedAtSeconds > 0) {
            // 一時停止から再開
            endTime = Instant.now().plusSeconds(pausedAtSeconds.toLong())
        }
        
        val targetEndTime = endTime ?: return
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (Instant.now().isBefore(targetEndTime)) {
                val remaining = Duration.between(Instant.now(), targetEndTime).seconds
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
            _uiState.value = _uiState.value.copy(isPaused = true)
        }
    }
    
    fun onEnd() {
        timerJob?.cancel()
        saveMeditationSession()
    }
    
    private fun onTimerComplete() {
        saveMeditationSession()
    }
    
    private fun saveMeditationSession() {
        viewModelScope.launch {
            val start = startTime ?: return@launch
            val end = Instant.now()
            
            endMeditationSessionUseCase(start, end)
                .onSuccess { savedSession ->
                    // 実際の経過時間（分）を計算
                    val actualDurationMinutes = ((end.epochSecond - start.epochSecond) / 60).toInt()
                    _uiState.value = _uiState.value.copy(
                        isCompleted = true,
                        actualDurationMinutes = actualDurationMinutes
                    )
                }
                .onFailure { error ->
                    // エラー時も完了扱いにするが、ログは記録
                    android.util.Log.e("CountdownViewModel", "Failed to save session", error)
                    val actualDurationMinutes = ((end.epochSecond - start.epochSecond) / 60).toInt()
                    _uiState.value = _uiState.value.copy(
                        isCompleted = true,
                        actualDurationMinutes = actualDurationMinutes,
                        error = "Failed to save session"
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
    val actualDurationMinutes: Int = 0,
    val error: String? = null
)
