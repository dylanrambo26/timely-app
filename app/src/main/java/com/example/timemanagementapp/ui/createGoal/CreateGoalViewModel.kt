package com.example.timemanagementapp.ui.createGoal

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalsRepository
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.ui.viewgoals.ViewGoalsDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CreateGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
) : ViewModel(){

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    val calendarEventId: Int = checkNotNull(savedStateHandle[ViewGoalsDestination.eventIdArg])
    private val _date = MutableStateFlow<LocalDate?>(null)
    val date: StateFlow<LocalDate?> = _date.asStateFlow()

    init{
        viewModelScope.launch {
            _date.value = calendarEventsRepository.getEventById(calendarEventId)?.date
        }
    }

    fun updateUiState(goalDetails: GoalDetails){
        val error = validateInput(goalDetails)
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    fun clearUiState(){
        goalUiState =
            goalUiState.copy(goalDetails = GoalDetails(), isEntryValid = false, errorMessage = null)
    }

    private fun validateInput(uiState: GoalDetails): Int? {
        val h = uiState.hours.toIntOrNull() ?: return R.string.invalid_hours
        val m = uiState.minutes.toIntOrNull() ?: return R.string.invalid_minutes

        if (uiState.title.isBlank()) return R.string.invalid_title
        if (h !in 0..23) return R.string.invalid_hours_0_23
        if (m !in 0..59) return R.string.invalid_minutes_0_59

        return null
    }

    suspend fun saveGoal(){

        val error = validateInput(goalUiState.goalDetails)
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        goalsRepository.insertGoal(goalUiState.goalDetails.toGoal())
    }

    suspend fun saveGoalAndAddToDate(onNavigate: (Int) -> Unit = {}){
        val error = validateInput(goalUiState.goalDetails)
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val goal = goalUiState.goalDetails.toGoal()
        val goalTotalMinutes = goal.hours * 60 + goal.minutes

        val isValidDuration = scheduledGoalsRepository.isValidDurationForDate(
            goalTotalMinutes = goalTotalMinutes,
            eventId = calendarEventId,
            excludedScheduledGoalId = null
        )

        if(!isValidDuration){
            goalUiState = goalUiState.copy(
                errorMessage = R.string.selected_goal_exceeds_remaining_time,
                isEntryValid = false
            )
            return
        }

        val goalId = goalsRepository.insertGoal(goal)

        scheduledGoalsRepository.insertScheduledGoal(
            ScheduledGoal(
                goalId = goalId,
                eventId = calendarEventId
            )
        )

        onNavigate(calendarEventId)

    }
}


data class GoalUiState(
    val goalDetails: GoalDetails = GoalDetails(),
    val isEntryValid: Boolean = false,
    val errorMessage: Int? = null
)

data class GoalDetails(
    val id: Int = 0,
    val title: String = "",
    val hours: String = "",
    val minutes: String = ""
)

fun GoalDetails.toGoal(): Goal = Goal(
    goalID = id,
    goalTitle = title,
    hours = hours.toIntOrNull() ?: 0,
    minutes = minutes.toIntOrNull() ?: 0,
)

fun Goal.toGoalUiState(isEntryValid: Boolean = false): GoalUiState = GoalUiState(
    goalDetails = this.toGoalDetails(),
    isEntryValid = isEntryValid
)

fun Goal.toGoalDetails(): GoalDetails = GoalDetails(
    id = goalID,
    title = goalTitle,
    hours = hours.toString(),
    minutes = minutes.toString()
)