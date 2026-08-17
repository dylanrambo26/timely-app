package com.example.timemanagementapp.ui.currenttask

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.testScheduledGoalsSizeThree
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.ScheduledGoalList
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListUiState
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListViewModel
import com.example.timemanagementapp.util.incompleteGoals

object CurrentTaskDestination : NavigationDest{
    override val route = "current_task"
    override val titleRes = R.string.change_current_task
}

@Composable
fun CurrentTaskScreen(
    //goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    scheduledGoalsListViewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currentTaskViewModel: CurrentTaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
    navigateBack: () -> Unit
){
    val scheduledGoalsListUiState by scheduledGoalsListViewModel.scheduledGoalsListUiState.collectAsState()
    val currentTaskUiState by currentTaskViewModel.currentTaskUiState.collectAsState()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.change_current_task))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        CurrentTaskBody(
            //goalListUiState = goalListUiState,
            scheduledGoalsListUiState = scheduledGoalsListUiState,
            //currentTaskUiState = currentTaskUiState,
            onSaveCurrentTaskPressed = {scheduledGoal ->
                currentTaskViewModel.startTaskTimer(scheduledGoal)
            },
            navigateToHome = navigateToHome,
            navigateBack = navigateBack,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CurrentTaskBody(
    //goalListUiState: GoalListUiState,
    scheduledGoalsListUiState: ScheduledGoalsListUiState,
    //currentTaskUiState: CurrentTaskUiState,
    onSaveCurrentTaskPressed: (ScheduledGoal) -> Unit,
    navigateToHome: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

        }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedGoalId by rememberSaveable { mutableStateOf<Int?>(null) }

        val selectedGoal = scheduledGoalsListUiState.scheduledGoalsList
            .firstOrNull{
                it.scheduledGoalId == selectedGoalId
            }
        val filteredGoals = scheduledGoalsListUiState.scheduledGoalsList.incompleteGoals()

        //Display a goal list filtered for goals that are paused and not started only
        ScheduledGoalList(
            goals = filteredGoals,
            selectedGoalId = selectedGoalId,
            onGoalClick = {scheduledGoal ->
                selectedGoalId = scheduledGoal.scheduledGoalId
            },
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
            modifier = Modifier.padding(16.dp)
        ) {
            //Cancel Button
            OutlinedButton(
                onClick = navigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel_edit_one_goal),
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }

            //Save Goal Button
            OutlinedButton(
                onClick = {
                    selectedGoal?.let {goal ->
                        if (
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ){
                            notificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }

                        onSaveCurrentTaskPressed(goal)
                        navigateToHome()
                    }
                },
                enabled = selectedGoal != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.save_current_task),
                    fontSize = 14.sp,
                    color = Color.Green
                )
            }
        }
    }

}
@Preview(showBackground = true)
@Composable
fun CurrentTaskBodyPreview(){
    TimeManagementAppTheme {
        CurrentTaskBody(
            scheduledGoalsListUiState = ScheduledGoalsListUiState(
                scheduledGoalsList = testScheduledGoalsSizeThree
            ),
            //currentTaskUiState = CurrentTaskUiState(testScheduledGoalsSizeThree[0]),
            onSaveCurrentTaskPressed = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
            navigateToHome = {},
            navigateBack = {}
        )
    }
}
