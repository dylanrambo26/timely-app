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
        val h = uiState.hours.toIntOrNull() ?: return false
        val m = uiState.minutes.toIntOrNull() ?: return false
        if(uiState.title.isBlank()) return false
        if(h < 0) return false
        if(m !in 0..59) return false

        val newMinutes = h * 60 + m
        return newMinutes <= goalUiState.remainingMinutesInDay
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

    suspend fun updateGoal() {
        if(validateInput(goalUiState.goalDetails)){
            goalsRepository.updateGoal(goalUiState.goalDetails.toGoal())
        }
    }
}

