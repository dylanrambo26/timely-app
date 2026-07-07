package com.example.timemanagementapp.ui.createGoal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
//import com.example.timemanagementapp.data.TestData
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import androidx.compose.runtime.getValue
import com.example.timemanagementapp.data.testGoalsSizeThree
import com.example.timemanagementapp.ui.components.GoalTemplateList
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import kotlinx.coroutines.launch


object CreateGoalDestination : NavigationDest{
    override val route = "create_goal"
    override val titleRes = R.string.create_a_goal_from_scratch

    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun CreateGoalScreen(
    createGoalViewModel: CreateGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
    goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    //scheduledGoalsListViewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val coroutineScope = rememberCoroutineScope()
    val goalListUiState by goalListViewModel.goalListUiState.collectAsState()
    Scaffold(
        topBar = { TimelySmallTopAppBar(stringResource(R.string.create_a_goal_from_scratch)) },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        CreateGoalBody(
            goalUiState = createGoalViewModel.goalUiState,
            goalListUiState = goalListUiState,
            //scheduledGoalsListUiState = scheduledGoalsListUiState,
            onGoalValueChange = createGoalViewModel::updateUiState,
            onAddGoalClicked = {
                coroutineScope.launch {
                    createGoalViewModel.saveGoal()
                }
            },
            onCancelButtonClicked = navigateBack,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CreateGoalBody(
    goalUiState: GoalUiState,
    goalListUiState: GoalListUiState,
    //scheduledGoalsListUiState: ScheduledGoalsListUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onAddGoalClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .navigationBarsPadding(),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier.weight(1.5f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            AddGoalInputForm(
                goalDetails = goalUiState.goalDetails,
                onValueChange = onGoalValueChange,
                modifier = Modifier
                    .fillMaxWidth()
            )
            AddGoalButtons(
                onAddGoalClicked = onAddGoalClicked,
                onCancelButtonClicked = onCancelButtonClicked,
                goalUiState = goalUiState,
            )
            if(goalUiState.errorMessage.isNotBlank()){
                Text(
                    text = goalUiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium_large)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.existing_goals),
                style = MaterialTheme.typography.headlineMedium
            )

            GoalTemplateList(
                goals = goalListUiState.goalList,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun AddGoalButtons(
    onAddGoalClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    goalUiState: GoalUiState,
){
    Column(
        modifier = Modifier
            .width(200.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //Save Goal and Add to Date Button
        FilledTonalButton(
            onClick = onAddGoalClicked,
            enabled = goalUiState.isEntryValid,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.save_goal_and_add_to_date),
                fontSize = 16.sp,
            )
        }
        //Save Goal Button
        OutlinedButton(
            onClick = onAddGoalClicked,
            enabled = goalUiState.isEntryValid,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.save_goal),
                fontSize = 16.sp,
            )
        }
        //Cancel Button
        OutlinedButton(
            onClick = onCancelButtonClicked,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.cancel),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun AddGoalInputForm(
    goalDetails: GoalDetails,
    modifier: Modifier = Modifier,
    onValueChange: (GoalDetails) -> Unit = {}
){
    //Goal Title Text Field
    OutlinedTextField(
        value = goalDetails.title,
        onValueChange = { onValueChange(goalDetails.copy(title = it)) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        colors = OutlinedTextFieldDefaults.colors(),
        label = {
            Text("Goal Title")
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        //Hours Text Field
        OutlinedTextField(
            value = goalDetails.hours,
            onValueChange = {onValueChange(goalDetails.copy(hours = it))},
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
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
            shape = RoundedCornerShape(16.dp),
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
}

//Preview the AddLogScreen
@Preview(showBackground = true)
@Composable
fun CreateGoalScreenPreview(){
    TimeManagementAppTheme {
        CreateGoalBody(
            goalUiState = GoalUiState(
                GoalDetails(
                    title = "Title", hours = "1", minutes = "30"
                ),
                isEntryValid = true,
            ),
            onGoalValueChange = {},
            onAddGoalClicked = {},
            onCancelButtonClicked = {},
            goalListUiState = GoalListUiState(
                goalList = testGoalsSizeThree,
            )
        )
    }
}
