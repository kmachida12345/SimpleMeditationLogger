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
    private var remainingSeconds: Int = durationMinutes * 60
    
    init {
        startTimer()
    }
    
    private fun startTimer() {
        if (startTime == null) {
            startTime = Instant.now()
        }
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (remainingSeconds > 0) {
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = remainingSeconds,
                    isPaused = false
                )
                delay(1.seconds)
                remainingSeconds--
            }
            // タイマー終了
            onTimerComplete()
        }
    }
    
    fun onPauseResume() {
        if (_uiState.value.isPaused) {
            startTimer()
        } else {
            timerJob?.cancel()
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
            
            endMeditationSessionUseCase(start, end).onSuccess {
                _uiState.value = _uiState.value.copy(isCompleted = true)
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
    val isCompleted: Boolean = false
)
