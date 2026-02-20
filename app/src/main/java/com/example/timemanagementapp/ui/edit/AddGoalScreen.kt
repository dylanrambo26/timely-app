package com.example.timemanagementapp.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
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
import com.example.timemanagementapp.ui.components.TimeRemaining
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch


object AddGoalDestination : NavigationDest{
    override val route = "add_goal"
    override val titleRes = R.string.top_app_bar_add_goal
}

@Composable
fun AddGoalScreen(
    viewModel: AddGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
    goalListViewModel: GoalListViewModel,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val coroutineScope = rememberCoroutineScope()
    val goalListUiState by goalListViewModel.goalListUiState.collectAsState()
    Scaffold(
        topBar = { TimelySmallTopAppBar(stringResource(R.string.add_goal)) },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        AddGoalBody(
            goalUiState = viewModel.goalUiState,
            goalListUiState = goalListUiState,
            onGoalValueChange = viewModel::updateUiState,
            onAddGoalClicked = {
                coroutineScope.launch {
                    viewModel.saveGoal()
                }
            },
            onClearButtonClicked = { viewModel.clearUiState() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AddGoalBody(
    goalUiState: GoalUiState,
    goalListUiState: GoalListUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onAddGoalClicked: () -> Unit,
    onClearButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(goalListUiState.goalList){goal ->
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text= "${goal.goalTitle} - ${goal.hours}h ${goal.minutes}m",
                )
            }
        }
        TimeRemaining(remaining = goalUiState.remainingMinutesInDay)
        AddGoalInputForm(
            goalDetails = goalUiState.goalDetails,
            onValueChange = onGoalValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            //Clear Fields Button
            OutlinedButton(
                onClick = onClearButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.clear_fields),
                    fontSize = 16.sp,
                )
            }

            //Add Goal Button
            OutlinedButton(
                onClick = onAddGoalClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                enabled = goalUiState.isEntryValid
            ) {
                Text(
                    text = stringResource(R.string.add_goal),
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
        if(goalUiState.errorMessage != ""){
            Text(
                text = goalUiState.errorMessage,
                color = MaterialTheme.colorScheme.onError,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
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
}

//Preview the AddLogScreen
@Preview(showBackground = true)
@Composable
fun AddGoalScreenPreview(){
    TimeManagementAppTheme {
        AddGoalBody(
            goalUiState = GoalUiState(
                GoalDetails(
                    title = "Title", hours = "1", minutes = "30"
                )
            ),
            onGoalValueChange = {},
            onAddGoalClicked = {},
            onClearButtonClicked = {},
            goalListUiState = GoalListUiState(listOf())
        )
    }
}