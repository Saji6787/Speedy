package com.example.datausage.domain.usecase

import com.example.datausage.data.DataUsageRepository

class RecordCurrentUsageUseCase(private val repository: DataUsageRepository) {
    suspend operator fun invoke() {
        val currentBytes = repository.getTodayUsageBytes()
        repository.saveDailyUsage(currentBytes)
    }
}
