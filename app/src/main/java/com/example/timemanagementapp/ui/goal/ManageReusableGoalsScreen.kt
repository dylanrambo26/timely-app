package com.example.timemanagementapp.ui.goal

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.recurrence.GoalWithRecurrence
import com.example.timemanagementapp.data.testGoalsWithRecurrenceSizeThree
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.lists.GoalTemplateList
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

object ManageReusableGoalsDestination : NavigationDest{
    override val route = "manage_reusable_goals"
    override val titleRes = R.string.manage_reusable_goals
}

@Composable
fun ManageReusableGoalsScreen(
    onAddGoalButtonClicked: () -> Unit = {},
    onEditGoal: (Goal) -> Unit,
    goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    manageReusableGoalsViewModel: ManageReusableGoalsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val goalListUiState by goalListViewModel.goalListUiState.collectAsState()

    var goalPendingDeletion by remember {
        mutableStateOf<GoalWithRecurrence?>(null)
    }
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.manage_reusable_goals))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        ManageReusableGoalsBody(
            onCreateGoalButtonClicked = onAddGoalButtonClicked,
            goalListUiState = goalListUiState,
            onDeleteGoal = {goalWithRecurrence ->
                if(goalWithRecurrence.recurrenceRule != null){
                    goalPendingDeletion = goalWithRecurrence
                } else {
                    goalListViewModel.deleteGoal(goalWithRecurrence.goal)
                }
            },
            onEditGoal = onEditGoal,
            modifier = Modifier.padding(innerPadding)
        )
    }

    goalPendingDeletion?.let {goalWithRecurrence ->
        DeleteRecurringGoalDialog(
            onDismiss = {
                goalPendingDeletion = null
            },
            onDeleteAll = {
                manageReusableGoalsViewModel.deleteGoalAndScheduledGoals(
                    goalWithRecurrence.goal
                )
                goalPendingDeletion = null
            },
            onKeepScheduled = {
                manageReusableGoalsViewModel.deleteGoalKeepScheduledGoals(
                    goalWithRecurrence.goal
                )
                goalPendingDeletion = null
            }
        )
    }
}

@Composable
fun ManageReusableGoalsBody(
    onCreateGoalButtonClicked: () -> Unit = {},
    goalListUiState: GoalListUiState,
    onDeleteGoal: (GoalWithRecurrence) -> Unit,
    onEditGoal: (Goal) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GoalTemplateList(
            goals = goalListUiState.goalList,
            onEditGoal = onEditGoal,
            onDeleteGoal = onDeleteGoal,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium_large)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),

            )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            IconButton(
                onClick = onCreateGoalButtonClicked,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(100.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.create_a_goal_from_scratch),
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = stringResource(R.string.create_a_goal_from_scratch),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun DeleteRecurringGoalDialog(
    onDismiss: () -> Unit,
    onDeleteAll: () -> Unit,
    onKeepScheduled: () -> Unit
){
    Dialog(onDismissRequest = onDismiss) {
        DeleteRecurringGoalDialogContent(
            onDismiss = onDismiss,
            onDeleteAll = onDeleteAll,
            onKeepScheduled = onKeepScheduled
        )
    }
}

@Composable
fun DeleteRecurringGoalDialogContent(
    onDismiss: () -> Unit,
    onDeleteAll: () -> Unit,
    onKeepScheduled: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Delete recurring goal?",
                style = MaterialTheme.typography.headlineSmall
            )

            Text("What should happen to its scheduled goals?")

            Button(
                onClick = onDeleteAll,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete all associated scheduled goals")
            }

            OutlinedButton(
                onClick = onKeepScheduled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keep scheduled goals and stop recurrence")
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DeleteRecurringGoalDialogPreview(){
    TimeManagementAppTheme {
        DeleteRecurringGoalDialogContent(
            onDismiss = {},
            onDeleteAll = {},
            onKeepScheduled = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ManageReusableGoalsBodyPreview(){
    TimeManagementAppTheme {
        ManageReusableGoalsBody(
            goalListUiState = GoalListUiState(
                goalList = testGoalsWithRecurrenceSizeThree
            ),
            onDeleteGoal = {},
            onEditGoal = {},
        )
    }
}