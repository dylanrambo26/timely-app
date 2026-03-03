package com.example.timemanagementapp.ui.edit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.GoalsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.combine

class EditGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository
) : ViewModel(){

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val goalId: Int = checkNotNull(savedStateHandle[EditGoalDestination.goalIdArg])

    private fun validateInput(uiState: GoalDetails = goalUiState.goalDetails): Boolean {
        val h = uiState.hours.toIntOrNull()
        if (h == null){
            goalUiState = goalUiState.copy(errorMessage = "Hours must be a valid number.")
            return false
        }

        val m = uiState.minutes.toIntOrNull()
        if (m == null){
            goalUiState = goalUiState.copy(errorMessage = "Minutes must be a valid number.")
            return false
        }

        if(uiState.title.isBlank()){
            goalUiState = goalUiState.copy(errorMessage = "Title cannot be empty.")
            return false
        }

        if(h < 0){
            goalUiState = goalUiState.copy(errorMessage = "Hours cannot be a negative number.")
            return false
        }

        if(m !in 0..59){
            goalUiState = goalUiState.copy(errorMessage = "Minutes must be between 0 and 59.")
            return false
        }

        val newMinutes = h * 60 + m

        if(newMinutes > goalUiState.remainingMinutesInDay){
            goalUiState = goalUiState.copy(errorMessage = "The goal's allotted time must be less than the remaining time in the day. Delete or edit the other goals before saving this goal.")
            return false
        }
        goalUiState = goalUiState.copy(errorMessage = "")
        return true
    }

    init {
        viewModelScope.launch {
            combine(
                goalsRepository.getGoalStream(goalId).filterNotNull(),
                goalsRepository.getTotalMinutesStream()
            ){goal, totalMinutes ->
                val details = goal.toGoalDetails()

                val currentGoalMinutes = goal.hours * 60 + goal.minutes
                val remainingExcludingCurrentGoal = MINUTES_IN_24_HOUR_DAY - (totalMinutes - currentGoalMinutes)

                GoalUiState(
                    goalDetails = details,
                    remainingMinutesInDay = remainingExcludingCurrentGoal,
                    isEntryValid = validateInput(details)
                )
            }.collect { combinedState ->
                goalUiState = combinedState
            }

        }
    }

    fun updateUiState(goalDetails: GoalDetails){
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = validateInput(goalDetails))
    }

    suspend fun updateGoal(): Boolean{
        if(!validateInput(goalUiState.goalDetails)) return false
        goalsRepository.updateGoal(goalUiState.goalDetails.toGoal())
        return true
    }
}

