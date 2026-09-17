package com.example.timemanagementapp.ui.currenttask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.data.alarm.AlarmManagerGoalsRepository
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private var pendingTask: ScheduledGoal? = null

    private val _taskStartState = MutableStateFlow(TaskStartState.IDLE)

    private val currentTask: Flow<ScheduledGoal?> =
        userPreferencesRepository.currentTaskID
            .flatMapLatest { currentTaskId ->
                if (currentTaskId == null){
                    flowOf(null)
                } else {
                    scheduledGoalsRepository.getScheduledGoal(currentTaskId)
                }
            }

    val currentTaskUiState: StateFlow<CurrentTaskUiState> =
        combine(
            currentTask,
            _taskStartState
        ){currentTask, taskStartState ->
            CurrentTaskUiState(
                currentTask = currentTask,
                taskStartState = taskStartState
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CurrentTaskUiState()
        )

    fun startTaskTimer(scheduledGoal: ScheduledGoal){
        if (!alarmManagerGoalsRepository.canScheduleExactAlarms()){
            pendingTask = scheduledGoal
            _taskStartState.value = TaskStartState.WAITING_FOR_ALARM_PERMISSION

            alarmManagerGoalsRepository.requestExactAlarmPermission()
            return
        }

        pendingTask = null
        beginTaskTimer(scheduledGoal)
    }

    private fun beginTaskTimer(scheduledGoal: ScheduledGoal){
        _taskStartState.value = TaskStartState.STARTING

        viewModelScope.launch {
            val currentTask = currentTaskUiState.value.currentTask

            if(currentTask != null && currentTask.scheduledGoalId != scheduledGoal.scheduledGoalId && currentTask.status == GoalStatus.RUNNING){
                Log.d("CurrentTaskViewModel", "Stop Current Task, Switch to new Current Task")
                stopTaskTimer(goalStatus = GoalStatus.PAUSED)
            }

            val updatedScheduledGoal = scheduledGoal.copy(
                startTimeMillis = System.currentTimeMillis(),
                status = GoalStatus.RUNNING
            )

            scheduledGoalsRepository.updateScheduledGoal(updatedScheduledGoal)

            userPreferencesRepository.saveCurrentTaskID(updatedScheduledGoal.scheduledGoalId)

            alarmManagerGoalsRepository.scheduleTimer(updatedScheduledGoal)

            _taskStartState.value = TaskStartState.STARTED
        }
    }

    fun onAppResumed(){
        val task = pendingTask ?: return

        if(alarmManagerGoalsRepository.canScheduleExactAlarms()){
            pendingTask = null
            beginTaskTimer(task)
        } else {
            pendingTask = null
            _taskStartState.value = TaskStartState.IDLE
        }
    }

    fun resetTaskStartState() {
        _taskStartState.value = TaskStartState.IDLE
    }

    suspend fun stopTaskTimer(goalStatus: GoalStatus){
        val currentTask = currentTaskUiState.value.currentTask ?: return

        alarmManagerGoalsRepository.cancelTimer(currentTask.scheduledGoalId)

        val isRunning = currentTask.status == GoalStatus.RUNNING && currentTask.startTimeMillis > 0L

        val sessionMillis = if(isRunning){
            System.currentTimeMillis() - (currentTask.startTimeMillis)
        } else {
            0L
        }

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

    fun needsExactAlarmPermission(): Boolean{
        return !alarmManagerGoalsRepository.canScheduleExactAlarms()
    }
}

data class CurrentTaskUiState(
    val currentTask: ScheduledGoal? = null,
    val taskStartState: TaskStartState = TaskStartState.IDLE
)

enum class TaskStartState{
    IDLE,
    WAITING_FOR_ALARM_PERMISSION,
    STARTING,
    STARTED
}