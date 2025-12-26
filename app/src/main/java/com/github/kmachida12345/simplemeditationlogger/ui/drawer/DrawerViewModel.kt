package com.github.kmachida12345.simplemeditationlogger.ui.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetDefaultDurationUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetHealthConnectEnabledUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.UpdateDefaultDurationUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.UpdateHealthConnectEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val getDefaultDurationUseCase: GetDefaultDurationUseCase,
    private val getHealthConnectEnabledUseCase: GetHealthConnectEnabledUseCase,
    private val updateDefaultDurationUseCase: UpdateDefaultDurationUseCase,
    private val updateHealthConnectEnabledUseCase: UpdateHealthConnectEnabledUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            getDefaultDurationUseCase().collect { seconds ->
                _uiState.value = _uiState.value.copy(
                    defaultMeditationSeconds = seconds
                )
            }
        }
        
        viewModelScope.launch {
            getHealthConnectEnabledUseCase().collect { enabled ->
                _uiState.value = _uiState.value.copy(
                    isHealthConnectEnabled = enabled
                )
            }
        }
    }
    
    fun onDefaultTimeClick() {
        _uiState.value = _uiState.value.copy(showTimePickerDialog = true)
    }
    
    fun onHealthConnectToggle(enabled: Boolean) {
        viewModelScope.launch {
            updateHealthConnectEnabledUseCase(enabled)
        }
    }
    
    fun onDismissTimePickerDialog() {
        _uiState.value = _uiState.value.copy(showTimePickerDialog = false)
    }
    
    fun onTimeSelected(seconds: Int) {
        viewModelScope.launch {
            updateDefaultDurationUseCase(seconds)
            _uiState.value = _uiState.value.copy(
                showTimePickerDialog = false
            )
        }
    }
}

data class DrawerUiState(
    val defaultMeditationSeconds: Int = 180,
    val isHealthConnectEnabled: Boolean = false,
    val showTimePickerDialog: Boolean = false
)
