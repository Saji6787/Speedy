package com.example.datausage.data

import com.example.datausage.data.local.DataStoreManager
import com.example.datausage.data.local.HistoryEntry
import com.example.datausage.data.system.NetworkStatsHelper
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class DataUsageRepository(
    private val networkStatsHelper: NetworkStatsHelper,
    private val dataStoreManager: DataStoreManager
) {

    fun getTodayUsageBytes(): Long {
        return networkStatsHelper.getTodayMobileDataUsageBytes()
    }

    val usageHistory: Flow<List<HistoryEntry>> = dataStoreManager.usageHistory
    
    val thresholdMb: Flow<Long> = dataStoreManager.thresholdMb

    suspend fun saveDailyUsage(bytes: Long) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val todayStart = calendar.timeInMillis
        
        dataStoreManager.addHistoryEntry(HistoryEntry(todayStart, bytes))
    }
    
    suspend fun updateThreshold(mb: Long) {
        dataStoreManager.setThreshold(mb)
    }
}
