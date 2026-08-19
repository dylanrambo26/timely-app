package com.example.timemanagementapp.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.analytics.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class AnalyticsViewModel(
    val analyticsRepository: AnalyticsRepository
): ViewModel()
{
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val selectedPeriod = MutableStateFlow(AnalyticsTimePeriod.WEEKLY)

    val analyticsUiState: StateFlow<AnalyticsUiState> =
        selectedPeriod
            .flatMapLatest { selectedPeriod ->
                val today = LocalDate.now()

                //displayEndDate only used for display on Analytics Screen, not for analytics queries since future scheduled goals could affect averages for being incomplete
                val (startDate, displayEndDate) = when(selectedPeriod){
                    AnalyticsTimePeriod.WEEKLY ->
                        today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)) to
                                today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                    AnalyticsTimePeriod.MONTHLY ->
                        today.withDayOfMonth(1) to today.withDayOfMonth(today.lengthOfMonth())

                    AnalyticsTimePeriod.YEARLY ->
                        today.withDayOfYear(1) to today.withDayOfYear(today.lengthOfYear())
                }

                val gridOffset = startDate.dayOfWeek.value % 7

                val completedStatsFlow = combine(
                    analyticsRepository.getCompletedGoalsCount(startDate, today),
                    analyticsRepository.getTotalCompletedMillis(startDate, today),
                    analyticsRepository.getTotalScheduledMillisForCompleteGoals(startDate, today)
                ){completedGoalCount, totalCompletedMillis, totalScheduledMillis ->
                    CompletedStats(
                        completedGoalCount = completedGoalCount,
                        totalCompletedMillis = totalCompletedMillis,
                        totalScheduledMillis = totalScheduledMillis
                    )
                }

                val dailyAnalyticsFlow =
                    analyticsRepository.getDailyAnalytics(
                        startDate = startDate,
                        endDate = today
                    )

                //The queries will take today only as the end date because of possible incompleted future goals
                combine(
                    completedStatsFlow,
                    dailyAnalyticsFlow
                ){ completedStats, dailyAnalytics ->

                    //Calculates average completed millis per task
                    val averageCompletedMillis = if (completedStats.completedGoalCount > 0){
                        completedStats.totalCompletedMillis / completedStats.completedGoalCount
                    } else {
                        0L
                    }

                    //Calculates how well user utilizes their completed time out of the scheduled time
                    val scheduledTimeUtilization = if(completedStats.totalScheduledMillis > 0){
                        completedStats.totalCompletedMillis.toDouble() / completedStats.totalScheduledMillis * 100
                    } else {
                        0.0
                    }

                    val completedScheduledMillis = dailyAnalytics.sumOf {it.completedScheduledMillis}
                    val partialMillis = dailyAnalytics.sumOf {it.partialMillis}
                    val unfinishedMillis = dailyAnalytics.sumOf {it.unfinishedMillis}

                    val percentageMillisSum = completedScheduledMillis + partialMillis + unfinishedMillis

                    val completedPercentage: Float
                    val partialPercentage: Float
                    val unfinishedPercentage: Float

                    if (percentageMillisSum > 0){
                        completedPercentage = completedScheduledMillis.toFloat() / percentageMillisSum * 100
                        partialPercentage = partialMillis.toFloat() / percentageMillisSum * 100
                        unfinishedPercentage = unfinishedMillis.toFloat() / percentageMillisSum * 100
                    } else {
                        completedPercentage = 0f
                        partialPercentage = 0f
                        unfinishedPercentage = 0f
                    }

                    val dailyActivity = dailyAnalytics.map {day ->
                        val partialAndCompletedMillis = day.completedScheduledMillis + day.partialMillis

                        val dailyPercentage = if (percentageMillisSum > 0){
                            partialAndCompletedMillis.toFloat() / percentageMillisSum * 100
                        } else {
                            0f
                        }

                        DailyActivity(
                            date = day.date,
                            completionPercentage = dailyPercentage
                        )
                    }

                    AnalyticsUiState(
                        selectedPeriod = selectedPeriod,
                        completedGoalCount = completedStats.completedGoalCount,
                        totalCompletedMillis = completedStats.totalCompletedMillis,
                        startDate = startDate,
                        endDate = displayEndDate,
                        averageCompletedMillis = averageCompletedMillis,
                        scheduledTimeUtilization = scheduledTimeUtilization,
                        completedPercentage = completedPercentage,
                        partialPercentage = partialPercentage,
                        unfinishedPercentage = unfinishedPercentage,
                        gridOffset = gridOffset,
                        dailyActivity = dailyActivity
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalyticsUiState()
            )

    fun updatePeriod(period: AnalyticsTimePeriod){
        selectedPeriod.value = period
    }
}

data class CompletedStats(
    val completedGoalCount: Int,
    val totalCompletedMillis: Long,
    val totalScheduledMillis: Long
)

/*data class ProgressStats(
    val partialMillis: Long,
    val unfinishedMillis: Long,
    val completedScheduledMillis: Long
)*/

data class DailyActivity(
    val date: LocalDate,
    val completionPercentage: Float,
)

data class AnalyticsUiState(
    val selectedPeriod: AnalyticsTimePeriod = AnalyticsTimePeriod.WEEKLY,
    val startDate: LocalDate = LocalDate.now().minusDays(6),
    val endDate: LocalDate = LocalDate.now(),
    val completedGoalCount: Int = 0,
    val totalCompletedMillis: Long = 0L,
    val averageCompletedMillis: Long = 0L,
    val scheduledTimeUtilization: Double = 0.0,
    val completedPercentage: Float = 0f,
    val partialPercentage: Float = 0f,
    val unfinishedPercentage: Float = 0f,
    val gridOffset: Int = 0,
    val dailyActivity: List<DailyActivity> = listOf()
)

enum class AnalyticsTimePeriod{
    WEEKLY,
    MONTHLY,
    YEARLY
}