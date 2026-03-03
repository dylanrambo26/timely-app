package com.example.timemanagementapp.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalsRepository
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GoalListViewModel(private val goalsRepository: GoalsRepository): ViewModel(){
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val goalListUiState: StateFlow<GoalListUiState> =
        combine(
            goalsRepository.getAllGoalsStream(),
            goalsRepository.getTotalMinutesStream()
        ){ goals, totalMinutes ->
            GoalListUiState(
                goalList = goals,
                totalMinutes = totalMinutes
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = GoalListUiState()
        )

    fun deleteGoal(goal: Goal){
        viewModelScope.launch {
            goalsRepository.deleteGoal(goal)
        }
    }
}

data class GoalListUiState(
    val goalList: List<Goal> = listOf(),
    val totalMinutes: Int = 0,
    val remainingMinutesInDay: Int = MINUTES_IN_24_HOUR_DAY - totalMinutes
)