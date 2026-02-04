package com.example.datausage.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.datausage.App
import com.example.datausage.util.Constants
import kotlinx.coroutines.flow.first
import java.util.Calendar

class DailyUsageWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as App
        val repository = app.repository
        val notificationHelper = app.notificationHelper

        return try {
            // 1. Get current usage
            val todayUsage = repository.getTodayUsageBytes()
            val todayUsageMb = todayUsage / (1024 * 1024)

            // 2. Save/Update history for today
            repository.saveDailyUsage(todayUsage)

            // 3. Check threshold
            val thresholdMb = repository.thresholdMb.first()
            // We want to notify if usage > threshold * 0.8 (warning) or > threshold
            // Logic can be refined to not spam. For now, simple check.
            // A smarter way would be to store "last notified level" in DataStore to avoid repeated alerts.
            // Keeping it simple as per "MVP" instruction.
            
            if (todayUsageMb >= thresholdMb) {
                notificationHelper.showThresholdNotification(todayUsageMb, thresholdMb)
            } else if (todayUsageMb >= thresholdMb * 0.8) {
                 // Optional: Warning at 80%
                 // notificationHelper.showThresholdNotification(todayUsageMb, thresholdMb)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
