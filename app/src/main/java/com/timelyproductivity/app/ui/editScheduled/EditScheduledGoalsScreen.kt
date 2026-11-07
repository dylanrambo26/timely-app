package com.timelyproductivity.app.ui.editScheduled

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
import com.timelyproductivity.app.R
import com.timelyproductivity.app.TimelyBottomAppBar
import com.timelyproductivity.app.TimelySmallTopAppBar
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testScheduledGoalsWithCategoriesSizeFour
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.TimelyScaffold
import com.timelyproductivity.app.ui.components.buttons.TwoActionButtonRow
import com.timelyproductivity.app.ui.components.filters.GoalFilteringControls
import com.timelyproductivity.app.ui.components.lists.ScheduledGoalList
import com.timelyproductivity.app.ui.components.time.DisplayTime
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.viewgoals.ScheduledGoalsListUiState
import com.timelyproductivity.app.ui.viewgoals.ScheduledGoalsListViewModel
import com.timelyproductivity.app.util.nonActiveGoals

object EditScheduledGoalsDestination : NavigationDest{
    override val route = "edit_scheduled_goals"
    override val titleRes = R.string.edit_goals_for_date
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun EditScheduledGoalsScreen(
    onAddGoalButtonClicked: (Int) -> Unit = {},
    navigateToViewGoals: (Int) -> Unit,
    onEditGoal: (ScheduledGoal) -> Unit,
    viewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val scheduledGoalsListUiState by viewModel.scheduledGoalsListUiState.collectAsState()
    val filters by viewModel.filters.collectAsState()
    TimelyScaffold(
        topBarTitle = stringResource(R.string.edit_todays_goals),
        showBottomBar = false
    ) { innerPadding ->
        EditScheduledGoalsBody(
            scheduledGoalsListUiState = scheduledGoalsListUiState,
            onDeleteGoal = {scheduledGoal -> viewModel.deleteScheduledGoal(scheduledGoal)},
            onEditGoal = onEditGoal,
            onAddGoal = {
                scheduledGoalsListUiState.calendarEventId?.let(onAddGoalButtonClicked)
            },
            navigateToViewGoals = {
                scheduledGoalsListUiState.calendarEventId?.let (
                    navigateToViewGoals
                )
            },
            modifier = Modifier.padding(innerPadding),

            filters = filters,
            onFiltersChanged = viewModel::updateFilters
        )
    }
}

@Composable
fun EditScheduledGoalsBody(
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    onDeleteGoal: (ScheduledGoal) -> Unit,
    onEditGoal: (ScheduledGoal) -> Unit,
    onAddGoal: () -> Unit,
    navigateToViewGoals: () -> Unit,
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
            categories = scheduledGoalsListUiState.categories,
            onFiltersChanged = onFiltersChanged,
            showCompletionFilter = false,
            resultCount = scheduledGoalsListUiState.filteredScheduledGoals.size,
            totalCount = scheduledGoalsListUiState.scheduledGoalsList.size
        )

        ScheduledGoalList(
            goals = scheduledGoalsListUiState.filteredScheduledGoals.nonActiveGoals(),
            categories = scheduledGoalsListUiState.categories,
            addCategoryColors = scheduledGoalsListUiState.useCategoryColorsEnabled,
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

        val canAddGoals = scheduledGoalsListUiState.calendarEventId != null && scheduledGoalsListUiState.remainingMinutesInDay > 0
        TwoActionButtonRow(
            onOutlinedButtonClicked = navigateToViewGoals,
            onButtonClicked = onAddGoal,
            outlinedButtonText = "View Goals",
            buttonText = if(canAddGoals) stringResource(R.string.add_goal) else "Day Full",
            buttonEnabled = canAddGoals,
            buttonIcon = if(canAddGoals) Icons.Default.AddCircle else null,
            buttonDescription = stringResource(R.string.add_goal)
        )
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
                scheduledGoalsList = testScheduledGoalsWithCategoriesSizeFour,
                categories = testGoalCategoriesSizeThreeWithColor,
                useCategoryColorsEnabled = true,
                filteredScheduledGoals = testScheduledGoalsWithCategoriesSizeFour
            ),
            onDeleteGoal = {},
            onEditGoal = {},
            onAddGoal = {},
            onFiltersChanged = {},
            navigateToViewGoals = {}
        )
    }
}
