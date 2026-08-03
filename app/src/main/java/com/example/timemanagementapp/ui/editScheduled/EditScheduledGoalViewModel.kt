package com.example.timemanagementapp.ui.editScheduled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.createGoal.toGoal
import com.example.timemanagementapp.util.validate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class EditScheduledGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduledGoalsRepository: ScheduledGoalsRepository
) : ViewModel(){

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val scheduledGoalId: Int = checkNotNull(savedStateHandle[EditScheduledGoalDestination.scheduledGoalIdArg])

    private var currentScheduledGoal: ScheduledGoal? = null

    private val _calendarEventId = MutableStateFlow<Int?>(null)
    val calendarEventId: StateFlow<Int?> = _calendarEventId.asStateFlow()

    /*private fun validateInput(uiState: GoalDetails): Int? {
        val h = uiState.hours.toIntOrNull() ?: return R.string.invalid_hours
        val m = uiState.minutes.toIntOrNull() ?: return R.string.invalid_minutes

        if (uiState.title.isBlank()) return R.string.invalid_title
        if (h !in 0..23) return R.string.invalid_hours_0_23
        if (m !in 0..59) return R.string.invalid_minutes_0_59

        return null
    }*/

    init {
        viewModelScope.launch {
            scheduledGoalsRepository
                .getScheduledGoal(scheduledGoalId)
                .filterNotNull()
                .collect { scheduledGoal ->
                    currentScheduledGoal = scheduledGoal
                    _calendarEventId.value = scheduledGoal.eventId

                    val details = GoalDetails(
                        id = scheduledGoal.scheduledGoalId,
                        title = scheduledGoal.scheduledGoalTitle,
                        hours = scheduledGoal.scheduledHours.toString(),
                        minutes = scheduledGoal.scheduledMinutes.toString()
                    )

                    goalUiState = GoalUiState(
                        goalDetails = details,
                        isEntryValid = goalUiState.goalDetails.validate() == null
                    )
                }
        }
    }
    fun updateUiState(goalDetails: GoalDetails) {
        val error = goalUiState.goalDetails.validate()
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    suspend fun updateScheduledGoal(onNavigate: (Int) -> Unit = {}) {
        val error = goalUiState.goalDetails.validate()
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val existingScheduledGoal = currentScheduledGoal ?: return

        val goal = goalUiState.goalDetails.toGoal()

        val goalTotalMinutes = goal.hours * 60 + goal.minutes

        val eventId = existingScheduledGoal.eventId
        val isValidDuration = scheduledGoalsRepository.isValidDurationForDate(
            goalTotalMinutes = goalTotalMinutes,
            eventId = eventId,
            excludedScheduledGoalId = existingScheduledGoal.scheduledGoalId
        )

        if(!isValidDuration){
            goalUiState = goalUiState.copy(
                errorMessage = R.string.selected_goal_exceeds_remaining_time,
                isEntryValid = false
            )
            return
        }

        scheduledGoalsRepository.updateScheduledGoal(
            existingScheduledGoal.copy(
                scheduledGoalTitle = goalUiState.goalDetails.title,
                scheduledHours = goalUiState.goalDetails.hours.toInt(),
                scheduledMinutes = goalUiState.goalDetails.minutes.toInt(),
                isCustomized = true
            )
        )

        onNavigate(existingScheduledGoal.eventId)
    }
}

