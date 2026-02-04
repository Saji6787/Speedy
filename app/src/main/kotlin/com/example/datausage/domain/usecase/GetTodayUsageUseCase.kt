package com.example.datausage.domain.usecase

import com.example.datausage.data.DataUsageRepository
import com.example.datausage.domain.model.DailyUsage
import com.example.datausage.util.formatDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetTodayUsageUseCase(private val repository: DataUsageRepository) {

    // Combining real-time stats with threshold settings to produce UI model
    operator fun invoke(): Flow<DailyUsage> {
        return repository.thresholdMb.map { threshold ->
            val bytes = repository.getTodayUsageBytes()
            val usedMb = (bytes / (1024 * 1024)).toDouble()
            val percent = if (threshold > 0) ((usedMb / threshold) * 100).toInt() else 0
            
            DailyUsage(
                byteUsed = bytes,
                percentUsed = percent,
                formattedDate = System.currentTimeMillis().formatDate()
            )
        }
    }
}
