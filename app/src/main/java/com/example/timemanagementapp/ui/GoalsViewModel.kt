package com.example.timemanagementapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

    val totalMinutesInDay = 24 * 60

    //Add a goal to the Goals list
    fun addGoal(goalTitle: String, goalTimeLimitHours: String, goalTimeLimitMinutes: String): Boolean{
        val userHours = goalTimeLimitHours.toIntOrNull() ?: 0
        val userMinutes = goalTimeLimitMinutes.toIntOrNull() ?: 0
        val newGoalMinutes = userHours * 60 + userMinutes


        val currentTotal = _uiState.value.goals.sumOf { it.timeLimit.hours * 60 + it.timeLimit.minutes}

        if(currentTotal + newGoalMinutes > totalMinutesInDay){
            return false
        }

        val newGoal = Goal(
            goalTitle = goalTitle,
            timeLimit = TimeDuration(userHours, userMinutes)
        )
        _uiState.update { currentState ->
            currentState.copy(
                goals = currentState.goals + newGoal
            )
        }
        recalculateTotalMinutes()
        return true
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

    fun deleteGoal(goal: Goal){
        _uiState.update { currentState ->
            currentState.copy(
                goals = currentState.goals - goal
            )
        }
        recalculateTotalMinutes()
    }

    private fun recalculateTotalMinutes(){
        val total = _uiState.value.goals.sumOf { it.timeLimit.hours * 60 + it.timeLimit.minutes }
        val remaining = totalMinutesInDay - total
        _uiState.update { current ->
            current.copy(
                totalMinutesTaken = total,
                remainingMinutesInDay = remaining
            )
        }
    }
}