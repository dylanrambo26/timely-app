package com.example.timemanagementapp.ui.editReusable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.CreateRecurrenceUseCase
import com.example.timemanagementapp.data.UpdateRecurrenceUseCase
import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceRule
import com.example.timemanagementapp.data.recurrenceRule2
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.createGoal.toGoal
import com.example.timemanagementapp.ui.goal.withAllRecurringDays
import com.example.timemanagementapp.ui.goal.withGoalRecurring
import com.example.timemanagementapp.ui.goal.withRecurrenceEndDate
import com.example.timemanagementapp.ui.goal.withRecurrenceEndDateEnabled
import com.example.timemanagementapp.ui.goal.withRecurrenceStartDate
import com.example.timemanagementapp.ui.goal.withRecurringDay
import com.example.timemanagementapp.util.validate
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class EditReusableGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val createRecurrenceUseCase: CreateRecurrenceUseCase,
    private val updateRecurrenceUseCase: UpdateRecurrenceUseCase,
) : ViewModel() {

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val goalId: Int = checkNotNull(savedStateHandle[EditReusableGoalDestination.goalIdArg])

    private var originalRecurrenceRule: RecurrenceRule? = null

    init {
        viewModelScope.launch {
            goalsRepository.getGoalWithRecurrenceStream(goalId)
                .filterNotNull()
                .collect {goalWithRecurrence ->
                    val goal = goalWithRecurrence.goal
                    val recurrenceRule = goalWithRecurrence.recurrenceRule
                    originalRecurrenceRule = recurrenceRule
                    val isRecurring = recurrenceRule != null

                    val details = GoalDetails(
                        id = goal.goalID,
                        title = goal.goalTitle,
                        hours = goal.hours.toString(),
                        minutes = goal.minutes.toString()
                    )

                    val originalDays = goalWithRecurrence.recurrenceRule?.recurringDays.orEmpty()

                    goalUiState = GoalUiState(
                        goalDetails = details,
                        isEntryValid = details.validate() == null,
                        isGoalRecurring = isRecurring,
                        wasOriginallyRecurring = isRecurring,
                        recurringDays = originalDays,
                        originalRecurringDays = originalDays,
                        recurrenceEndDate = recurrenceRule?.endDate,
                        hasRecurrenceEndDate = recurrenceRule?.endDate != null
                    )
                }
        }
    }

    fun updateIsGoalRecurring(isRecurring: Boolean){
        goalUiState = goalUiState.withGoalRecurring(isRecurring)
    }

    fun updateAllRecurringDays(isChecked: Boolean){
        goalUiState = goalUiState.withAllRecurringDays(isChecked)
    }

    fun onRecurringDayChange(
        day: DayOfWeek,
        isChecked: Boolean
    ){
        goalUiState = goalUiState.withRecurringDay(day, isChecked)
    }

    fun updateHasRecurrenceEndDate(hasRecurrenceEndDate: Boolean){
        goalUiState = goalUiState.withRecurrenceEndDateEnabled(hasRecurrenceEndDate)
    }

    fun updateRecurrenceStartDate(recurrenceStartDate: LocalDate){
        goalUiState = goalUiState.withRecurrenceStartDate(recurrenceStartDate)
    }

    fun updateRecurrenceEndDate(recurrenceEndDate: LocalDate?){
        goalUiState = goalUiState.withRecurrenceEndDate(recurrenceEndDate)
    }

    fun updateUiState(goalDetails: GoalDetails) {
        val error = goalDetails.validate()
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    suspend fun updateReusableGoal(
        onNavigate: () -> Unit = {},
        updateFutureScheduledGoals: Boolean
    ) {
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

        //Updates goal values to the new ones, does not depend on recurring status
        if(updateFutureScheduledGoals){
            scheduledGoalsRepository.updateFutureScheduledGoalsFromEditedTemplate(
                goalId = goal.goalID,
                title = goal.goalTitle,
                hours = goal.hours,
                minutes = goal.minutes,
                startDate = LocalDate.now()
            )
        }

        //Recurring to Recurring case
        if(goalUiState.wasOriginallyRecurring){

            val existingRule = originalRecurrenceRule ?: return

            updateRecurrenceUseCase(
                originalRule = existingRule,
                recurringDays = goalUiState.recurringDays,
                startDate = goalUiState.recurrenceStartDate,
                endDate = goalUiState.recurrenceEndDate,
                deleteOldScheduledGoals = updateFutureScheduledGoals
            )
        }

        if (!goalUiState.wasOriginallyRecurring && goalUiState.isGoalRecurring){
            createRecurrenceUseCase(
                recurringDays = goalUiState.recurringDays,
                goal = goal,
                startDate = goalUiState.recurrenceStartDate,
                endDate = goalUiState.recurrenceEndDate
            )
        }
        onNavigate()
    }
}