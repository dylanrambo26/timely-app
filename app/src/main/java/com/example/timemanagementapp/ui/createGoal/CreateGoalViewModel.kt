package com.example.timemanagementapp.ui.createGoal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceRule
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class CreateGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
) : ViewModel(){

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    val calendarEventId =
        savedStateHandle.get<Int>(CreateGoalDestination.eventIdArg)
            ?.takeIf { it != -1 }
    private val _date = MutableStateFlow<LocalDate?>(null)
    val date: StateFlow<LocalDate?> = _date.asStateFlow()

    val canAddGoalToDate: Boolean
        get() = calendarEventId != null

    init{
        calendarEventId?.let {
            viewModelScope.launch {
                _date.value = calendarEventsRepository.getEventById(calendarEventId)?.date
            }
        }
    }

    fun updateIsGoalRecurring(isRecurring: Boolean){
        goalUiState = goalUiState.copy(
            isGoalRecurring = isRecurring
        )
    }

    fun updateHasRecurrenceEndDate(hasRecurrenceEndDate: Boolean){
        goalUiState = goalUiState.copy(
            hasRecurrenceEndDate = hasRecurrenceEndDate
        )
    }

    fun updateRecurrenceEndDate(recurrenceEndDate: LocalDate?){
        goalUiState = goalUiState.copy(
            recurrenceEndDate = recurrenceEndDate
        )
    }

    fun updateAllRecurringDays(isChecked: Boolean){
        goalUiState = goalUiState.copy(
            recurringDays = if(isChecked){
                DayOfWeek.entries.toSet()
            } else {
                emptySet()
            }
        )
    }

    fun onRecurringDayChange(
        day: DayOfWeek,
        isChecked: Boolean
    ){
        goalUiState = goalUiState.copy(
            recurringDays =
                if (isChecked){
                    goalUiState.recurringDays + day
                } else {
                    goalUiState.recurringDays - day
                }
        )
    }

    fun updateUiState(goalDetails: GoalDetails){
        val error = validateInput(goalDetails)
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    fun clearUiState(){
        goalUiState =
            goalUiState.copy(goalDetails = GoalDetails(), isEntryValid = false, errorMessage = null)
    }

    private fun validateInput(uiState: GoalDetails): Int? {
        val h = uiState.hours.toIntOrNull() ?: return R.string.invalid_hours
        val m = uiState.minutes.toIntOrNull() ?: return R.string.invalid_minutes

        if (uiState.title.isBlank()) return R.string.invalid_title
        if (h !in 0..23) return R.string.invalid_hours_0_23
        if (m !in 0..59) return R.string.invalid_minutes_0_59

        return null
    }

    suspend fun saveGoal(){

        val error = validateInput(goalUiState.goalDetails)
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        goalsRepository.insertGoal(goalUiState.goalDetails.toGoal())
    }

    suspend fun saveGoalAndAddToDate(onNavigate: (Int) -> Unit = {}){
        val eventId = calendarEventId ?: return

        val error = validateInput(goalUiState.goalDetails)
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val goal = goalUiState.goalDetails.toGoal()
        val goalTotalMinutes = goal.hours * 60 + goal.minutes

        val isValidDuration = scheduledGoalsRepository.isValidDurationForDate(
            goalTotalMinutes = goalTotalMinutes,
            eventId = eventId,
            excludedScheduledGoalId = null
        )

        if(!isValidDuration){
            goalUiState = goalUiState.copy(
                errorMessage = R.string.selected_goal_exceeds_remaining_time,
                isEntryValid = false
            )
            return
        }

        val goalId = goalsRepository.insertGoal(goal)

        if(goalUiState.isGoalRecurring){
            createRecurrenceRule(
                recurringDays = goalUiState.recurringDays,
                endDate = goalUiState.recurrenceEndDate,
                goal = goal,
                goalId = goalId
            )
        }
        else {
            //Insert scheduled goal with reusable new reusable goal values
            //Goal is only scheduled once
            scheduledGoalsRepository.insertScheduledGoal(
                ScheduledGoal(
                    goalId = goalId,
                    eventId = eventId,
                    scheduledGoalTitle = goal.goalTitle,
                    scheduledHours = goal.hours,
                    scheduledMinutes = goal.minutes,
                    recurrenceRuleId = null
                )
            )
        }
        onNavigate(eventId)
    }

    private suspend fun createRecurrenceRule(
        recurringDays: Set<DayOfWeek>,
        goalId: Int,
        endDate: LocalDate?,
        goal: Goal
    ){
        val startDate = calculateRecurrenceStartDate(recurringDays)

        val newRecurrenceRule = RecurrenceRule(
            goalId = goalId,
            recurringDays = recurringDays,
            startDate = startDate,
            endDate = endDate
        )
        val recurrenceRuleId = goalsRepository.insertRecurrenceRule(newRecurrenceRule)
        val insertedRecurrenceRule = newRecurrenceRule.copy(
            recurrenceRuleId = recurrenceRuleId.toInt()
        )
        scheduleRecurringGoals(
            recurrenceRule = insertedRecurrenceRule,
            goal = goal
        )
    }

    private suspend fun scheduleRecurringGoals(
        recurrenceRule: RecurrenceRule,
        goal: Goal
    ){
        val schedulingEndDateExclusive = recurrenceRule.endDate?.plusDays(1) ?: recurrenceRule.startDate.plusMonths(3).plusDays(1)

        val dates = recurrenceRule.startDate.datesUntil(schedulingEndDateExclusive)

        for (date in dates){
            if (date.dayOfWeek in recurrenceRule.recurringDays){
                val eventId = calendarEventsRepository.getOrCreateEventIdForDate(date)

                val scheduledGoal = ScheduledGoal(
                    goalId = recurrenceRule.goalId,
                    eventId = eventId,
                    scheduledGoalTitle = goal.goalTitle,
                    scheduledHours = goal.hours,
                    scheduledMinutes = goal.minutes,
                    recurrenceRuleId = recurrenceRule.recurrenceRuleId
                )
                scheduledGoalsRepository.insertScheduledGoal(scheduledGoal)
            }
        }
    }

    private fun calculateRecurrenceStartDate(
        recurringDays: Set<DayOfWeek>
    ): LocalDate{
        var date = LocalDate.now()

        while(date.dayOfWeek !in recurringDays){
            date = date.plusDays(1)
        }

        return date
    }
}


data class GoalUiState(
    val goalDetails: GoalDetails = GoalDetails(),
    val isEntryValid: Boolean = false,
    val errorMessage: Int? = null,
    val isDurationEditable: Boolean = true,
    val isGoalRecurring: Boolean = false,
    val recurringDays: Set<DayOfWeek> = emptySet(),
    val recurrenceEndDate: LocalDate? = null,
    val hasRecurrenceEndDate: Boolean = false
)

data class GoalDetails(
    val id: Int = 0,
    val title: String = "",
    val hours: String = "",
    val minutes: String = ""
)

fun GoalDetails.toGoal(): Goal = Goal(
    goalID = id,
    goalTitle = title,
    hours = hours.toIntOrNull() ?: 0,
    minutes = minutes.toIntOrNull() ?: 0,
)

fun Goal.toGoalUiState(isEntryValid: Boolean = false): GoalUiState = GoalUiState(
    goalDetails = this.toGoalDetails(),
    isEntryValid = isEntryValid
)

fun Goal.toGoalDetails(): GoalDetails = GoalDetails(
    id = goalID,
    title = goalTitle,
    hours = hours.toString(),
    minutes = minutes.toString()
)