package com.example.timemanagementapp.ui.currenttask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalsRepository
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.data.AlarmManagerGoalsRepository
import com.example.timemanagementapp.data.GoalStatus
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
    private val alarmManagerGoalsRepository: AlarmManagerGoalsRepository
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

    fun startTaskTimer(goal: Goal){
        viewModelScope.launch {
            if(userPreferencesRepository.getCurrentTaskID() != null){
                stopTaskTimer()
            }

            val startTime = System.currentTimeMillis()

            val updatedGoal = goal.copy(
                startTimeMillis = startTime,
                status = GoalStatus.RUNNING
            )
            goalsRepository.updateGoal(updatedGoal)

            userPreferencesRepository.saveCurrentTaskID(updatedGoal.goalID)

            alarmManagerGoalsRepository.scheduleTimer(updatedGoal)
        }
    }

    suspend fun stopTaskTimer(){
        val currentTask = currentTaskUiState.value.currentTask ?: return
        val recentCompletedMillis = System.currentTimeMillis() - (currentTask.startTimeMillis)
        goalsRepository.updateGoal(
            currentTask.copy(
                completedMillis = currentTask.completedMillis + recentCompletedMillis,
                startTimeMillis = 0L,
                status = GoalStatus.PAUSED
            )
        )

        alarmManagerGoalsRepository.cancelTimer(currentTask.goalID)
    }

    fun pauseTask(){
        viewModelScope.launch {
            stopTaskTimer()
        }
    }
}

data class CurrentTaskUiState(
    val currentTask: Goal? = null
)