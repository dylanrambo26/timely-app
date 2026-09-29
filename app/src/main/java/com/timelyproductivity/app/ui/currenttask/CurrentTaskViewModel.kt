package com.timelyproductivity.app.ui.currenttask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.alarm.AlarmManagerGoalsRepository
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import com.timelyproductivity.app.ui.reminders.ReminderEditorActions
import com.timelyproductivity.app.ui.reminders.ReminderEditorState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrentTaskViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    //private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val alarmManagerGoalsRepository: AlarmManagerGoalsRepository
): ViewModel(), ReminderEditorActions{
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private var pendingTask: ScheduledGoal? = null

    private val _taskStartState = MutableStateFlow(TaskStartState.IDLE)
    private val _selectedCountdownReminders = MutableStateFlow<Set<Int>?>(null)
    private val _selectedGoal = MutableStateFlow<ScheduledGoal?>(null)

    val reminderEditor = ReminderEditorState()
    val reminderEditorUiState = reminderEditor.uiState

    private val _isCustomizingReminders = MutableStateFlow(false)

    private val currentTask: Flow<ScheduledGoal?> =
        userPreferencesRepository.currentTaskID
            .flatMapLatest { currentTaskId ->
                if (currentTaskId == null){
                    flowOf(null)
                } else {
                    scheduledGoalsRepository.getScheduledGoal(currentTaskId)
                }
            }

    private val taskState =
        combine(
            currentTask,
            _taskStartState
        ){currentTask, taskStartState ->
            currentTask to taskStartState
        }

    private val reminderState =
        combine(
            userPreferencesRepository.defaultCountdownRemindersMinutes,
            _selectedCountdownReminders
        ){defaults, override ->
            defaults to override
        }

    private val selectionState =
        combine(
            _selectedGoal,
            _isCustomizingReminders
        ){ selectedGoal, isCustomizing ->
            selectedGoal to isCustomizing
        }

    val currentTaskUiState: StateFlow<CurrentTaskUiState> =
        combine(
            taskState,
            reminderState
        ){task, reminders->
            CurrentTaskUiState(
                currentTask = task.first,
                taskStartState = task.second,
                countdownReminders = reminders.second ?: reminders.first
            )
        }.combine(
            selectionState
        ){ uiState, selection ->
            uiState.copy(
                selectedGoal = selection.first,
                isCustomizingReminders = selection.second
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

            alarmManagerGoalsRepository.scheduleCompletionAlarm(updatedScheduledGoal)

            val reminders = currentTaskUiState.value.countdownReminders
            userPreferencesRepository.setCurrentTaskCountdownRemindersMinutes(
                reminders
            )

            if(reminders.isNotEmpty()){
                alarmManagerGoalsRepository.scheduleCountdownReminders(
                    scheduledGoal = updatedScheduledGoal,
                    reminderMinutes = reminders
                )
            }

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
        val reminders = userPreferencesRepository.currentTaskCountdownRemindersMinutes.first()

        val currentTask = currentTaskUiState.value.currentTask ?: return

        alarmManagerGoalsRepository.cancelCompletionAlarm(currentTask.scheduledGoalId)

        if(reminders.isNotEmpty()){
            alarmManagerGoalsRepository.cancelCountdownReminders(
                scheduledGoalId = currentTask.scheduledGoalId,
                reminderMinutes = reminders
            )
        }

        userPreferencesRepository.setCurrentTaskCountdownRemindersMinutes(emptySet())

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

    fun selectGoal(scheduledGoal: ScheduledGoal){
        _selectedGoal.value = scheduledGoal
    }

    override fun openAddReminderDialog() {
        reminderEditor.openAddReminderDialog()
    }

    override fun openEditReminderDialog(minutes: Int) {
        reminderEditor.openEditReminderDialog(minutes)
    }

    override fun closeReminderDialog() {
        reminderEditor.closeReminderDialog()
    }

    override fun updateReminderInput(input: String) {
        reminderEditor.updateReminderInput(input)
    }

    override fun saveReminder() {
        val selectedGoalMinutes = _selectedGoal.value?.let { goal ->
            goal.scheduledHours * 60 + goal.scheduledMinutes
        }

        val minutes = reminderEditor.validateReminderInput(
            currentTaskUiState.value.countdownReminders,
            goalMinutes = selectedGoalMinutes
        ) ?: return

        val editorState = reminderEditor.uiState.value

        val updatedMinutes = currentTaskUiState.value.countdownReminders
            .toMutableSet()
            .apply {
                editorState.originalMinutes?.let{originalMinutes ->
                    remove(originalMinutes)
                }

                add(minutes)
            }

        _selectedCountdownReminders.value = updatedMinutes
        closeReminderDialog()
    }

    override fun deleteReminder(minutes: Int) {
        val updatedMinutes =
            currentTaskUiState.value.countdownReminders - minutes

        _selectedCountdownReminders.value = updatedMinutes
    }

    fun customizeReminder(){
        _isCustomizingReminders.value = true
    }

    fun finishCustomizingReminders(){
        _isCustomizingReminders.value = false
    }
}

data class CurrentTaskUiState(
    val currentTask: ScheduledGoal? = null,
    val selectedGoal: ScheduledGoal? = null,
    val taskStartState: TaskStartState = TaskStartState.IDLE,
    val countdownReminders: Set<Int> = emptySet(),
    val isCustomizingReminders: Boolean = false
)

enum class TaskStartState{
    IDLE,
    WAITING_FOR_ALARM_PERMISSION,
    STARTING,
    STARTED
}