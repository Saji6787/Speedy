package com.example.datausage.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.datausage.data.DataUsageRepository
import com.example.datausage.domain.model.DailyUsage
import com.example.datausage.domain.model.SpeedTestResult
import com.example.datausage.domain.usecase.GetHistoryUseCase
import com.example.datausage.domain.usecase.GetTodayUsageUseCase
import com.example.datausage.domain.usecase.PerformSpeedTestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val dailyUsage: DailyUsage? = null,
    val history: List<com.example.datausage.data.local.HistoryEntry> = emptyList(),
    val thresholdMb: Long = 1024,
    val speedTestResult: SpeedTestResult? = null,
    val isSpeedTestLoading: Boolean = false
)

class HomeViewModel(
    private val repository: DataUsageRepository
) : ViewModel() {

    private val getTodayUsageUseCase = GetTodayUsageUseCase(repository)
    private val getHistoryUseCase = GetHistoryUseCase(repository)
    private val performSpeedTestUseCase = PerformSpeedTestUseCase()

    // Internal state for speed test which isn't a stream from repo
    private val _speedTestState = MutableStateFlow<SpeedTestResult?>(null)
    private val _loadingState = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        getTodayUsageUseCase(),
        getHistoryUseCase(),
        repository.thresholdMb,
        _speedTestState,
        _loadingState
    ) { usage, history, threshold, speed, loading ->
        HomeUiState(
            dailyUsage = usage,
            history = history,
            thresholdMb = threshold,
            speedTestResult = speed,
            isSpeedTestLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun runSpeedTest() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = performSpeedTestUseCase()
            _speedTestState.value = result
            _loadingState.value = false
        }
    }
    
    fun setThreshold(mb: Long) {
        viewModelScope.launch {
            repository.updateThreshold(mb)
        }
    }

    class Factory(private val repository: DataUsageRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
