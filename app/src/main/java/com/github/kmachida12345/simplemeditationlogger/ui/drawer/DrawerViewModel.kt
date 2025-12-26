package com.github.kmachida12345.simplemeditationlogger.ui.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetAppSettingsUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.UpdateAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateAppSettingsUseCase: UpdateAppSettingsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            getAppSettingsUseCase().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    defaultMeditationMinutes = settings?.defaultMeditationMinutes ?: 3,
                    isHealthConnectEnabled = settings?.isHealthConnectEnabled ?: false
                )
            }
        }
    }
    
    fun onHealthConnectToggle(enabled: Boolean) {
        viewModelScope.launch {
            updateAppSettingsUseCase.updateHealthConnectEnabled(enabled)
        }
    }
}

data class DrawerUiState(
    val defaultMeditationMinutes: Int = 3,
    val isHealthConnectEnabled: Boolean = false
)
