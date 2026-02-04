package com.example.datausage

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.datausage.data.DataUsageRepository
import com.example.datausage.data.local.DataStoreManager
import com.example.datausage.data.system.NetworkStatsHelper
import com.example.datausage.notification.NotificationHelper
import com.example.datausage.util.Constants
import com.example.datausage.worker.DailyUsageWorker
import java.util.concurrent.TimeUnit

class App : Application() {

    // Simple manual DI
    lateinit var repository: DataUsageRepository
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        
        val networkStatsHelper = NetworkStatsHelper(this)
        val dataStoreManager = DataStoreManager(this)
        repository = DataUsageRepository(networkStatsHelper, dataStoreManager)
        notificationHelper = NotificationHelper(this)
        
        // Setup Notif Channel
        notificationHelper.createNotificationChannel()
        
        // Setup Worker
        setupWorker()
    }
    
    private fun setupWorker() {
        // Constraints: Not Low Battery
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<DailyUsageWorker>(
            Constants.WORK_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(Constants.WORK_TAG_DAILY_USAGE)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            Constants.WORK_TAG_DAILY_USAGE,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing if valid
            periodicWork
        )
    }
}
