package com.example.timemanagementapp.ui.currenttask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalsRepository
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.data.WorkManagerGoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrentTaskViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val goalsRepository: GoalsRepository,
    private val workManagerGoalsRepository: WorkManagerGoalsRepository
): ViewModel(){
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val currentTaskUiState: StateFlow<CurrentTaskUiState> =
        userPreferencesRepository.currentTaskID
            .flatMapLatest { currentTaskId ->

                if (currentTaskId == null) {
                    flowOf(CurrentTaskUiState())
                } else {
                    goalsRepository.getGoalStream(currentTaskId)
                        .map { goal ->
                            CurrentTaskUiState(
                                currentTask = goal
                            )
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CurrentTaskUiState()
            )

    /*fun setCurrentTask(goalId: Int){
        viewModelScope.launch {
            userPreferencesRepository.saveCurrentTaskID(goalId)
        }
    }*/
    fun startTaskTimer(goal: Goal){
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            goalsRepository.updateGoal(
                goal.copy(
                    startTimeMillis = startTime
                )
            )

            userPreferencesRepository.saveCurrentTaskID(goal.goalID)

            workManagerGoalsRepository.scheduleTimer(goal)
        }
    }

    fun stopTask(){

    }
}

data class CurrentTaskUiState(
    val currentTask: Goal? = null
)