package com.timelyproductivity.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.ui.components.settings.ReminderEditorUiState
import com.timelyproductivity.app.ui.reminders.ReminderEditorActions
import com.timelyproductivity.app.ui.reminders.ReminderEditorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel(), ReminderEditorActions {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val reminderEditor = ReminderEditorState()
    val reminderEditorUiState = reminderEditor.uiState

    override fun openAddReminderDialog(){
        reminderEditor.openAddReminderDialog()
    }

    override fun openEditReminderDialog(minutes: Int){
        reminderEditor.openEditReminderDialog(minutes)
    }

    override fun updateReminderInput(input: String){
        reminderEditor.updateReminderInput(input)
    }

    override fun deleteReminder(minutes: Int){
        val updatedMinutes =
            settingsUiState.value.countdownRemindersMinutes - minutes

        viewModelScope.launch {
            userPreferencesRepository.setCountdownRemindersMinutes(updatedMinutes)
        }
    }

    override fun closeReminderDialog(){
        reminderEditor.closeReminderDialog()
    }

    override fun saveReminder() {
        val minutes = reminderEditor.validateReminderInput(
            settingsUiState.value.countdownRemindersMinutes
        ) ?: return

        val editorState = reminderEditor.uiState.value
        val updatedMinutes = settingsUiState.value.countdownRemindersMinutes
            .toMutableSet()
            .apply {
                editorState.originalMinutes?.let{originalMinutes ->
                    remove(originalMinutes)
                }

                add(minutes)
            }

        viewModelScope.launch {
            userPreferencesRepository.setCountdownRemindersMinutes(updatedMinutes)
            closeReminderDialog()
        }
    }

    fun setTaskCompletionNotificationsEnabled(enabled: Boolean){
        viewModelScope.launch {
            userPreferencesRepository.setTaskCompletionNotificationsEnabled(enabled)
        }
    }

    fun setCountdownRemindersEnabled(enabled: Boolean){
        viewModelScope.launch {
            userPreferencesRepository.setCountdownRemindersEnabled(enabled)
        }
    }

    fun setTaskNotificationSoundEnabled(enabled: Boolean){
        viewModelScope.launch {
            userPreferencesRepository.setTaskNotificationSoundEnabled(enabled)
        }
    }

    val settingsUiState: StateFlow<SettingsUiState> =
        combine(
            userPreferencesRepository.taskCompletionNotificationsEnabled,
            userPreferencesRepository.countdownRemindersEnabled,
            userPreferencesRepository.countdownRemindersMinutes,
            userPreferencesRepository.taskNotificationSoundEnabled
        ){ completionNotificationsEnabled, countdownRemindersEnabled, countdownRemindersMinutes, taskNotificationSoundEnabled->
            SettingsUiState(
                taskCompletionNotificationsEnabled = completionNotificationsEnabled,
                countdownRemindersEnabled = countdownRemindersEnabled,
                countdownRemindersMinutes = countdownRemindersMinutes,
                taskNotificationSoundEnabled = taskNotificationSoundEnabled
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SettingsUiState()
        )


}

data class SettingsUiState(
    val taskCompletionNotificationsEnabled: Boolean = true,
    val countdownRemindersEnabled: Boolean = false,
    val countdownRemindersMinutes: Set<Int> = setOf(10,5,1),
    val taskNotificationSoundEnabled: Boolean = true,
)