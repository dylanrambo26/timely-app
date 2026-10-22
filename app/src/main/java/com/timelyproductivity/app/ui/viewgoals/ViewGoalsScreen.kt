package com.timelyproductivity.app.ui.viewgoals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.TimelyBottomAppBar
import com.timelyproductivity.app.TimelySmallTopAppBar
import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testScheduledGoalsWithCategoriesSizeFour
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.components.ActiveGoalFilters
import com.timelyproductivity.app.ui.components.GoalFilterSheet
import com.timelyproductivity.app.ui.components.ColorLegend
import com.timelyproductivity.app.ui.components.GoalFilterBar
import com.timelyproductivity.app.ui.components.LegendItem
import com.timelyproductivity.app.ui.components.lists.ScheduledGoalList
import com.timelyproductivity.app.ui.components.time.DisplayTime
import com.timelyproductivity.app.ui.filters.CompletionFilter
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.filters.applyFilters
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.settings.SettingsSwitch
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.completedGoal
import com.timelyproductivity.app.util.formatLocalDateToCalendarDate
import com.timelyproductivity.app.util.formatLocalDateToShorthandDate
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
    scheduledGoalsListViewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToViewGoals: (Int) -> Unit = {},
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val scheduledGoalsListUiState by scheduledGoalsListViewModel.scheduledGoalsListUiState.collectAsState()
    val filters by scheduledGoalsListViewModel.filters.collectAsState()
    val formattedDate = formatLocalDateToShorthandDate(scheduledGoalsListUiState.date, "Today's")
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
            onMarkAsComplete = scheduledGoalsListViewModel::setComplete,

            filters = filters,
            onSearchQueryChanged = scheduledGoalsListViewModel::updateFilters,
            onFiltersChanged = scheduledGoalsListViewModel::updateFilters
        )
    }
}

@Composable
fun ViewGoalsBody(
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    isPastDate: () -> Boolean,
    onAddGoal: () -> Unit,
    navigateToCalendar: () -> Unit,
    nextDayClicked: () -> Unit,
    previousDayClicked: () -> Unit,
    onEditGoalsClicked: () -> Unit,
    onMarkAsComplete: (ScheduledGoal, Boolean) -> Unit,

    filters: GoalFilterState = GoalFilterState(),
    onSearchQueryChanged: (GoalFilterState) -> Unit,
    onFiltersChanged: (GoalFilterState) -> Unit,

    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ViewGoalsHeader(
            displayedDay = scheduledGoalsListUiState.date,
            nextDayClicked = nextDayClicked,
            previousDayClicked = previousDayClicked,
            totalMinutes = scheduledGoalsListUiState.totalMinutes
        )

        var showFilterSheet by rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ){
            GoalFilterBar(
                searchQuery = filters.searchQuery,
                onSearchQueryChanged = {query ->
                    onSearchQueryChanged(
                        filters.copy(
                            searchQuery = query
                        )
                    )
                },
                onFilterClick = {
                    showFilterSheet = true
                }
            )

            ActiveGoalFilters(
                filters = filters,
                categories = scheduledGoalsListUiState.categories,
                onFiltersChanged = onFiltersChanged
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )

        if (showFilterSheet) {
            GoalFilterSheet(
                filters = filters,
                categories = scheduledGoalsListUiState.categories,
                showCompletionFilter = true,
                onFiltersChanged = onFiltersChanged,
                onDismiss = {
                    showFilterSheet = false
                }
            )
        }

        ScheduledGoalList(
            goals = scheduledGoalsListUiState.filteredScheduledGoals,
            categories = scheduledGoalsListUiState.categories,
            addCompletionColor = true,
            addCategoryColors = scheduledGoalsListUiState.useCategoryColorsEnabled,
            addCheckboxes = true,
            onCompleteChange = onMarkAsComplete,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        if (!isPastDate()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEditGoalsClicked,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_goals_for_date))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Edit",
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onAddGoal,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = stringResource(R.string.add_goal))
                    Spacer(Modifier.width(8.dp))
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
    modifier: Modifier = Modifier,
    previousDayClicked: () -> Unit = {},
    nextDayClicked: () -> Unit = {},
    displayedDay: LocalDate,
    totalMinutes: Int,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Text(
                text = formatLocalDateToCalendarDate(displayedDay),
                textAlign = TextAlign.Center
            )
            DisplayTime(
                title = "Scheduled",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                duration = totalMinutes
            )
        }


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
        val selectedDate = LocalDate.now()
        val goalFilterState = GoalFilterState(
            categoryIds = setOf(testGoalCategoriesSizeThreeWithColor[0].categoryId, testGoalCategoriesSizeThreeWithColor[1].categoryId),
            priorities = setOf(),
            completionFilter = CompletionFilter.ALL
        )
        ViewGoalsBody(
            isPastDate = {false},
            scheduledGoalsListUiState = ScheduledGoalsListUiState(
                scheduledGoalsList = testScheduledGoalsWithCategoriesSizeFour,
                filteredScheduledGoals = testScheduledGoalsWithCategoriesSizeFour.applyFilters(goalFilterState),
                categories = testGoalCategoriesSizeThreeWithColor,
                date = selectedDate,
                totalMinutes = 120,
                useCategoryColorsEnabled = true
            ),
            onAddGoal = {},
            onEditGoalsClicked = {},
            navigateToCalendar = {},
            nextDayClicked = {},
            previousDayClicked = {},
            onMarkAsComplete = {_,_ ->},
            modifier = Modifier,
            onFiltersChanged = {},
            onSearchQueryChanged = {},
            //filters = goalFilterState
        )
    }
}
