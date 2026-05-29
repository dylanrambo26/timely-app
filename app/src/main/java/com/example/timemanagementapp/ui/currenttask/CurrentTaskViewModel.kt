package com.example.timemanagementapp.ui.currenttask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrentTaskViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel(){
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val currentTaskUiState: StateFlow<CurrentTaskUiState> =
        userPreferencesRepository.currentTaskID
            .map {currentTaskID ->
                CurrentTaskUiState(currentTaskID = currentTaskID)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CurrentTaskUiState()
            )

    fun setCurrentTask(goalId: Int){
        viewModelScope.launch {
            userPreferencesRepository.saveCurrentTaskID(goalId)
        }
    }
}

data class CurrentTaskUiState(
    val currentTaskID: Int? = null
)