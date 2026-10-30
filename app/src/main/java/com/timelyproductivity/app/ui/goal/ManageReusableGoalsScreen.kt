package com.timelyproductivity.app.ui.goal

//import com.example.timemanagementapp.data.TestData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.TimelyBottomAppBar
import com.timelyproductivity.app.TimelySmallTopAppBar
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.recurrence.GoalWithRecurrence
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testGoalsWithCategoriesSizeThree
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.components.filters.GoalFilteringControls
import com.timelyproductivity.app.ui.components.lists.GoalTemplateList
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

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
    val filters by goalListViewModel.filters.collectAsState()

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

            filters = filters,
            onFiltersChanged = goalListViewModel::updateFilters,
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
    modifier: Modifier = Modifier,

    filters: GoalFilterState = GoalFilterState(),
    onFiltersChanged: (GoalFilterState) -> Unit,
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoalFilteringControls(
            filters = filters,
            onFiltersChanged = onFiltersChanged,
            showCompletionFilter = false,
            showRecurrenceFilter = true,
            categories = goalListUiState.categories,
            resultCount = goalListUiState.filteredGoalList.size,
            totalCount = goalListUiState.goalList.size
        )

        GoalTemplateList(
            goals = goalListUiState.filteredGoalList,
            categories = goalListUiState.categories,
            addCategoryColors = goalListUiState.useCategoryColorsEnabled,
            onEditGoal = onEditGoal,
            onDeleteGoal = onDeleteGoal,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onCreateGoalButtonClicked
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text(stringResource(R.string.create_a_goal_from_scratch))
            }
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
                goalList = testGoalsWithCategoriesSizeThree,
                categories = testGoalCategoriesSizeThreeWithColor,
                useCategoryColorsEnabled = true,
                filteredGoalList = testGoalsWithCategoriesSizeThree
            ),
            onDeleteGoal = {},
            onEditGoal = {},
            onFiltersChanged = {}
        )
    }
}