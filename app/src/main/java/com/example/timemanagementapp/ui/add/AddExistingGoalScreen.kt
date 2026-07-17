package com.example.timemanagementapp.ui.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
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
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.timemanagementapp.data.testGoalsSizeThree
import com.example.timemanagementapp.ui.components.AddGoalButton
import com.example.timemanagementapp.ui.components.DisplayTime
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.components.GoalTemplateList
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListUiState
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object AddExistingGoalDestination : NavigationDest{
    override val route = "add_existing_goals"
    override val titleRes = R.string.add_an_existing_goal
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun AddExistingGoalScreen(
    onCancelButtonClicked: () -> Unit = {},
    navigateToCreateGoal: (Int) -> Unit,
    navigateToViewGoals: (Int) -> Unit,
    goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    scheduledGoalsViewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val showDurationError by scheduledGoalsViewModel.showDurationError.collectAsState()
    val goalListUiState by goalListViewModel.goalListUiState.collectAsState()
    val scheduledGoalsListUiState by scheduledGoalsViewModel.scheduledGoalsListUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.add_an_existing_goal))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        AddExistingGoalBody(
            goalListUiState = goalListUiState,
            scheduledGoalsListUiState = scheduledGoalsListUiState,
            showDurationError = showDurationError,
            onAddGoalPressed = { goalId ->
                coroutineScope.launch {
                    scheduledGoalsViewModel.addScheduledGoalFromExistingGoal(
                        goalId = goalId,
                        onNavigate = { navigateToViewGoals(scheduledGoalsViewModel.calendarEventId) }
                    )
                }
            },
            onCancelClicked = onCancelButtonClicked,
            modifier = Modifier.padding(innerPadding),
            onCreateGoalPressed = {
                navigateToCreateGoal(scheduledGoalsViewModel.calendarEventId)
            }
        )
    }
}

@Composable
fun AddExistingGoalBody(
    goalListUiState: GoalListUiState,
    //goalsListUiState: ScheduledGoalsListUiState,
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    showDurationError: Boolean,
    onAddGoalPressed: (Int) -> Unit,
    onCreateGoalPressed: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedGoalId by rememberSaveable { mutableStateOf<Int?>(null) }

        if (goalListUiState.goalList.isEmpty()){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = stringResource(R.string.no_generic_goals_exist_create_one),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
                Spacer(modifier = Modifier.height(16.dp))
                AddGoalButton(
                    onAddGoal = onCreateGoalPressed,
                    text = stringResource(R.string.create_a_goal_from_scratch)
                )
            }
        } else {
            GoalTemplateList(
                goals = goalListUiState.goalList,
                onGoalClick = {goal ->
                    selectedGoalId = goal.goalID
                },
                selectedGoalId = selectedGoalId,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
        DisplayTime(duration = scheduledGoalsListUiState.remainingMinutesInDay, title = stringResource(R.string.available_time_in_full_day))

        if(showDurationError){
            Text(
                text = stringResource(R.string.selected_goal_exceeds_remaining_time),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
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
                .weight(0.33f)
        ) {
            //Cancel Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onCancelClicked()
                    }
                    .padding(12.dp)
                    .width(200.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ){
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        stringResource(R.string.Cancel),
                        textAlign = TextAlign.Center
                    )
                }
            }

            //Add Goal Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                       selectedGoalId?.let { onAddGoalPressed(it) }
                    }
                    .padding(12.dp)
                    .width(200.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            ){
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        stringResource(R.string.add_goal),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AddExistingGoalBodyPreview(){
    TimeManagementAppTheme {
        AddExistingGoalBody(
            goalListUiState = GoalListUiState(
                goalList = testGoalsSizeThree
            ),
            scheduledGoalsListUiState = ScheduledGoalsListUiState(),
            onAddGoalPressed = {},
            onCancelClicked = {},
            modifier = Modifier,
            onCreateGoalPressed = {},
            showDurationError = true
        )
    }
}