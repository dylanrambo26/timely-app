package com.example.timemanagementapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalsUiState
import com.example.timemanagementapp.data.TimeDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Extends ViewModel() and provides a custom uiState for modifying time management goals
 */
class GoalsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState(goals = listOf()))
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
    var userNewHours by mutableStateOf("")
        private set

    var userNewMinutes by mutableStateOf("")
        private set

    var userNewGoalTitle by mutableStateOf("")
        private set

    //Add a goal to the Goals list
    fun addGoal(goalTitle: String, goalTimeLimitHours: String, goalTimeLimitMinutes: String){
        val userHours = goalTimeLimitHours.toIntOrNull() ?: 0
        val userMinutes = goalTimeLimitMinutes.toIntOrNull() ?: 0

        val newGoal = Goal(
            goalTitle = goalTitle,
            timeLimit = TimeDuration(userHours, userMinutes)
        )
        _uiState.update { currentState ->
            currentState.copy(
                goals = currentState.goals + newGoal
            )
        }
    }

    //Update the user's new hours field
    fun updateNewUserHours(userHours: String){
        userNewHours = userHours
    }

    //Update the user's new minutes field
    fun updateNewUserMinutes(userMinutes: String){
        userNewMinutes = userMinutes
    }

    //Update the user's new goal title field
    fun updateNewUserGoalTitle(userGoalTitle: String){
        userNewGoalTitle = userGoalTitle
    }

    //Clear the current Goal fields
    fun cleanGoalFields(){
        updateNewUserHours("")
        updateNewUserMinutes("")
        updateNewUserGoalTitle("")
    }
}