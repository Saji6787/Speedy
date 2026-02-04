package com.example.datausage.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatBytes(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.US, "%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return sdf.format(Date(this))
}
