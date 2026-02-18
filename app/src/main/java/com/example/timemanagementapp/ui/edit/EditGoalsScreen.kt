package com.example.timemanagementapp.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.Goal
//import com.example.timemanagementapp.data.TestData
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.GoalList
import com.example.timemanagementapp.ui.components.TimeRemaining
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.navigation.NavigationDest

object EditGoalsDestination : NavigationDest{
    override val route = "edit_goals"
    override val titleRes = R.string.edit_todays_goals
}

@Composable
fun EditGoalsScreen(
    //currentGoals: List<Goal>,
    onAddGoalButtonClicked: () -> Unit = {},
    //onDeleteGoal: (Goal) -> Unit,
    onEditGoal: (Goal) -> Unit,
    //remaining: Int,
    viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val goalListUiState by viewModel.goalListUiState.collectAsState()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.edit_todays_goals))
                 },
        bottomBar = {
            TimelyBottomAppBar()
        }
    ) { innerPadding ->
        EditGoalsBody(
            goalListUiState = goalListUiState,
            onDeleteGoal = {goal -> viewModel.deleteGoal(goal)},
            onEditGoal = onEditGoal,
            onAddGoal = onAddGoalButtonClicked,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EditGoalsBody(
    goalListUiState: GoalListUiState,
    onDeleteGoal: (Goal) -> Unit,
    onEditGoal: (Goal) -> Unit,
    onAddGoal: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoalList(
            goals = goalListUiState.goalList,
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
        TimeRemaining(remaining = goalListUiState.remainingMinutesInDay)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            IconButton(
                onClick = onAddGoal,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(100.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_goal),
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = stringResource(R.string.top_app_bar_add_goal),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
    }

}
/*
@Preview(showBackground = true)
@Composable
fun EditGoalsScreenPreview(){
    TimeManagementAppTheme {
        EditGoalsScreen(
            //currentGoals = TestData.goals,
            onDeleteGoal = {},
            onEditGoal = {},
            onAddGoalButtonClicked = {},
            //remaining = 870
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
            onDeleteGoal = {},
            onEditGoal = {},
            onAddGoalButtonClicked = {},
            //remaining = 1440
        )
    }
}*/
