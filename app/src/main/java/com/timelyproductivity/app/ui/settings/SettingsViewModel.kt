package com.timelyproductivity.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.ui.reminders.ReminderEditorActions
import com.timelyproductivity.app.ui.reminders.ReminderEditorState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
            userPreferencesRepository.setDefaultCountdownRemindersMinutes(updatedMinutes)
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
            userPreferencesRepository.setDefaultCountdownRemindersMinutes(updatedMinutes)
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

    fun setUseCategoryColorsEnabled(enabled: Boolean){
        viewModelScope.launch {
            userPreferencesRepository.setUseCategoryColorsEnabled(enabled)
        }
    }

    val settingsUiState: StateFlow<SettingsUiState> =
        combine(
            userPreferencesRepository.taskCompletionNotificationsEnabled,
            userPreferencesRepository.countdownRemindersEnabled,
            userPreferencesRepository.defaultCountdownRemindersMinutes,
            userPreferencesRepository.taskNotificationSoundEnabled,
            userPreferencesRepository.useCategoryColorsEnabled
        ){ completionNotificationsEnabled, countdownRemindersEnabled, countdownRemindersMinutes, taskNotificationSoundEnabled, useCategoryColorsEnabled->
            SettingsUiState(
                taskCompletionNotificationsEnabled = completionNotificationsEnabled,
                countdownRemindersEnabled = countdownRemindersEnabled,
                countdownRemindersMinutes = countdownRemindersMinutes,
                taskNotificationSoundEnabled = taskNotificationSoundEnabled,
                useCategoryColorsEnabled = useCategoryColorsEnabled
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
    val useCategoryColorsEnabled: Boolean = false,
)