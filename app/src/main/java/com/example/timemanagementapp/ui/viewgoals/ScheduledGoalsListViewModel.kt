package com.example.timemanagementapp.ui.viewgoals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.calendar.CalendarEvent
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalWithGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduledGoalsListViewModel(
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val selectedDate = MutableStateFlow(LocalDate.now())

    val scheduledGoalsListUiState: StateFlow<ScheduledGoalsListUiState> =
        selectedDate
            .flatMapLatest { date: LocalDate ->
                calendarEventsRepository.getEventByDateFlow(date)
                    .flatMapLatest { event: CalendarEvent? ->
                        if (event == null) {
                            flowOf(emptyList())
                        } else {
                            scheduledGoalsRepository.getScheduledGoalsWithGoal(
                                event.eventId
                            )
                        }
                    }
            }
            .map { scheduledGoals: List<ScheduledGoalWithGoal> ->
                ScheduledGoalsListUiState(
                    scheduledGoalsList = scheduledGoals,
                    totalMinutes = scheduledGoals.sumOf {
                        it.goal.hours * 60 + it.goal.minutes
                    }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ScheduledGoalsListUiState()
            )

    fun deleteScheduledGoal(scheduledGoal: ScheduledGoal){
        viewModelScope.launch {
            scheduledGoalsRepository.deleteScheduledGoal(scheduledGoal = scheduledGoal)
        }
    }
}

data class ScheduledGoalsListUiState(
    val scheduledGoalsList: List<ScheduledGoalWithGoal> = emptyList(),
    val totalMinutes: Int = 0,
    val remainingMinutesInDay: Int = MINUTES_IN_24_HOUR_DAY - totalMinutes
)