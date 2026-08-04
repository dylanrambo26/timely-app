package com.example.timemanagementapp.ui.currenttask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.data.alarm.AlarmManagerGoalsRepository
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrentTaskViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    //private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
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
                    scheduledGoalsRepository.getScheduledGoal(currentTaskId)
                        .map { combinedGoal ->
                            CurrentTaskUiState(
                                currentTask = combinedGoal
                            )
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CurrentTaskUiState()
            )

    fun startTaskTimer(scheduledGoal: ScheduledGoal){
        viewModelScope.launch {
            val currentTask = currentTaskUiState.value.currentTask

            //Only stop the timer if the previous task was still running and is a different task than the incoming task
            //This is used when the user changes the task while the task is running
            if(currentTask != null && currentTask.scheduledGoalId != scheduledGoal.scheduledGoalId && currentTask.status == GoalStatus.RUNNING){
                stopTaskTimer(goalStatus = GoalStatus.PAUSED)
            }

            val startTime = System.currentTimeMillis()

            val updatedScheduledGoal = scheduledGoal.copy(
                startTimeMillis = startTime,
                status = GoalStatus.RUNNING
            )

            scheduledGoalsRepository.updateScheduledGoal(updatedScheduledGoal)

            userPreferencesRepository.saveCurrentTaskID(updatedScheduledGoal.scheduledGoalId)

            alarmManagerGoalsRepository.scheduleTimer(updatedScheduledGoal)
        }
    }

    suspend fun stopTaskTimer(goalStatus: GoalStatus){
        val currentTask = currentTaskUiState.value.currentTask ?: return

        alarmManagerGoalsRepository.cancelTimer(currentTask.scheduledGoalId)

        if(currentTask.status == GoalStatus.COMPLETED) return

        if (currentTask.startTimeMillis <= 0L) {
            Log.e(
                "Timer",
                "Invalid startTimeMillis: ${currentTask.startTimeMillis}"
            )
            return
        }

        val sessionMillis = System.currentTimeMillis() - (currentTask.startTimeMillis)

        scheduledGoalsRepository.updateScheduledGoal(
            currentTask.copy(
                completedMillis = currentTask.completedMillis + sessionMillis,
                startTimeMillis = 0L,
                status = goalStatus
            )
        )
    }

    fun pauseTask(){
        viewModelScope.launch {
            stopTaskTimer(goalStatus = GoalStatus.PAUSED)
        }
    }

    fun markAsComplete(){
        viewModelScope.launch {
            stopTaskTimer(goalStatus = GoalStatus.COMPLETED)
        }
    }
}

data class CurrentTaskUiState(
    val currentTask: ScheduledGoal? = null
)