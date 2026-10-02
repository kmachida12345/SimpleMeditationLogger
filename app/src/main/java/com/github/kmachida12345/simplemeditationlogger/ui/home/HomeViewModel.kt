package com.github.kmachida12345.simplemeditationlogger.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetDefaultDurationUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.GetHealthConnectEnabledUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.SyncToHealthConnectUseCase
import com.github.kmachida12345.simplemeditationlogger.domain.usecase.UpdateDefaultDurationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDefaultDurationUseCase: GetDefaultDurationUseCase,
    private val getHealthConnectEnabledUseCase: GetHealthConnectEnabledUseCase,
    private val updateDefaultDurationUseCase: UpdateDefaultDurationUseCase,
    private val syncToHealthConnectUseCase: SyncToHealthConnectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getDefaultDurationUseCase().collect { seconds ->
                _uiState.value = _uiState.value.copy(
                    defaultDurationSeconds = seconds
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

        // 同期に失敗したままのセッションをアプリ起動時にリトライする
        viewModelScope.launch {
            if (getHealthConnectEnabledUseCase().first()) {
                syncToHealthConnectUseCase.syncAllUnsynced()
                    .onSuccess { result ->
                        if (result.totalCount > 0) {
                            Log.d(
                                "HomeViewModel",
                                "Health Connect retry: ${result.successCount}/${result.totalCount} synced"
                            )
                        }
                    }
                    .onFailure { error ->
                        Log.w("HomeViewModel", "Health Connect retry sync failed", error)
                    }
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
    
    fun onTimeSelected(seconds: Int) {
        viewModelScope.launch {
            updateDefaultDurationUseCase(seconds)
            _uiState.value = _uiState.value.copy(
                showTimePickerDialog = false
            )
        }
    }
}

data class HomeUiState(
    val defaultDurationSeconds: Int = 180,
    val isHealthConnectEnabled: Boolean = false,
    val showTimePickerDialog: Boolean = false
)
