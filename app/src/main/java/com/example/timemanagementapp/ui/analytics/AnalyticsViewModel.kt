package com.example.timemanagementapp.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.analytics.AnalyticsRepository
import com.example.timemanagementapp.ui.currenttask.CurrentTaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

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
                    AnalyticsTimePeriod.WEEKLY -> today.minusDays(6)
                    AnalyticsTimePeriod.MONTHLY -> today.withDayOfMonth(1)
                    AnalyticsTimePeriod.YEARLY -> today.withDayOfYear(1)
                }
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
                        totalCompletedMillis = totalCompletedMillis
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
    val completedGoalCount: Int = 0,
    val totalCompletedMillis: Long = 0L
)

enum class AnalyticsTimePeriod{
    WEEKLY,
    MONTHLY,
    YEARLY
}