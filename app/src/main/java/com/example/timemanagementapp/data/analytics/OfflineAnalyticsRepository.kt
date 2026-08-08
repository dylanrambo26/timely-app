package com.example.timemanagementapp.data.analytics

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineAnalyticsRepository(
    private val analyticsDao: AnalyticsDao
): AnalyticsRepository {
    override fun getCompletedGoalsCount(startDate: LocalDate, endDate: LocalDate): Flow<Int> = analyticsDao.getCompletedGoalsCount(startDate, endDate)

    override fun getTotalCompletedMillis(startDate: LocalDate, endDate: LocalDate): Flow<Long> = analyticsDao.getTotalCompletedMillis(startDate, endDate)
}