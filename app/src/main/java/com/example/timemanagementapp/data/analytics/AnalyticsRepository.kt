package com.example.timemanagementapp.data.analytics

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AnalyticsRepository {
    fun getCompletedGoalsCount(startDate: LocalDate, endDate: LocalDate): Flow<Int>
    fun getTotalCompletedMillis(startDate: LocalDate, endDate: LocalDate): Flow<Long>
    fun getTotalScheduledMillisForCompleteGoals(startDate: LocalDate, endDate: LocalDate): Flow<Long>
}