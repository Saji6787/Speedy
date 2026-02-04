package com.example.datausage.data.system

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import java.util.Calendar

class NetworkStatsHelper(private val context: Context) {

    private val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    fun getTodayMobileDataUsageBytes(): Long {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        // Reset time to beginning of the day (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        return try {
            // Needed to get subscriberId for accurate mobile data tracking sometimes, 
            // but passing null invokes usage for all mobile networks which is usually what we want for total.
            // On Android 10+ accessing subscriberId needs precise permission, but null is valid for aggregation.
            val bucket = networkStatsManager.querySummaryForDevice(
                NetworkCapabilities.TRANSPORT_CELLULAR,
                null, // Subscriber ID (null for all)
                startTime,
                endTime
            )
            
            bucket.rxBytes + bucket.txBytes
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}
