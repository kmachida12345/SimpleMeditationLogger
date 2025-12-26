package com.github.kmachida12345.simplemeditationlogger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        initializeSettings()
    }
    
    private fun initializeSettings() {
        viewModelScope.launch {
            getAppSettingsUseCase.initialize()
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            getAppSettingsUseCase().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    defaultDurationMinutes = settings?.defaultMeditationMinutes ?: 3,
                    isHealthConnectEnabled = settings?.isHealthConnectEnabled ?: false
                )
            }
        }
    }
    
    fun onStartMeditation() {
        // タイマー画面に遷移（Navigationで処理）
    }
    
    fun onAdjustTime() {
        // 時間設定ダイアログを表示
        _uiState.value = _uiState.value.copy(showTimePickerDialog = true)
    }
    
    fun onDismissTimePickerDialog() {
        _uiState.value = _uiState.value.copy(showTimePickerDialog = false)
    }
    
    fun onTimeSelected(minutes: Int) {
        _uiState.value = _uiState.value.copy(
            defaultDurationMinutes = minutes,
            showTimePickerDialog = false
        )
    }
}

data class HomeUiState(
    val defaultDurationMinutes: Int = 3,
    val isHealthConnectEnabled: Boolean = false,
    val showTimePickerDialog: Boolean = false
)
