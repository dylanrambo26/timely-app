package com.timelyproductivity.app.ui.reminders

import com.timelyproductivity.app.ui.components.settings.ReminderEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReminderEditorState {

    private val _uiState = MutableStateFlow(ReminderEditorUiState())

    val uiState: StateFlow<ReminderEditorUiState> = _uiState.asStateFlow()

    fun openAddReminderDialog(){
        _uiState.value =
            ReminderEditorUiState(
                isVisible = true
            )
    }

    fun openEditReminderDialog(minutes: Int){
        _uiState.value =
            ReminderEditorUiState(
                isVisible = true,
                input = minutes.toString(),
                originalMinutes = minutes
            )
    }

    fun updateReminderInput(input: String){
        _uiState.value =
            _uiState.value.copy(
                input = input,
                errorMessage = null
            )
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message
        )
    }

    fun closeReminderDialog(){
        _uiState.value =
            ReminderEditorUiState()
    }

    fun validateReminderInput(
        existingReminders: Set<Int>
    ): Int?{
        val editorState = uiState.value
        val minutes = editorState.input.toIntOrNull()

        val error = when {
            minutes == null ->
                "Enter a valid number."
            minutes <= 0 ->
                "Reminder time must be greater than zero."
            minutes != editorState.originalMinutes &&
                    minutes in existingReminders ->
                "Reminder with that value already exists."
            else -> null
        }

        if (error != null) {
            setError(error)
            return null
        }

        return minutes
    }
}