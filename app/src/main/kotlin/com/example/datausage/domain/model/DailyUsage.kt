package com.example.datausage.domain.model

data class DailyUsage(
    val byteUsed: Long,
    val percentUsed: Int,
    val formattedDate: String
)
