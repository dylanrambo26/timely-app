package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.util.formatLocalDateToExtendedShorthandDate
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale
import java.time.format.TextStyle as DateTextStyle

@Composable
fun RecurringGoalBody(
    recurringDays: Set<DayOfWeek>,

    recurrenceStartDate: LocalDate,
    recurrenceEndDate: LocalDate?,
    hasRecurrenceEndDate: Boolean,
    isGoalRecurring: Boolean,
    onRecurringChange: (Boolean) -> Unit,
    onDailyChange: (Boolean) -> Unit,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,

    updateRecurrenceStartDate: (LocalDate) -> Unit,
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
                recurrenceStartDate = recurrenceStartDate,
                recurrenceEndDate = recurrenceEndDate,
                hasRecurrenceEndDate = hasRecurrenceEndDate,
                onDailyChange = onDailyChange,
                onRecurringDayChange = onRecurringDayChange,
                updateRecurrenceEndDate = updateRecurrenceEndDate,
                updateRecurrenceStartDate = updateRecurrenceStartDate,
                onEndDateEnabledChanged = onEndDateEnabledChanged
            )
        }
    }
}

@Composable
fun RecurrenceOptions(
    recurrenceStartDate: LocalDate,
    recurrenceEndDate: LocalDate?,

    hasRecurrenceEndDate: Boolean,
    recurringDays: Set<DayOfWeek>,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,

    updateRecurrenceStartDate: (LocalDate) -> Unit,
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
    StartDateBody(
        recurrenceStartDate = recurrenceStartDate,
        updateRecurrenceStartDate = updateRecurrenceStartDate
    )
    EndDateBody(
        recurringDays = recurringDays,
        recurrenceStartDate = recurrenceStartDate,
        recurrenceEndDate = recurrenceEndDate,
        hasRecurrenceEndDate = hasRecurrenceEndDate,
        updateRecurrenceEndDate = updateRecurrenceEndDate,
        onEndDateEnabledChanged = onEndDateEnabledChanged
    )
}

@Composable
fun StartDateBody(
    recurrenceStartDate: LocalDate,
    updateRecurrenceStartDate: (LocalDate) -> Unit
){
    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = "Starts:")

        TextButton(
            onClick = {
                showDatePicker = true
            }
        ) {
            Text(
                text = formatLocalDateToExtendedShorthandDate(
                    recurrenceStartDate
                )
            )

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendar Icon"
            )
        }
    }
    if(showDatePicker){
        RecurrenceDatePicker(
            selectedDate = recurrenceStartDate,
            onDateSelected = updateRecurrenceStartDate,
            invalidDateMessage = stringResource(R.string.recurrence_start_date_cannot_be_in_past),
            minimumDate = LocalDate.now(),
            onDismissRequest = {
                showDatePicker = false
            }
        )
    }

}

@Composable
fun EndDateBody(
    hasRecurrenceEndDate: Boolean,
    recurrenceStartDate: LocalDate,
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
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Ends:")

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
        RecurrenceDatePicker(
            selectedDate = recurrenceEndDate,
            onDateSelected = updateRecurrenceEndDate,
            minimumDate = recurrenceStartDate,
            invalidDateMessage = stringResource(R.string.recurrence_end_date_must_be_after_start_date),
            onDismissRequest = {
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceDatePicker(
    selectedDate: LocalDate?,
    minimumDate: LocalDate,
    invalidDateMessage: String,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
){
    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val initialSelectedDateMillis = selectedDate
        ?.atStartOfDay(ZoneOffset.UTC)
        ?.toInstant()
        ?.toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    val selectedLocalDate = datePickerState.selectedDateMillis?.let { millis ->
        Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
    }

    val isSelectedDateValid = selectedLocalDate?.let { date ->
        !date.isBefore(minimumDate)
    } == true

    errorMessage = if (!isSelectedDateValid){
        invalidDateMessage
    } else {
        null
    }

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedLocalDate?.let { date ->
                        onDateSelected(date)
                        onDismissRequest()
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
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
        Column {
            DatePicker(state = datePickerState)

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 8.dp
                    )
                )
            }
        }
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
            recurrenceStartDate = goalUiState.recurrenceStartDate,
            hasRecurrenceEndDate = goalUiState.hasRecurrenceEndDate,
            isGoalRecurring = goalUiState.isGoalRecurring,
            onRecurringChange = {},
            onRecurringDayChange = {_,_ ->},
            onDailyChange = {},
            onEndDateEnabledChanged = {},
            updateRecurrenceStartDate = {},
            updateRecurrenceEndDate = {}
        )
    }
}