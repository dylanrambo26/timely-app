package com.timelyproductivity.app.ui.goal

import com.timelyproductivity.app.R
import com.timelyproductivity.app.ui.createGoal.GoalUiState
import com.timelyproductivity.app.util.validate
import java.time.DayOfWeek
import java.time.LocalDate


fun GoalUiState.withRecurrenceEndDate(
    date: LocalDate?
): GoalUiState {
    return copy(
        recurrenceEndDate = date,
    )
}

fun GoalUiState.withRecurrenceStartDate(
    date: LocalDate
): GoalUiState{
    return copy(
        recurrenceStartDate = date,
        recurrenceEndDate = recurrenceEndDate?.takeUnless { it.isBefore(date) }
    )
}

fun GoalUiState.validateRecurrence(
    today: LocalDate = LocalDate.now()
): Int? {
    if(!isGoalRecurring){
        return null
    }

    if(recurringDays.isEmpty()){
        return R.string.select_at_least_one_recurring_day
    }

    if(recurrenceStartDate.isBefore(today)){
        return R.string.recurrence_start_date_cannot_be_in_past
    }

    if(
        recurrenceEndDate != null && recurrenceEndDate.isBefore(recurrenceStartDate)
    ){
        return R.string.recurrence_end_date_must_be_after_start_date
    }

    return null
}

val GoalUiState.canSave: Boolean
    get() {
        val goalValuesValid = goalDetails.validate() == null

        val recurrenceDatesValid = !hasRecurrenceEndDate || (
                recurrenceEndDate != null
                        && !recurrenceEndDate.isBefore(recurrenceStartDate)
                        && !recurrenceStartDate.isBefore(LocalDate.now()))

        val recurrenceValid =
            !isGoalRecurring || (recurringDays.isNotEmpty() && recurrenceDatesValid)

        return goalValuesValid && recurrenceValid
    }

fun GoalUiState.withGoalRecurring(
    isRecurring: Boolean
): GoalUiState {
    return copy(isGoalRecurring = isRecurring)
}

fun GoalUiState.withRecurrenceEndDateEnabled(
    isEnabled: Boolean
): GoalUiState {
    return copy(hasRecurrenceEndDate = isEnabled)
}

fun GoalUiState.withAllRecurringDays(
    isChecked: Boolean
): GoalUiState {
    return copy(
        recurringDays = if (isChecked){
            DayOfWeek.entries.toSet()
        } else {
            emptySet()
        }
    )
}

fun GoalUiState.withRecurringDay(
    day: DayOfWeek,
    isChecked: Boolean
): GoalUiState {
    val updatedDays = if (isChecked) {
        recurringDays + day
    } else {
        recurringDays - day
    }

    return copy(
        recurringDays = updatedDays
    )
}