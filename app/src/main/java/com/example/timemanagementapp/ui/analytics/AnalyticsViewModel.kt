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

                val startDate = when(selectedPeriod){
                    AnalyticsTimePeriod.WEEKLY ->
                        today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    AnalyticsTimePeriod.MONTHLY -> today.withDayOfMonth(1)
                    AnalyticsTimePeriod.YEARLY -> today.withDayOfYear(1)
                }

                //Only used for display on Analytics Screen, not for analytics queries since future scheduled goals could affect averages for being incomplete
                val displayEndDate = when(selectedPeriod){
                    AnalyticsTimePeriod.WEEKLY ->
                        today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                    AnalyticsTimePeriod.MONTHLY ->
                        today.withDayOfMonth(today.lengthOfMonth())

                    AnalyticsTimePeriod.YEARLY ->
                        today.withDayOfYear(today.lengthOfYear())
                }
                //The queries will take today only as the end date because of possible incompleted future goals
                combine(
                    analyticsRepository.getCompletedGoalsCount(
                        startDate = startDate,
                        endDate = today
                    ),
                    analyticsRepository.getTotalCompletedMillis(
                        startDate = startDate,
                        endDate = today
                    )
                ){
                    completedGoalCount, totalCompletedMillis ->
                    AnalyticsUiState(
                        selectedPeriod = selectedPeriod,
                        completedGoalCount = completedGoalCount,
                        totalCompletedMillis = totalCompletedMillis,
                        startDate = startDate,
                        endDate = displayEndDate
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

data class AnalyticsUiState(
    val selectedPeriod: AnalyticsTimePeriod = AnalyticsTimePeriod.WEEKLY,
    val startDate: LocalDate = LocalDate.now().minusDays(6),
    val endDate: LocalDate = LocalDate.now(),
    val completedGoalCount: Int = 0,
    val totalCompletedMillis: Long = 0L
)

enum class AnalyticsTimePeriod{
    WEEKLY,
    MONTHLY,
    YEARLY
}