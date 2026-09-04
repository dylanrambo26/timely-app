package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.util.formatLocalDateToExtendedShorthandDate
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale

@Composable
fun RecurringGoalBody(
    recurringDays: Set<DayOfWeek>,
    recurrenceEndDate: LocalDate?,
    hasRecurrenceEndDate: Boolean,
    isGoalRecurring: Boolean,
    onRecurringChange: (Boolean) -> Unit,
    onDailyChange: (Boolean) -> Unit,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,
    updateRecurrenceEndDate: (LocalDate?) -> Unit
){
    Column(){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Make Goal Recurring?"
            )
            Checkbox(
                checked = isGoalRecurring,
                onCheckedChange = onRecurringChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        if (isGoalRecurring){
            RecurrenceOptions(
                recurringDays = recurringDays,
                recurrenceEndDate = recurrenceEndDate,
                hasRecurrenceEndDate = hasRecurrenceEndDate,
                onDailyChange = onDailyChange,
                onRecurringDayChange = onRecurringDayChange,
                updateRecurrenceEndDate = updateRecurrenceEndDate,
                onEndDateEnabledChanged = onEndDateEnabledChanged
            )
        }
    }
}

@Composable
fun RecurrenceOptions(
    recurrenceEndDate: LocalDate?,
    hasRecurrenceEndDate: Boolean,
    recurringDays: Set<DayOfWeek>,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,
    updateRecurrenceEndDate: (LocalDate?) -> Unit,
    onDailyChange: (Boolean) -> Unit,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "Daily"
        )
        Checkbox(
            checked = DayOfWeek.entries.all {day ->
                day in recurringDays
            },
            onCheckedChange = onDailyChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
    DaysOfWeekSelection(
        recurringDays = recurringDays,
        onRecurringDayChange = onRecurringDayChange
    )
    EndDateBody(
        recurringDays = recurringDays,
        recurrenceEndDate = recurrenceEndDate,
        hasRecurrenceEndDate = hasRecurrenceEndDate,
        updateRecurrenceEndDate = updateRecurrenceEndDate,
        onEndDateEnabledChanged = onEndDateEnabledChanged
    )
}

@Composable
fun EndDateBody(
    hasRecurrenceEndDate: Boolean,
    recurrenceEndDate: LocalDate?,
    recurringDays: Set<DayOfWeek>,
    onEndDateEnabledChanged: (Boolean) -> Unit,
    updateRecurrenceEndDate: (LocalDate?) -> Unit
){
    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "Set End Date?"
        )
        Checkbox(
            checked = hasRecurrenceEndDate,
            onCheckedChange = onEndDateEnabledChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            ),
            enabled = recurringDays.isNotEmpty()
        )
    }
    if(hasRecurrenceEndDate){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            TextButton(
                onClick = {
                    showDatePicker = true
                },
            ) {
                Text(
                    text = if (recurrenceEndDate != null){
                        formatLocalDateToExtendedShorthandDate(recurrenceEndDate)
                    } else {
                        "Select End Date"
                    }
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar Icon"
                )
            }
        }
    }

    if(showDatePicker){
        EndDatePicker(
            updateRecurrenceEndDate = updateRecurrenceEndDate,
            onDismissRequest = {
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndDatePicker(
    updateRecurrenceEndDate: (LocalDate?) -> Unit,
    onDismissRequest: () -> Unit,
){
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis

                    if (selectedMillis != null){
                        val selectedDate = Instant
                            .ofEpochMilli(selectedMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        updateRecurrenceEndDate(selectedDate)
                    }

                    onDismissRequest()
                }
            ){
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    ){
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DaysOfWeekSelection(
    recurringDays: Set<DayOfWeek>,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit
){
    val daysOfWeekSundayFirst = listOf(DayOfWeek.SUNDAY) + DayOfWeek.entries.filter { it != DayOfWeek.SUNDAY}
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        daysOfWeekSundayFirst.forEach { day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.getDisplayName(DateTextStyle.SHORT, Locale.getDefault())
                )
                Checkbox(
                    checked = day in recurringDays,
                    onCheckedChange = {isChecked ->
                        onRecurringDayChange(day, isChecked)
                    }
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RecurringGoalBodyPreview(){
    TimeManagementAppTheme {
        val goalUiState = GoalUiState(
            GoalDetails(
                title = "test", hours = "1", minutes = "30"
            ),
            isEntryValid = false,
            errorMessage = R.string.invalid_title,
            isGoalRecurring = true,
            hasRecurrenceEndDate = true,
            recurrenceEndDate = LocalDate.of(2026, 10, 3),
            recurringDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),

        )

        RecurringGoalBody(
            recurringDays = goalUiState.recurringDays,
            recurrenceEndDate = goalUiState.recurrenceEndDate,
            hasRecurrenceEndDate = goalUiState.hasRecurrenceEndDate,
            isGoalRecurring = goalUiState.isGoalRecurring,
            onRecurringChange = {},
            onRecurringDayChange = {_,_ ->},
            onDailyChange = {},
            onEndDateEnabledChanged = {},
            updateRecurrenceEndDate = {}
        )
    }
}