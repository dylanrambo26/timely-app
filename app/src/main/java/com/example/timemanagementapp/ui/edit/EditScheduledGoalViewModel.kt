package com.example.timemanagementapp.ui.edit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.goal.GoalsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.createGoal.toGoal
import com.example.timemanagementapp.ui.createGoal.toGoalDetails
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.combine

class EditGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduledGoalsRepository: ScheduledGoalsRepository
) : ViewModel(){

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val scheduledGoalId: Int = checkNotNull(savedStateHandle[EditScheduledGoalDestination.scheduledGoalIdArg])

    private var currentScheduledGoal: ScheduledGoal? = null



    private fun validateInput(uiState: GoalDetails = goalUiState.goalDetails): Boolean {
        val h = uiState.hours.toIntOrNull()
        if (h == null){
            goalUiState = goalUiState.copy(errorMessage = R.string.invalid_hours)
            return false
        }

        val m = uiState.minutes.toIntOrNull()
        if (m == null){
            goalUiState = goalUiState.copy(errorMessage = R.string.invalid_minutes)
            return false
        }

        if(uiState.title.isBlank()){
            goalUiState = goalUiState.copy(errorMessage = R.string.invalid_title)
            return false
        }

        if(h !in 0..23){
            goalUiState = goalUiState.copy(errorMessage = R.string.invalid_hours_0_23)
            return false
        }

        if(m !in 0..59){
            goalUiState = goalUiState.copy(errorMessage = R.string.invalid_minutes_0_59)
            return false
        }

        goalUiState = goalUiState.copy(errorMessage = null)
        return true
    }

    init {
        viewModelScope.launch {
            scheduledGoalsRepository
                .getScheduledGoalWithGoal(scheduledGoalId)
                .filterNotNull()
                .collect { combinedGoal ->
                    val scheduledGoal = combinedGoal.scheduledGoal
                    currentScheduledGoal = scheduledGoal
                    val goal = combinedGoal.goal

                    val details = GoalDetails(
                        id = scheduledGoal.scheduledGoalId,
                        title = scheduledGoal.customTitle ?: goal.goalTitle,
                        hours = (scheduledGoal.customHours ?: goal.hours).toString(),
                        minutes = (scheduledGoal.customMinutes ?: goal.minutes).toString()
                    )

                    goalUiState = GoalUiState(
                        goalDetails = details,
                        isEntryValid = validateInput(details)
                    )
                }
        }
    }
    fun updateUiState(goalDetails: GoalDetails) {
        goalUiState =
            goalUiState.copy(
                goalDetails = goalDetails,
                isEntryValid = validateInput(goalDetails)
            )
    }

    suspend fun updateScheduledGoal(): Boolean {
        if (!validateInput(goalUiState.goalDetails)) return false
        val existingScheduledGoal = currentScheduledGoal ?: return false

        scheduledGoalsRepository.updateScheduledGoal(
            existingScheduledGoal.copy(
                customTitle = goalUiState.goalDetails.title,
                customHours = goalUiState.goalDetails.hours.toIntOrNull(),
                customMinutes = goalUiState.goalDetails.minutes.toIntOrNull()
            )
        )
        return true
    }
}

