package com.example.timemanagementapp.ui.goal

import com.example.timemanagementapp.ui.createGoal.GoalUiState
import java.time.DayOfWeek
import java.time.LocalDate


fun GoalUiState.withRecurrenceEndDate(
    date: LocalDate?
): GoalUiState {
    return copy(
        recurrenceEndDate = date,
    )
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