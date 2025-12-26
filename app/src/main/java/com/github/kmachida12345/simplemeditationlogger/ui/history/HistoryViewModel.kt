package com.github.kmachida12345.simplemeditationlogger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetMeditationHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getMeditationHistoryUseCase: GetMeditationHistoryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            getMeditationHistoryUseCase().collect { sessions ->
                _uiState.value = _uiState.value.copy(
                    sessions = sessions,
                    isLoading = false
                )
            }
        }
    }
}

data class HistoryUiState(
    val sessions: List<MeditationSession> = emptyList(),
    val isLoading: Boolean = true
)
