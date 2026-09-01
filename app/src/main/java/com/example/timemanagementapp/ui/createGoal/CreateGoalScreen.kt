package com.example.timemanagementapp.ui.createGoal

//import com.example.timemanagementapp.data.TestData
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.TableInfo
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.testGoalsSizeThree
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.lists.GoalTemplateCard
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.util.formatLocalDateToExtendedShorthandDate
import com.example.timemanagementapp.util.formatLocalDateToShorthandDate
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale
import kotlin.collections.emptySet
import java.time.format.TextStyle as DateTextStyle


object CreateGoalDestination : NavigationDest{
    override val route = "create_goal"
    override val titleRes = R.string.create_a_goal_from_scratch

    const val eventIdArg = "eventId"
    val routeWithArgs = "$route?$eventIdArg={$eventIdArg}"
}

@Composable
fun CreateGoalScreen(
    createGoalViewModel: CreateGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
    goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    navigateToViewGoals: (Int) -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val coroutineScope = rememberCoroutineScope()
    val selectedDate by createGoalViewModel.date.collectAsState()
    Scaffold(
        topBar = { TimelySmallTopAppBar(stringResource(R.string.create_a_goal_from_scratch)) },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        CreateGoalBody(
            goalUiState = createGoalViewModel.goalUiState,
            onGoalValueChange = createGoalViewModel::updateUiState,
            onSaveGoalClicked = {
                coroutineScope.launch {
                    createGoalViewModel.saveGoal()
                }
            },
            onSaveGoalAndAddToDateClicked = {
                coroutineScope.launch {
                    createGoalViewModel.saveGoalAndAddToDate(
                        onNavigate = {eventId ->
                            navigateToViewGoals(eventId)}
                    )
                }
            },
            onCancelButtonClicked = navigateBack,
            showSaveGoalAndAddToDateButton = createGoalViewModel.canAddGoalToDate,
            modifier = Modifier.padding(innerPadding),
            onRecurringChange = createGoalViewModel::updateIsGoalRecurring,
            onDailyChange = createGoalViewModel::updateAllRecurringDays,
            onRecurringDayChange = createGoalViewModel::onRecurringDayChange,
            onEndDateEnabledChanged = createGoalViewModel::updateHasRecurrenceEndDate,
            updateRecurrenceEndDate = createGoalViewModel::updateRecurrenceEndDate,
            selectedDate = selectedDate
        )
    }
}

@Composable
fun CreateGoalBody(
    goalUiState: GoalUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onSaveGoalClicked: () -> Unit,
    onSaveGoalAndAddToDateClicked: () -> Unit,
    onRecurringChange: (Boolean) -> Unit,
    onDailyChange: (Boolean) -> Unit,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,
    updateRecurrenceEndDate: (LocalDate?) -> Unit,
    onCancelButtonClicked: () -> Unit,
    showSaveGoalAndAddToDateButton: Boolean,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = null
){
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(dimensionResource(R.dimen.padding_medium))
            .navigationBarsPadding(),

        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text= "New Goal:",
        )
        Spacer(modifier = Modifier.height(8.dp))
        GoalTemplateCard(
            goal = goalUiState.goalDetails.toGoal(),
            recurringDays = if (goalUiState.isGoalRecurring){
                goalUiState.recurringDays
            } else {
                null
            }
        )

        Spacer(modifier = Modifier.height(64.dp))

        AddGoalInputForm(
            goalDetails = goalUiState.goalDetails,
            onValueChange = onGoalValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        RecurringGoalBody(
            goalUiState = goalUiState,
            onRecurringChange = onRecurringChange,
            onDailyChange = onDailyChange,
            onRecurringDayChange = onRecurringDayChange,
            updateRecurrenceEndDate = updateRecurrenceEndDate,
            onEndDateEnabledChanged = onEndDateEnabledChanged
        )

        //Error Message Space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ){
            goalUiState.errorMessage?.let {
                Text(
                    text = stringResource(it),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        AddGoalButtons(
            onSaveGoalClicked = onSaveGoalClicked,
            onSaveGoalAndAddToDateClicked = onSaveGoalAndAddToDateClicked,
            onCancelButtonClicked = onCancelButtonClicked,
            goalUiState = goalUiState,
            selectedDate = selectedDate,
            showSaveGoalAndAddToDateButton = showSaveGoalAndAddToDateButton
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RecurringGoalBody(
    goalUiState: GoalUiState,
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
                checked = goalUiState.isGoalRecurring,
                onCheckedChange = onRecurringChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        if (goalUiState.isGoalRecurring){
            RecurrenceOptions(
                goalUiState = goalUiState,
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
    goalUiState: GoalUiState,
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
                day in goalUiState.recurringDays
            },
            onCheckedChange = onDailyChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
    DaysOfWeekSelection(
        goalUiState = goalUiState,
        onRecurringDayChange = onRecurringDayChange
    )
    EndDateBody(
        goalUiState = goalUiState,
        updateRecurrenceEndDate = updateRecurrenceEndDate,
        onEndDateEnabledChanged = onEndDateEnabledChanged
    )
}

@Composable
fun EndDateBody(
    goalUiState: GoalUiState,
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
            checked = goalUiState.hasRecurrenceEndDate,
            onCheckedChange = onEndDateEnabledChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            ),
            enabled = goalUiState.recurringDays.isNotEmpty()
        )
    }
    if(goalUiState.hasRecurrenceEndDate){
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
                    text = if (goalUiState.recurrenceEndDate != null){
                        formatLocalDateToExtendedShorthandDate(goalUiState.recurrenceEndDate)
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
            goalUiState = goalUiState,
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
    goalUiState: GoalUiState,
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
    goalUiState: GoalUiState,
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
                    checked = day in goalUiState.recurringDays,
                    onCheckedChange = {isChecked ->
                        onRecurringDayChange(day, isChecked)
                    }
                )
            }
        }

    }
}



