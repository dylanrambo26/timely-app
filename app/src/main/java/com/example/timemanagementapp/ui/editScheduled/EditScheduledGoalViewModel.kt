package com.example.timemanagementapp.ui.editScheduled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.goal.GoalStatus
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

    init {
        viewModelScope.launch {
            scheduledGoalsRepository
                .getScheduledGoal(scheduledGoalId)
                .filterNotNull()
                .collect { scheduledGoal ->
                    currentScheduledGoal = scheduledGoal
                    _calendarEventId.value = scheduledGoal.eventId

                    val isCurrentOrPaused =
                        (scheduledGoal.status == GoalStatus.RUNNING) || (scheduledGoal.status == GoalStatus.PAUSED)

                    val details = GoalDetails(
                        id = scheduledGoal.scheduledGoalId,
                        title = scheduledGoal.scheduledGoalTitle,
                        hours = scheduledGoal.scheduledHours.toString(),
                        minutes = scheduledGoal.scheduledMinutes.toString()
                    )

                    goalUiState = GoalUiState(
                        goalDetails = details,
                        isEntryValid = details.validate() == null,
                        isDurationEditable = !isCurrentOrPaused
                    )
                }
        }
    }
    fun updateUiState(goalDetails: GoalDetails) {
        val error = goalDetails.validate()
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
        val isDurationLocked = existingScheduledGoal.status == GoalStatus.RUNNING || existingScheduledGoal.status == GoalStatus.PAUSED
        if(!isDurationLocked){
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
        }

        val updatedHours = if(isDurationLocked){
            existingScheduledGoal.scheduledHours
        } else {
            goalUiState.goalDetails.hours.toInt()
        }

        val updatedMinutes = if(isDurationLocked){
            existingScheduledGoal.scheduledMinutes
        } else {
            goalUiState.goalDetails.minutes.toInt()
        }

        scheduledGoalsRepository.updateScheduledGoal(
            existingScheduledGoal.copy(
                scheduledGoalTitle = goalUiState.goalDetails.title,
                scheduledHours = updatedHours,
                scheduledMinutes = updatedMinutes,
                isCustomized = true
            )
        )

        onNavigate(existingScheduledGoal.eventId)
    }
}

