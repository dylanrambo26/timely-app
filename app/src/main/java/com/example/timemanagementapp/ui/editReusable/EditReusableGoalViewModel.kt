package com.example.timemanagementapp.ui.editReusable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.createGoal.toGoal
import com.example.timemanagementapp.util.validate
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditReusableGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository
) : ViewModel() {

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val goalId: Int = checkNotNull(savedStateHandle[EditReusableGoalDestination.goalIdArg])

    init {
        viewModelScope.launch {
            goalsRepository.getGoalStream(goalId)
                .filterNotNull()
                .collect {goal ->
                    val details = GoalDetails(
                        id = goal.goalID,
                        title = goal.goalTitle,
                        hours = goal.hours.toString(),
                        minutes = goal.minutes.toString()
                    )

                    goalUiState = GoalUiState(
                        goalDetails = details,
                        isEntryValid = details.validate() == null
                    )
                }
        }
    }

    fun updateUiState(goalDetails: GoalDetails) {
        val error = goalDetails.validate()
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    suspend fun updateReusableGoal(onNavigate: () -> Unit = {}, updateFutureScheduledGoals: Boolean) {
        val error = goalUiState.goalDetails.validate()
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val goal = goalUiState.goalDetails.toGoal()
        goalsRepository.updateGoal(goal)

        if(updateFutureScheduledGoals){
            scheduledGoalsRepository.updateFutureScheduledGoalsFromEditedTemplate(
                goalId = goal.goalID,
                title = goal.goalTitle,
                hours = goal.hours,
                minutes = goal.minutes,
                startDate = LocalDate.now()
            )
        }

        onNavigate()
    }
}