@Composable
fun AddGoalButtons(
    onSaveGoalClicked: () -> Unit,
    onSaveGoalAndAddToDateClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    selectedDate: LocalDate? = null,
    goalUiState: GoalUiState,
    showSaveGoalAndAddToDateButton: Boolean
){
    Column(
        modifier = Modifier
            .width(240.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //Save Goal and Add to Date Button (only show if eventId exists in view model)
        if(showSaveGoalAndAddToDateButton){
            FilledTonalButton(
                onClick = onSaveGoalAndAddToDateClicked,
                enabled = goalUiState.isEntryValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                selectedDate?.let {date ->
                    Text(
                        text = if (!goalUiState.isGoalRecurring){
                            stringResource(
                                R.string.save_goal_and_add_to_date,
                                formatLocalDateToShorthandDate(date, "Today")
                            )
                        } else {
                            stringResource(R.string.save_and_schedule)
                        },
                        fontSize = 16.sp,
                    )
                }
            }
        }
        //Save Goal Button
        OutlinedButton(
            onClick = onSaveGoalClicked,
            enabled = goalUiState.isEntryValid,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.save_goal),
                fontSize = 16.sp,
            )
        }
        //Cancel Button
        OutlinedButton(
            onClick = onCancelButtonClicked,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.back),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun AddGoalInputForm(
    goalDetails: GoalDetails,
    modifier: Modifier = Modifier,
    onValueChange: (GoalDetails) -> Unit = {}
){
    Column(
        modifier = modifier,
    ) {
        //Goal Title Text Field
        OutlinedTextField(
            value = goalDetails.title,
            onValueChange = { onValueChange(goalDetails.copy(title = it)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            colors = OutlinedTextFieldDefaults.colors(),
            label = {
                Text("Goal Title")
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Hours Text Field
            OutlinedTextField(
                value = goalDetails.hours,
                onValueChange = { onValueChange(goalDetails.copy(hours = it)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(),
                label = {
                    Text("Hours")
                }
            )
            // Minutes Text Field
            OutlinedTextField(
                value = goalDetails.minutes,
                onValueChange = { onValueChange(goalDetails.copy(minutes = it)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(),
                label = {
                    Text("Minutes")
                }
            )
        }
    }
}

//Preview the AddLogScreen
@Preview(showBackground = true)
@Composable
fun CreateGoalScreenPreview(){
    TimeManagementAppTheme {
        CreateGoalBody(
            goalUiState = GoalUiState(
                GoalDetails(
                    title = "test", hours = "1", minutes = "30"
                ),
                isEntryValid = false,
                errorMessage = R.string.invalid_title,
                isGoalRecurring = true,
                hasRecurrenceEndDate = true,
                recurrenceEndDate = LocalDate.of(2026, 10, 3)
            ),
            onGoalValueChange = {},
            onSaveGoalClicked = {},
            onCancelButtonClicked = {},
            onSaveGoalAndAddToDateClicked = {},
            showSaveGoalAndAddToDateButton = true,
            selectedDate = LocalDate.now(),
            onRecurringChange = {},
            onDailyChange = {},
            onRecurringDayChange = {_,_ ->},
            onEndDateEnabledChanged = {},
            updateRecurrenceEndDate = {}
        )
    }
}
