package com.github.kmachida12345.simplemeditationlogger.ui.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.data.healthconnect.HealthConnectManager
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
    private val updateHealthConnectEnabledUseCase: UpdateHealthConnectEnabledUseCase,
    private val healthConnectManager: HealthConnectManager
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
        
        // Health Connectの利用可能性をチェック
        viewModelScope.launch {
            val available = healthConnectManager.isAvailable()
            _uiState.value = _uiState.value.copy(
                isHealthConnectAvailable = available
            )
        }
    }
    
    fun onDefaultTimeClick() {
        _uiState.value = _uiState.value.copy(showTimePickerDialog = true)
    }
    
    fun onHealthConnectToggle(enabled: Boolean) {
        if (enabled) {
            // ONにする場合はパーミッションチェック
            viewModelScope.launch {
                val hasPermission = healthConnectManager.hasPermissions()
                if (hasPermission) {
                    // すでにパーミッションがある場合は有効化
                    updateHealthConnectEnabledUseCase(true)
                } else {
                    // パーミッションがない場合はリクエストを要求
                    _uiState.value = _uiState.value.copy(
                        shouldRequestHealthConnectPermission = true
                    )
                }
            }
        } else {
            // OFFにする場合は即座に無効化
            viewModelScope.launch {
                updateHealthConnectEnabledUseCase(false)
            }
        }
    }
    
    fun onPermissionRequestHandled() {
        _uiState.value = _uiState.value.copy(
            shouldRequestHealthConnectPermission = false
        )
    }
    
    fun onPermissionGranted() {
        viewModelScope.launch {
            updateHealthConnectEnabledUseCase(true)
        }
    }
    
    fun onPermissionDenied() {
        // パーミッションが拒否された場合は何もしない（トグルはOFFのまま）
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
    val isHealthConnectAvailable: Boolean = false,
    val showTimePickerDialog: Boolean = false,
    val shouldRequestHealthConnectPermission: Boolean = false
)
