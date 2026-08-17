package com.example.timemanagementapp.data.analytics

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineAnalyticsRepository(
    private val analyticsDao: AnalyticsDao
): AnalyticsRepository {
    override fun getCompletedGoalsCount(startDate: LocalDate, endDate: LocalDate): Flow<Int> = analyticsDao.getCompletedGoalsCount(startDate, endDate)

    override fun getTotalCompletedMillis(startDate: LocalDate, endDate: LocalDate): Flow<Long> = analyticsDao.getTotalCompletedMillis(startDate, endDate)

    override fun getTotalScheduledMillisForCompleteGoals(startDate: LocalDate, endDate: LocalDate): Flow<Long>  = analyticsDao.getTotalScheduledMillisForCompleteGoals(startDate, endDate)

    override fun getPartialCompletedMillis(startDate: LocalDate, endDate: LocalDate): Flow<Long> = analyticsDao.getPartialCompletedMillis(startDate, endDate)

    override fun getUnfinishedMillis(startDate: LocalDate, endDate: LocalDate): Flow<Long> = analyticsDao.getUnfinishedMillis(startDate, endDate)

    override fun getCompletedScheduledMillis(startDate: LocalDate, endDate: LocalDate): Flow<Long> = analyticsDao.getCompletedScheduledMillis(startDate, endDate)

    override fun getDailyAnalytics(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyAnalytics>> = analyticsDao.getDailyAnalytics(startDate, endDate)
}