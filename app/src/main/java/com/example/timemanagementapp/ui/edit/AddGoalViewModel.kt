package com.example.timemanagementapp.ui.edit

import android.R
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalsRepository
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY

class AddGoalViewModel(private val goalsRepository: GoalsRepository) : ViewModel(){


    var goalUiState by mutableStateOf(GoalUiState())
        private set


    fun updateUiState(goalDetails: GoalDetails){
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = validateInput(goalDetails))
    }

    fun clearUiState(){
        goalUiState =
            goalUiState.copy(goalDetails = GoalDetails(), isEntryValid = false, errorMessage = "")
    }

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
        }

        val newMinutes = h * 60 + m

        if(newMinutes > goalUiState.remainingMinutesInDay){
            goalUiState = goalUiState.copy(errorMessage = "The goal's allotted time must be less than the remaining time in the day. Delete or edit the other goals before saving this goal.")
        }
        goalUiState = goalUiState.copy(errorMessage = "")
        return true
    }

    suspend fun saveGoal(){
        if(validateInput()){
            goalsRepository.insertGoal(goalUiState.goalDetails.toGoal())
        }
    }
}


data class GoalUiState(
    val goalDetails: GoalDetails = GoalDetails(),
    val isEntryValid: Boolean = false,
    val remainingMinutesInDay: Int = MINUTES_IN_24_HOUR_DAY,
    val errorMessage: String = ""
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