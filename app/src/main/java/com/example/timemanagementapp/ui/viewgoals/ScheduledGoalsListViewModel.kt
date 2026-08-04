package com.example.timemanagementapp.ui.viewgoals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduledGoalsListViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val eventIdFromRoute: Int? = savedStateHandle[ViewGoalsDestination.eventIdArg]
    var calendarEventId: Int = 0
        private set
    private val _calendarEventId = MutableStateFlow<Int?>(null)
    private val _date = MutableStateFlow<LocalDate?>(null)
    private val _showDurationError = MutableStateFlow(false)
    val showDurationError = _showDurationError.asStateFlow()

    init{
        viewModelScope.launch {
            calendarEventId = eventIdFromRoute ?: calendarEventsRepository.getOrCreateEventIdForDate(LocalDate.now())

            _calendarEventId.value = calendarEventId
            _date.value = calendarEventsRepository.getEventById(calendarEventId)?.date
        }
    }

    val scheduledGoalsListUiState =
        _calendarEventId
            .filterNotNull()
            .flatMapLatest { eventId ->
                scheduledGoalsRepository.getScheduledGoals(eventId)
                .map{ scheduledGoals ->
                    val totalMinutes = scheduledGoals.sumOf {
                        val hours = it.scheduledHours
                        val minutes = it.scheduledMinutes

                        hours * 60 + minutes
                    }

                    ScheduledGoalsListUiState(
                        scheduledGoalsList = scheduledGoals,
                        calendarEventId = eventId,
                        date = _date.value,
                        totalMinutes = totalMinutes,
                        remainingMinutesInDay = MINUTES_IN_24_HOUR_DAY - totalMinutes
                    )
                }
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

    suspend fun addScheduledGoalFromExistingGoal(
        goalId: Int,
        onNavigate: (Int) -> Unit = {}
    ){
        val success = scheduledGoalsRepository.validInsertScheduledGoal(
            goalId = goalId,
            eventId = calendarEventId
        )

        _showDurationError.value = !success

        if(success){
            onNavigate(calendarEventId)
        }
    }

    fun isPastDate(): Boolean{
        return _date.value?.isBefore(LocalDate.now()) ?: false
    }

    fun viewPreviousDay(onNavigate: (Int) -> Unit){
        viewModelScope.launch {
            val currentDate = scheduledGoalsListUiState.value.date ?: return@launch

            val previousDate = currentDate.minusDays(1)

            val previousEventId = calendarEventsRepository.getOrCreateEventIdForDate(previousDate)

            onNavigate(previousEventId)
        }
    }

    fun viewNextDay(onNavigate: (Int) -> Unit){
        viewModelScope.launch {
            val currentDate = scheduledGoalsListUiState.value.date ?: return@launch

            val nextDate = currentDate.plusDays(1)

            val previousEventId = calendarEventsRepository.getOrCreateEventIdForDate(nextDate)

            onNavigate(previousEventId)
        }
    }
}

data class ScheduledGoalsListUiState(
    val scheduledGoalsList: List<ScheduledGoal> = emptyList(),
    val calendarEventId: Int? = null,
    val date: LocalDate? = null,
    val totalMinutes: Int = 0,
    val remainingMinutesInDay: Int = MINUTES_IN_24_HOUR_DAY - totalMinutes,
    val remainingTimeError: Boolean = false
)