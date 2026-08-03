package com.example.timemanagementapp.ui.editScheduled

//import com.example.timemanagementapp.data.TestData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.testScheduledGoalsSizeThree
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.DisplayTime
import com.example.timemanagementapp.ui.components.ScheduledGoalList
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListUiState
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListViewModel
import com.example.timemanagementapp.util.nonActiveGoals

object EditScheduledGoalsDestination : NavigationDest{
    override val route = "edit_scheduled_goals"
    override val titleRes = R.string.edit_goals_for_date
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun EditScheduledGoalsScreen(
    onAddGoalButtonClicked: (Int) -> Unit = {},
    onEditGoal: (ScheduledGoal) -> Unit,
    //viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    //val goalListUiState by viewModel.goalListUiState.collectAsState()
    val scheduledGoalsListUiState by viewModel.scheduledGoalsListUiState.collectAsState()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.edit_todays_goals))
                 },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        EditScheduledGoalsBody(
            //goalListUiState = goalListUiState,
            scheduledGoalsListUiState = scheduledGoalsListUiState,
            onDeleteGoal = {scheduledGoal -> viewModel.deleteScheduledGoal(scheduledGoal)},
            onEditGoal = onEditGoal,
            onAddGoal = {
                scheduledGoalsListUiState.calendarEventId?.let(onAddGoalButtonClicked)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EditScheduledGoalsBody(
    //goalListUiState: GoalListUiState,
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    onDeleteGoal: (ScheduledGoal) -> Unit,
    onEditGoal: (ScheduledGoal) -> Unit,
    onAddGoal: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val filteredGoals = scheduledGoalsListUiState.scheduledGoalsList.nonActiveGoals()

        ScheduledGoalList(
            goals = filteredGoals,
            onDeleteGoal = onDeleteGoal,
            onEditGoal = onEditGoal,
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
        //TimeRemaining(remaining = goalListUiState.remainingMinutesInDay)
        DisplayTime(duration = scheduledGoalsListUiState.remainingMinutesInDay, title = stringResource(R.string.available_time_in_full_day))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            IconButton(
                onClick = onAddGoal,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(100.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_goal),
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = stringResource(R.string.add_goal),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }

}
/*@Preview(showBackground = true)
@Composable
fun EditGoalsScreenPreview(){
    TimeManagementAppTheme {
        EditGoalsScreen(
            //currentGoals = TestData.goals,
            onEditGoal = {},
            onAddGoalButtonClicked = {},
            navigateToHome = {},
            navigateToCalendar = {},
            navigateToAnalytics = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditGoalsEmptyListScreenPreview(){
    val emptyGoals = emptyList<Goal>()
    TimeManagementAppTheme {
        EditGoalsScreen(
            //currentGoals = emptyGoals,
            onEditGoal = {},
            onAddGoalButtonClicked = {},
            navigateToHome = {},
            navigateToCalendar = {},
            navigateToAnalytics = {}
        )
    }
}*/

@Preview(showBackground = true)
@Composable
fun EditScheduledGoalsBodyPreview(){
    TimeManagementAppTheme {
        EditScheduledGoalsBody(
            scheduledGoalsListUiState = ScheduledGoalsListUiState(
                scheduledGoalsList = testScheduledGoalsSizeThree
            ),
            onDeleteGoal = {},
            onEditGoal = {},
            onAddGoal = {},
        )
    }
}
