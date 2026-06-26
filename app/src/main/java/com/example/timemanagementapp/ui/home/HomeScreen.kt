package com.example.timemanagementapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.navigation.NavigationDest
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalStatus
import com.example.timemanagementapp.ui.components.RemainingTaskTime
import com.example.timemanagementapp.ui.components.TimeRemainingInDay
import com.example.timemanagementapp.ui.currenttask.CurrentTaskUiState
import com.example.timemanagementapp.ui.currenttask.CurrentTaskViewModel
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme


object HomeDestination : NavigationDest {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    //TODO add navigation and screens for Calendar, Analytics, and Settings
    navigateToEditGoals: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToChangeCurrentTask: () -> Unit,
    modifier: Modifier = Modifier,
    goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currentTaskViewModel: CurrentTaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val currentTaskUiState by currentTaskViewModel.currentTaskUiState.collectAsState()
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
                        onClick = ({ /*TODO: Add settings functionality*/ })) {
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

    ){  innerPadding ->
        HomeBody(
            currentTaskUiState = currentTaskUiState,
            modifier = modifier.padding(innerPadding),
            onPauseButtonClicked = { currentTaskViewModel.pauseTask() },
            onResumeButtonClicked = { currentTaskUiState.currentTask?.let { currentTaskViewModel.startTaskTimer(it) } },
            onEditButtonClicked = navigateToEditGoals,
            onCurrentTaskClicked = navigateToChangeCurrentTask,
        )
}}

/**
 * @param onEditButtonClicked - function that will navigate to Add Log screen when clicked
 * @param modifier
 */
@Composable
fun HomeBody(
    currentTaskUiState: CurrentTaskUiState,
    onPauseButtonClicked: () -> Unit = {},
    onResumeButtonClicked: () -> Unit = {},
    onEditButtonClicked: () -> Unit = {},
    onCurrentTaskClicked: () -> Unit = {},
    modifier: Modifier = Modifier
){
    Column (
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text= stringResource(R.string.current_task) + " ${currentTaskUiState.currentTask?.goalTitle ?: "No Active Task"}",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth(),
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                currentTaskUiState.currentTask?.let {goal ->
                    RemainingTaskTime(goal)
                }
            }
            item{
                val currentTaskStatusText = when(currentTaskUiState.currentTask?.status){
                    GoalStatus.COMPLETED -> stringResource(R.string.current_task_status_completed)
                    GoalStatus.NOT_STARTED -> stringResource(R.string.current_task_status_not_started)
                    GoalStatus.RUNNING -> stringResource(R.string.current_task_status_running)
                    GoalStatus.PAUSED -> stringResource(R.string.current_task_status_paused)
                    null -> ""
                }

                Text(text = currentTaskStatusText)
            }

            item{
                TimeRemainingInDay()
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            thickness = 4.dp
        )

        val pauseButtonEnabled = (currentTaskUiState.currentTask?.status == GoalStatus.RUNNING) ||
                (currentTaskUiState.currentTask?.status == GoalStatus.PAUSED)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                if(currentTaskUiState.currentTask?.status == GoalStatus.RUNNING){
                    onPauseButtonClicked()
                }
                else if(currentTaskUiState.currentTask?.status == GoalStatus.PAUSED){
                    onResumeButtonClicked()
                }
            },
            enabled = pauseButtonEnabled,
        ){
            val pauseButtonText = when(currentTaskUiState.currentTask?.status){
            GoalStatus.PAUSED -> "Task is Paused: Click to Resume"
            GoalStatus.RUNNING -> "Pause Current Task"
            GoalStatus.NOT_STARTED -> "Task Not Started"
            GoalStatus.COMPLETED -> "Task Complete"
            null -> ""
        }
            Text(text = pauseButtonText, textAlign = TextAlign.Center)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .weight(0.67f)
        ) {
            //Edit Log Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
            ) {
                IconButton(
                    onClick = onEditButtonClicked,
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.view_todays_goals),
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.view_todays_goals),
                    textAlign = TextAlign.Center
                )
            }

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
        HomeBody(
            currentTaskUiState = CurrentTaskUiState(Goal(
                goalID = 0,
                hours = 1,
                minutes = 30,
                goalTitle = "study",
                status = GoalStatus.NOT_STARTED
            )),
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
        )
    }
}


