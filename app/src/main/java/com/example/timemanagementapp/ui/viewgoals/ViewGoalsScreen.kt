package com.example.timemanagementapp.ui.viewgoals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.testScheduledGoalsSizeThree
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.ScheduledGoalList
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.completedGoal
import com.example.timemanagementapp.util.completedGoals
import com.example.timemanagementapp.util.formatLocalDateToCalendarDate
import com.example.timemanagementapp.util.formatLocalDateToShorthandDate
import com.example.timemanagementapp.util.incompleteGoals
import java.time.LocalDate

object ViewGoalsDestination : NavigationDest{
    override val route = "view_goals"
    override val titleRes = R.string.view_goals_for_date
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun ViewGoalsScreen(
    onAddGoalButtonClicked: (Int) -> Unit = {},
    onEditGoalsButtonClicked: (Int) -> Unit = {},
    //nextDayClicked: () -> Unit,
    //previousDayClicked: () -> Unit,
    //viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    scheduledGoalsListViewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToViewGoals: (Int) -> Unit = {},
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val scheduledGoalsListUiState by scheduledGoalsListViewModel.scheduledGoalsListUiState.collectAsState()
    val formattedDate = formatLocalDateToShorthandDate(scheduledGoalsListUiState.date)
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(
                stringResource(
                    R.string.view_goals_for_date,
                    formattedDate
                )
            )
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        ViewGoalsBody(
            scheduledGoalsListUiState = scheduledGoalsListUiState,
            isPastDate = { scheduledGoalsListViewModel.isPastDate() },
            onAddGoal = {
                scheduledGoalsListUiState.calendarEventId?.let(onAddGoalButtonClicked)
            },
            onEditGoalsClicked = {
                scheduledGoalsListUiState.calendarEventId?.let(onEditGoalsButtonClicked)
            },
            navigateToCalendar = navigateToCalendar,
            modifier = Modifier.padding(innerPadding),
            nextDayClicked = {
                scheduledGoalsListViewModel.viewNextDay { eventId ->
                    navigateToViewGoals(eventId)
                }
            },
            previousDayClicked = {
                scheduledGoalsListViewModel.viewPreviousDay { eventId ->
                    navigateToViewGoals(eventId)
                }
            },
            formattedDate = formattedDate
        )
    }
}

@Composable
fun ViewGoalsBody(
    //goalListUiState: GoalListUiState,
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    isPastDate: () -> Boolean,
    onAddGoal: () -> Unit,
    navigateToCalendar: () -> Unit,
    nextDayClicked: () -> Unit,
    previousDayClicked: () -> Unit,
    onEditGoalsClicked: () -> Unit,
    modifier: Modifier = Modifier,
    formattedDate: String = ""
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ViewGoalsHeader(
            displayedDay = scheduledGoalsListUiState.date,
            nextDayClicked = nextDayClicked,
            previousDayClicked = previousDayClicked
        )
        val orderedGoalList = scheduledGoalsListUiState.scheduledGoalsList.completedGoals() + scheduledGoalsListUiState.scheduledGoalsList.incompleteGoals()
        ScheduledGoalList(
            goals = orderedGoalList,
            addColors = true,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium_large)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),

            )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.completedGoal)
            )
            Text(text = " = Completed")
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
            Text(text = " = Incomplete")
        }
        if (!isPastDate()){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
            ) {
                //Edit Log Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    IconButton(
                        onClick = onEditGoalsClicked,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_goals_for_date, formattedDate),
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(
                            R.string.edit_goals_for_date,
                            formattedDate
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    IconButton(
                        onClick = onAddGoal,
                        modifier = Modifier
                            .size(100.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = stringResource(R.string.add_goal),
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.add_goal),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else{
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
            ){
                Text(
                    text = stringResource(R.string.past_date)
                )
                FilledTonalButton(
                    onClick = navigateToCalendar,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                ) {
                    Text(
                        text = stringResource(R.string.return_to_calendar),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    )
                }
            }

        }
    }
}

@Composable
fun ViewGoalsHeader(
    previousDayClicked: () -> Unit = {},
    nextDayClicked: () -> Unit = {},
    displayedDay: LocalDate?,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = previousDayClicked
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_day)
            )
        }

        Text(
            text = formatLocalDateToCalendarDate(displayedDay),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = nextDayClicked
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.next_day)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewGoalsTobBarPreview(){
    TimeManagementAppTheme {
        TimelySmallTopAppBar(
            stringResource(
                R.string.view_goals_for_date,
                "7/6"
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ViewGoalsBodyPreview(){
    TimeManagementAppTheme {
        val selectedDate = LocalDate.of(2026, 7,6)
        ViewGoalsBody(
            isPastDate = {false},
            scheduledGoalsListUiState = ScheduledGoalsListUiState(
                scheduledGoalsList = testScheduledGoalsSizeThree,
                date = selectedDate
            ),
            onAddGoal = {},
            onEditGoalsClicked = {},
            formattedDate = formatLocalDateToShorthandDate(selectedDate),
            navigateToCalendar = {},
            nextDayClicked = {},
            previousDayClicked = {},
            modifier = Modifier
        )
    }
}
