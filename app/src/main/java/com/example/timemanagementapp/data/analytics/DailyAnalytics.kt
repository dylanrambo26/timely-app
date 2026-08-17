package com.example.timemanagementapp.data.analytics

import java.time.LocalDate

data class DailyAnalytics(
    val date: LocalDate,
    val completedScheduledMillis: Long,
    val partialMillis: Long,
    val unfinishedMillis: Long
)
