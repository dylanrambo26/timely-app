package com.timelyproductivity.app.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.TimelyBottomAppBar
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.currenttask.CurrentTaskUiState
import com.timelyproductivity.app.ui.currenttask.CurrentTaskViewModel
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme


object HomeDestination : NavigationDest {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToViewGoals: (Int) -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToChangeCurrentTask: () -> Unit,
    navigateToManageReusableGoals: () -> Unit,
    modifier: Modifier = Modifier,
    currentTaskViewModel: CurrentTaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val currentTaskUiState by currentTaskViewModel.currentTaskUiState.collectAsState()
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    HomeScreenContent(
        currentTaskUiState = currentTaskUiState,
        homeUiState = homeUiState,
        navigateToCalendar = navigateToCalendar,
        navigateToSettings = navigateToSettings,
        navigateToAnalytics = navigateToAnalytics,
        onPauseButtonClicked = { currentTaskViewModel.pauseTask() },
        onResumeButtonClicked = { currentTaskUiState.currentTask?.let { currentTaskViewModel.startTaskTimer(it) } },
        onViewButtonClicked = {
            homeViewModel.viewGoalsForToday{ eventId ->
                navigateToViewGoals(eventId)
            }
        },
        onManageReusableGoalsClicked = navigateToManageReusableGoals,
        onCurrentTaskClicked = navigateToChangeCurrentTask,
        onMarkAsCompleteClicked = {currentTaskViewModel.markAsComplete()},
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    navigateToSettings: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit,

    currentTaskUiState: CurrentTaskUiState,
    homeUiState: HomeUiState,
    onPauseButtonClicked: () -> Unit,
    onResumeButtonClicked: () -> Unit,
    onViewButtonClicked: () -> Unit,
    onCurrentTaskClicked: () -> Unit,
    onManageReusableGoalsClicked: () -> Unit,
    onMarkAsCompleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
){
    Scaffold(
        //Top bar and bottom bar persist through each navigation
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Welcome, User", //TODO change later to supply actual username
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    //Settings Button
                    IconButton(
                        onClick = navigateToSettings
                    )
                    {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings Button"
                        )
                    }
                }

            )
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onAnalyticsClick = navigateToAnalytics,
                onHomeClick = {}
            )
        }

    ){ innerPadding ->
        HomeBody(
            currentTaskUiState = currentTaskUiState,
            homeUiState = homeUiState,
            modifier = modifier.padding(innerPadding),
            onPauseButtonClicked = onPauseButtonClicked,
            onResumeButtonClicked = onResumeButtonClicked,
            onViewButtonClicked = onViewButtonClicked,
            onManageReusableGoalsClicked = onManageReusableGoalsClicked,
            onCurrentTaskClicked = onCurrentTaskClicked,
            onMarkAsCompleteClicked = onMarkAsCompleteClicked,
        )
    }
}

/**
 * @param onViewButtonClicked - function that will navigate to Add Log screen when clicked
 * @param modifier
 */
@Composable
fun HomeBody(
    currentTaskUiState: CurrentTaskUiState,
    homeUiState: HomeUiState,
    onPauseButtonClicked: () -> Unit = {},
    onResumeButtonClicked: () -> Unit = {},
    onViewButtonClicked: () -> Unit = {},
    onCurrentTaskClicked: () -> Unit = {},
    onManageReusableGoalsClicked: () -> Unit = {},
    onMarkAsCompleteClicked: () -> Unit = {},
    modifier: Modifier = Modifier
){

    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val category = homeUiState.goalCategories.firstOrNull(){
            it.categoryId == currentTaskUiState.currentTask?.scheduledCategoryId
        }

        if(currentTaskUiState.currentTask != null) {
            CurrentTaskCard(
                scheduledGoal = currentTaskUiState.currentTask,
                useCategoryColorsEnabled = homeUiState.useCategoryColorsEnabled,
                category = category,
                onMarkAsCompleteClicked = onMarkAsCompleteClicked,
                onPauseButtonClicked = onPauseButtonClicked,
                onResumeButtonClicked = onResumeButtonClicked
            )
        } else {
            Text(
                text = "No current task selected. Click \"Change Current Task\" button to select one.",
                style = MaterialTheme.typography.titleMedium
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            thickness = 4.dp
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
            ) {
                IconButton(
                    onClick = onViewButtonClicked,
                    modifier = Modifier.size(75.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.view_todays_goals),
                        modifier = Modifier
                            .size(75.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.view_todays_goals),
                    textAlign = TextAlign.Center
                )
            }

            //Manage Reusable Goals Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
            ) {
                IconButton(
                    onClick = onManageReusableGoalsClicked,
                    modifier = Modifier.size(75.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.manage_reusable_goals),
                        modifier = Modifier
                            .size(75.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.manage_reusable_goals),
                    textAlign = TextAlign.Center
                )
            }

            //Current Task Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable {
                        onCurrentTaskClicked()
                    },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ){
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        "Click Here to Change Current Task",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview(){
    TimeManagementAppTheme{
        val goal = Goal(
            goalID = 7,
            goalTitle = "Test Goal",
            hours = 1,
            minutes = 30,
        )
        HomeBody(
            currentTaskUiState = CurrentTaskUiState(
                ScheduledGoal(
                        scheduledGoalId = 12,
                        eventId = 3,
                        goalId = 7,
                        status = GoalStatus.COMPLETED,
                        startTimeMillis = 123456789L,
                        completedMillis = 600000L,
                        scheduledGoalTitle = goal.goalTitle,
                        scheduledHours = goal.hours,
                        scheduledMinutes = goal.minutes,
                    )
                ),
            homeUiState = HomeUiState(
                goalCategories = testGoalCategoriesSizeThreeWithColor,
                useCategoryColorsEnabled = true
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
        )
    }
}

@Preview(
    name = "Galaxy S23+",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=803dp,dpi=393",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeContentPreview(){
    TimeManagementAppTheme(
        darkTheme = true,
        dynamicColor = false
    ){
        val goal = Goal(
            goalID = 7,
            goalTitle = "Test Goal",
            hours = 1,
            minutes = 30
        )

        val scheduledGoal = ScheduledGoal(
            scheduledGoalId = 12,
            eventId = 3,
            goalId = 7,
            status = GoalStatus.PAUSED,
            startTimeMillis = System.currentTimeMillis(),
            completedMillis = 600000L,
            scheduledGoalTitle = goal.goalTitle,
            scheduledHours = goal.hours,
            scheduledMinutes = goal.minutes,
            scheduledCategoryId = testGoalCategoriesSizeThreeWithColor[0].categoryId
        )

        HomeScreenContent(
            navigateToSettings = {},
            navigateToCalendar = {},
            navigateToAnalytics = {},
            onMarkAsCompleteClicked = {},
            onResumeButtonClicked = {},
            onViewButtonClicked = {},
            onCurrentTaskClicked = {},
            onManageReusableGoalsClicked = {},
            onPauseButtonClicked = {},
            currentTaskUiState = CurrentTaskUiState(
                currentTask = scheduledGoal
            ),
            homeUiState = HomeUiState(
                goalCategories = testGoalCategoriesSizeThreeWithColor,
                useCategoryColorsEnabled = true
            )
        )
    }
}


