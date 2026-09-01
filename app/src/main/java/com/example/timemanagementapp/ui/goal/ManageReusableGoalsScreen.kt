package com.example.timemanagementapp.ui.goal

//import com.example.timemanagementapp.data.TestData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.testGoalsSizeThree
import com.example.timemanagementapp.data.testGoalsWithRecurrenceSizeThree
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.components.lists.GoalTemplateList
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

object ManageReusableGoalsDestination : NavigationDest{
    override val route = "manage_reusable_goals"
    override val titleRes = R.string.manage_reusable_goals
}

@Composable
fun ManageReusableGoalsScreen(
    onAddGoalButtonClicked: () -> Unit = {},
    onEditGoal: (Goal) -> Unit,
    viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val goalListUiState by viewModel.goalListUiState.collectAsState()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.manage_reusable_goals))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        ManageReusableGoalsBody(
            onCreateGoalButtonClicked = onAddGoalButtonClicked,
            goalListUiState = goalListUiState,
            onDeleteGoal = {goal -> viewModel.deleteGoal(goal)},
            onEditGoal = onEditGoal,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ManageReusableGoalsBody(
    onCreateGoalButtonClicked: () -> Unit = {},
    goalListUiState: GoalListUiState,
    onDeleteGoal: (Goal) -> Unit,
    onEditGoal: (Goal) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GoalTemplateList(
            goals = goalListUiState.goalList,
            onEditGoal = onEditGoal,
            onDeleteGoal = onDeleteGoal,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                onClick = onCreateGoalButtonClicked,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(100.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.create_a_goal_from_scratch),
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = stringResource(R.string.create_a_goal_from_scratch),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ManageReusableGoalsBodyPreview(){
    TimeManagementAppTheme {
        ManageReusableGoalsBody(
            goalListUiState = GoalListUiState(
                goalList = testGoalsWithRecurrenceSizeThree
            ),
            onDeleteGoal = {},
            onEditGoal = {},
        )
    }
}