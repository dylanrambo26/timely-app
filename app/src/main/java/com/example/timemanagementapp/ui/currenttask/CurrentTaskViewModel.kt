package com.example.timemanagementapp.ui.currenttask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.data.alarm.AlarmManagerGoalsRepository
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalWithGoal
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
                    scheduledGoalsRepository.getScheduledGoalWithGoal(currentTaskId)
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

    fun startTaskTimer(scheduledGoalWithGoal: ScheduledGoalWithGoal){
        viewModelScope.launch {
            val currentTask = currentTaskUiState.value.currentTask

            //Only stop the timer if the previous task was still running and is a different task than the incoming task
            //This is used when the user changes the task while the task is running
            if(currentTask != null && currentTask.scheduledGoal.scheduledGoalId != scheduledGoalWithGoal.scheduledGoal.scheduledGoalId && currentTask.scheduledGoal.status == GoalStatus.RUNNING){
                stopTaskTimer()
            }

            val startTime = System.currentTimeMillis()

            val updatedScheduledGoal = scheduledGoalWithGoal.scheduledGoal.copy(
                startTimeMillis = startTime,
                status = GoalStatus.RUNNING
            )

            val updatedScheduledGoalWithGoal = scheduledGoalWithGoal.copy(
                scheduledGoal = updatedScheduledGoal
            )

            scheduledGoalsRepository.updateScheduledGoal(updatedScheduledGoal)

            userPreferencesRepository.saveCurrentTaskID(updatedScheduledGoal.scheduledGoalId)

            alarmManagerGoalsRepository.scheduleTimer(updatedScheduledGoalWithGoal)
        }
    }

    suspend fun stopTaskTimer(){
        val currentTask = currentTaskUiState.value.currentTask ?: return

        if(currentTask.scheduledGoal.status == GoalStatus.COMPLETED) return

        if (currentTask.scheduledGoal.startTimeMillis <= 0L) {
            Log.e(
                "Timer",
                "Invalid startTimeMillis: ${currentTask.scheduledGoal.startTimeMillis}"
            )
            return
        }

        val sessionMillis = System.currentTimeMillis() - (currentTask.scheduledGoal.startTimeMillis)

        scheduledGoalsRepository.updateScheduledGoal(
            currentTask.scheduledGoal.copy(
                completedMillis = currentTask.scheduledGoal.completedMillis + sessionMillis,
                startTimeMillis = 0L,
                status = GoalStatus.PAUSED
            )
        )

        alarmManagerGoalsRepository.cancelTimer(currentTask.scheduledGoal.scheduledGoalId)


    }

    fun pauseTask(){
        viewModelScope.launch {
            stopTaskTimer()
        }
    }
}

data class CurrentTaskUiState(
    val currentTask: ScheduledGoalWithGoal? = null
)