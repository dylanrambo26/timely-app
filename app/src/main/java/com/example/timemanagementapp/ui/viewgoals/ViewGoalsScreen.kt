package com.example.timemanagementapp.ui.viewgoals

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalStatus
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.DisplayTime
import com.example.timemanagementapp.ui.components.GoalList
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.util.completedGoals
import com.example.timemanagementapp.util.filterByStatus
import com.example.timemanagementapp.util.incompleteGoals

/*object ViewGoalsDestination : NavigationDest{
    override val route = "view_goals"
    override val titleRes = R.string.view_todays_goals
}*/

@Composable
fun ViewGoalsScreen(
    onAddGoalButtonClicked: () -> Unit = {},
    onEditGoalsButtonClicked: () -> Unit = {},
    viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val goalListUiState by viewModel.goalListUiState.collectAsState()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.edit_todays_goals))
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
            goalListUiState = goalListUiState,
            onAddGoal = onAddGoalButtonClicked,
            onEditGoalsClicked = onEditGoalsButtonClicked,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ViewGoalsBody(
    goalListUiState: GoalListUiState,
    onAddGoal: () -> Unit,
    onEditGoalsClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val orderedGoalList = goalListUiState.goalList.completedGoals() + goalListUiState.goalList.incompleteGoals()
        GoalList(
            goals = orderedGoalList,
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
@Preview(showBackground = true)
@Composable
fun ViewGoalsBodyPreview(){
    TimeManagementAppTheme {
        ViewGoalsBody(
            goalListUiState = GoalListUiState(listOf(
                Goal(0,1,0, "study", GoalStatus.NOT_STARTED),
                Goal(1,1,0, "sleep", GoalStatus.NOT_STARTED),
                Goal(2,3,0, "video games", GoalStatus.NOT_STARTED)
            )),
            onAddGoal = {},
            onEditGoalsClicked = {},
            modifier = Modifier
        )
    }
}
