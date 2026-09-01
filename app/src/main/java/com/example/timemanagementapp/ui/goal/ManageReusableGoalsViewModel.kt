package com.example.timemanagementapp.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageReusableGoalsViewModel(
    private val goalsRepository: GoalsRepository
): ViewModel(){
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val goalListUiState: StateFlow<GoalListUiState> =
        goalsRepository.getAllGoalsWithRecurrence()
            .map { goals ->
                GoalListUiState(goalList = goals)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = GoalListUiState()
            )

    fun deleteGoalAndScheduledGoals(goal: Goal){
        viewModelScope.launch {
            goalsRepository.deleteGoalAndScheduledGoals(goal)
        }
    }

    fun deleteGoalKeepScheduledGoals(goal: Goal){
        viewModelScope.launch {
            goalsRepository.deleteGoal(goal)
        }
    }
}