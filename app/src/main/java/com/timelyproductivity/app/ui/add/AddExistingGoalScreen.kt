package com.timelyproductivity.app.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.TimelySmallTopAppBar
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testGoalsWithCategoriesSizeThree
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.components.AddGoalButton
import com.timelyproductivity.app.ui.components.filters.GoalFilteringControls
import com.timelyproductivity.app.ui.components.lists.GoalTemplateList
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.goal.GoalListUiState
import com.timelyproductivity.app.ui.goal.GoalListViewModel
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.viewgoals.AddExistingGoalUiState
import com.timelyproductivity.app.ui.viewgoals.ScheduledGoalsListUiState
import com.timelyproductivity.app.ui.viewgoals.ScheduledGoalsListViewModel
import com.timelyproductivity.app.util.MINUTES_IN_24_HOUR_DAY
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
){
    val goalListUiState by goalListViewModel.goalListUiState.collectAsState()
    val filters by goalListViewModel.filters.collectAsState()
    val addExistingGoalUiState by scheduledGoalsViewModel.addExistingGoalUiState.collectAsState()
    val scheduledGoalsListUiState by scheduledGoalsViewModel.scheduledGoalsListUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.add_an_existing_goal))
        },
    ) { innerPadding ->
        AddExistingGoalBody(
            goalListUiState = goalListUiState,
            scheduledGoalsListUiState = scheduledGoalsListUiState,
            addExistingGoalUiState = addExistingGoalUiState,
            showDurationError = addExistingGoalUiState.showSelectionDurationError,
            onAddGoalsPressed = { goalIds ->
                coroutineScope.launch {
                    scheduledGoalsViewModel.addScheduledGoalsFromExistingGoals(
                        goalIds = goalIds,
                        onNavigate = { navigateToViewGoals(scheduledGoalsViewModel.calendarEventId) }
                    )
                }
            },
            onGoalClicked = scheduledGoalsViewModel::toggleGoalSelection,
            onCancelClicked = onCancelButtonClicked,
            modifier = Modifier.padding(innerPadding),
            onCreateGoalPressed = {
                navigateToCreateGoal(scheduledGoalsViewModel.calendarEventId)
            },

            filters = filters,
            onFiltersChanged = goalListViewModel::updateFilters
        )
    }
}

@Composable
fun AddExistingGoalBody(
    goalListUiState: GoalListUiState,
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    addExistingGoalUiState: AddExistingGoalUiState,
    onGoalClicked: (Goal, Int) -> Unit,
    showDurationError: Boolean,
    onAddGoalsPressed: (Set<Int>) -> Unit,
    onCreateGoalPressed: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier,

    filters: GoalFilterState = GoalFilterState(),
    onFiltersChanged: (GoalFilterState) -> Unit,
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            GoalFilteringControls(
                filters = filters,
                categories = goalListUiState.categories,
                onFiltersChanged = onFiltersChanged,
                showRecurrenceFilter = true,
                showCompletionFilter = false,
                resultCount = goalListUiState.filteredGoalList.size,
                totalCount = goalListUiState.goalList.size,
                defaultIsExpanded = false
            )

            SchedulingCapacityIndicator(
                remainingMinutesInDay = scheduledGoalsListUiState.remainingMinutesInDay,
                selectedMinutes = addExistingGoalUiState.selectedMinutes,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            GoalTemplateList(
                goals = goalListUiState.filteredGoalList,
                categories = goalListUiState.categories,
                addCategoryColors = goalListUiState.useCategoryColorsEnabled,
                onGoalClick = { goal ->
                    onGoalClicked(
                        goal,
                        scheduledGoalsListUiState.remainingMinutesInDay
                    )
                },
                selectedGoalIds = addExistingGoalUiState.selectedGoalIds,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }

        if(showDurationError){
            Text(
                text = stringResource(
                    R.string.selected_goal_exceeds_remaining_time,
                ),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onCancelClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    stringResource(R.string.Cancel),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = { onAddGoalsPressed(addExistingGoalUiState.selectedGoalIds) },
                enabled = addExistingGoalUiState.selectedGoalIds.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pluralStringResource(
                        R.plurals.add_selected_goals,
                        addExistingGoalUiState.selectedGoalIds.size,
                        addExistingGoalUiState.selectedGoalIds.size
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SchedulingCapacityIndicator(
    remainingMinutesInDay: Int,
    selectedMinutes: Int,
    modifier: Modifier = Modifier
) {
    val totalMinutes = MINUTES_IN_24_HOUR_DAY

    val alreadyScheduledMinutes =
        (totalMinutes - remainingMinutesInDay)
            .coerceIn(0, totalMinutes)

    val availableMinutes =
        (totalMinutes - alreadyScheduledMinutes)

    val validSelectedMinutes =
        selectedMinutes.coerceIn(0, availableMinutes)

    val remainingMinutes =
        availableMinutes - validSelectedMinutes

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Remaining: ${remainingMinutes / 60}h ${remainingMinutes % 60}m",
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Time already scheduled
            if (alreadyScheduledMinutes > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(alreadyScheduledMinutes.toFloat())
                        .background(MaterialTheme.colorScheme.outline)
                )
            }

            // Time from newly selected goals
            if (validSelectedMinutes > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(validSelectedMinutes.toFloat())
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Remaining available time
            if (remainingMinutes > 0) {
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(remainingMinutes.toFloat())
                )
            }
        }

        Text(
            text = "Selected: ${validSelectedMinutes / 60}h ${validSelectedMinutes % 60}m",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddExistingGoalBodyPreview(){
    TimeManagementAppTheme {
        AddExistingGoalBody(
            goalListUiState = GoalListUiState(
                goalList = testGoalsWithCategoriesSizeThree,
                filteredGoalList = testGoalsWithCategoriesSizeThree,
                categories = testGoalCategoriesSizeThreeWithColor,
                useCategoryColorsEnabled = true,
            ),
            scheduledGoalsListUiState = ScheduledGoalsListUiState(
                totalMinutes = 6 * 60
            ),
            addExistingGoalUiState = AddExistingGoalUiState(
                selectedGoalIds = setOf(testGoalsWithCategoriesSizeThree[0].goal.goalID, testGoalsWithCategoriesSizeThree[1].goal.goalID),
                selectedMinutes = 3 * 60
            ),
            onGoalClicked = {_,_->},
            onAddGoalsPressed = {},
            onCancelClicked = {},
            modifier = Modifier,
            onCreateGoalPressed = {},
            showDurationError = false,
            onFiltersChanged = {}
        )
    }
}