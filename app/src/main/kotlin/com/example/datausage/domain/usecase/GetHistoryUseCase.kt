package com.example.datausage.domain.usecase

import com.example.datausage.data.DataUsageRepository
import com.example.datausage.data.local.HistoryEntry
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(private val repository: DataUsageRepository) {
    operator fun invoke(): Flow<List<HistoryEntry>> {
        return repository.usageHistory
    }
}
