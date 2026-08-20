package com.example.timemanagementapp.ui.editReusable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.lists.GoalTemplateCard
import com.example.timemanagementapp.ui.createGoal.GoalDetails
import com.example.timemanagementapp.ui.createGoal.GoalUiState
import com.example.timemanagementapp.ui.createGoal.toGoal
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import kotlinx.coroutines.launch

object EditReusableGoalDestination : NavigationDest {
    override val route = "edit_reusable_goal"
    override val titleRes = R.string.edit_one_goal
    const val goalIdArg = "goalId"
    val routeWithArgs = "$route/{$goalIdArg}"
}

@Composable
fun EditReusableGoalScreen(
    modifier: Modifier = Modifier,
    viewModel: EditReusableGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),

    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit, //TODO
){
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { TimelySmallTopAppBar(stringResource(R.string.edit_one_goal)) },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )}
    ) { innerPadding ->
        EditReusableGoalBody(
            goalUiState = viewModel.goalUiState,
            onGoalValueChange = viewModel::updateUiState,
            onSaveAndUpdateScheduledGoalsClicked = {
                coroutineScope.launch {
                    viewModel.updateReusableGoal(
                        onNavigate = navigateBack,
                        updateFutureScheduledGoals = true
                    )
                }
            },
            onSaveGoal = {
                coroutineScope.launch {
                    viewModel.updateReusableGoal(
                        onNavigate = navigateBack,
                        updateFutureScheduledGoals = false
                    )
                }
            },
            navigateBack = navigateBack,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EditReusableGoalBody(
    goalUiState: GoalUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onSaveAndUpdateScheduledGoalsClicked: () -> Unit,
    onSaveGoal: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //TimeRemaining(remaining = goalUiState.remainingMinutesInDay)
        //DisplayTime(duration = goalUiState.remainingMinutesInDay, title = stringResource(R.string.available_time_in_full_day))

        val oldGoalDetails = remember(goalUiState.goalDetails.id){
            goalUiState.goalDetails
        }
        Text(stringResource(R.string.old_goal))
        GoalTemplateCard(
            goal = oldGoalDetails.toGoal(),
        )
        Text(stringResource(R.string.new_goal))
        GoalTemplateCard(
            goal = goalUiState.goalDetails.toGoal(),
        )


        EditGoalInputForm(
            goalDetails = goalUiState.goalDetails,
            onValueChange = onGoalValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier.width(280.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Save Goal Button
            Button(
                onClick = onSaveGoal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.save_edit_one_goal),
                    fontSize = 16.sp,
                )
            }
            if(goalUiState.errorMessage != null){
                Text(
                    text = stringResource(goalUiState.errorMessage),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }

            //Save Goal and Ripple Edit future scheduled goals
            OutlinedButton(
                onClick = onSaveAndUpdateScheduledGoalsClicked,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.save_and_overwrite_future_scheduled_goals),
                    fontSize = 16.sp,
                )
            }

            //Cancel Edit Button
            TextButton(
                onClick = navigateBack,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.cancel_edit_one_goal),
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
fun EditGoalInputForm(
    goalDetails: GoalDetails,
    modifier: Modifier = Modifier,
    onValueChange: (GoalDetails) -> Unit = {}
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        //Hours Text Field
        OutlinedTextField(
            value = goalDetails.hours,
            onValueChange = {onValueChange(goalDetails.copy(hours = it))},
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(),
            label = {
                Text("Hours")
            }
        )
        // Minutes Text Field
        OutlinedTextField(
            value = goalDetails.minutes,
            onValueChange = {onValueChange(goalDetails.copy(minutes = it))},
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(),
            label = {
                Text("Minutes")
            }
        )
    }

    //Goal Title Text Field
    OutlinedTextField(
        value = goalDetails.title,
        onValueChange = {onValueChange(goalDetails.copy(title = it))},
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        colors = OutlinedTextFieldDefaults.colors(),
        label = {
            Text("Goal Title")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditReusableGoalBodyPreview(){
    TimeManagementAppTheme {
        EditReusableGoalBody(
            goalUiState = GoalUiState(
                GoalDetails(
                    title = "Title", hours = "1", minutes = "30"
                )
            ),
            onGoalValueChange = {},
            onSaveAndUpdateScheduledGoalsClicked = {},
            onSaveGoal = {},
            navigateBack = {}
        )
    }
}