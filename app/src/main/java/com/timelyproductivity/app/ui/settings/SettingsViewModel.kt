package com.timelyproductivity.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _reminderEditorUiState = MutableStateFlow(ReminderEditorUiState())

    val reminderEditorUiState: StateFlow<ReminderEditorUiState> = _reminderEditorUiState.asStateFlow()

    fun openAddReminderDialog(){
        _reminderEditorUiState.value =
            ReminderEditorUiState(
                isVisible = true
            )
    }

    fun openEditReminderDialog(minutes: Int){
        _reminderEditorUiState.value =
            ReminderEditorUiState(
                isVisible = true,
                input = minutes.toString(),
                originalMinutes = minutes
            )
    }

    fun updateReminderInput(input: String){
        _reminderEditorUiState.value =
            _reminderEditorUiState.value.copy(
                input = input,
                errorMessage = null
            )
    }

    fun deleteReminder(minutes: Int){
        val updatedMinutes =
            settingsUiState.value.countdownRemindersMinutes - minutes

        viewModelScope.launch {
            userPreferencesRepository.setCountdownRemindersMinutes(updatedMinutes)
        }
    }

    fun closeReminderDialog(){
        _reminderEditorUiState.value =
            ReminderEditorUiState()
    }

    fun saveReminder() {
        val editorState = _reminderEditorUiState.value
        val minutes = editorState.input.toIntOrNull()

        val error = when {
            minutes == null ->
                "Enter a valid number."
            minutes <= 0 ->
                "Reminder time must be greater than zero."
            minutes != editorState.originalMinutes &&
                    minutes in settingsUiState.value.countdownRemindersMinutes ->
                        "Reminder with that value already exists."
            else -> null
        }

        if (error != null) {
            _reminderEditorUiState.value = editorState.copy(errorMessage = error)
            return
        }

        val validMinutes = minutes ?: return

        val updatedMinutes = settingsUiState.value.countdownRemindersMinutes
            .toMutableSet()
            .apply {
                editorState.originalMinutes?.let{originalMinutes ->
                    remove(originalMinutes)
                }

                add(validMinutes)
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

data class ReminderEditorUiState(
    val isVisible: Boolean = false,
    val input: String = "",
    val originalMinutes: Int? = null,
    val errorMessage: String? = null
)