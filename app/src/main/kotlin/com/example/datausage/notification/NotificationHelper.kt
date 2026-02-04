package com.example.datausage.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.datausage.MainActivity
import com.example.datausage.R
import com.example.datausage.util.Constants

class NotificationHelper(private val context: Context) {

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Data Usage Monitor"
            val descriptionText = "Notifications for data usage thresholds and daily summary"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showThresholdNotification(usageMb: Long, thresholdMb: Long) {
        if (checkNotificationPermission()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Using default for now, should replace with app icon
                .setContentTitle("Data Usage Alert")
                .setContentText("You have used $usageMb MB of your $thresholdMb MB daily limit.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            try {
                // Using NotificationManager directly if compat issue arises, but Core KTX is usually fine
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constants.NOTIFICATION_ID_DAILY, builder.build())
            } catch (e: SecurityException) {
                // Should not happen if permission checked, but safety first
                e.printStackTrace()
            }
        }
    }

    private fun checkNotificationPermission(): Boolean {
        // In a real app we would check Manifest permission and runtime permission for Tiramisu+
        // For now assuming granted or fail silently as per lightweight requirement (user enables in settings if missed)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        return true
    }
}
