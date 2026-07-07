package com.example.timemanagementapp.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.completedGoal
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.timemanagementapp.data.testGoalsSizeThree
import com.example.timemanagementapp.ui.components.AddGoalButton
import com.example.timemanagementapp.ui.goal.GoalListUiState
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.components.GoalTemplateList

object AddExistingGoalDestination : NavigationDest{
    override val route = "add_existing_goals"
    override val titleRes = R.string.add_an_existing_goal
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun AddExistingGoalScreen(
    onAddGoalButtonClicked: () -> Unit = {},
    onCancelButtonClicked: () -> Unit = {},
    onCreateGoal: () -> Unit,
    viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    //viewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    //val scheduledGoalsListUiState by viewModel.scheduledGoalsListUiState.collectAsState()
    val goalListUiState by viewModel.goalListUiState.collectAsState()
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.add_an_existing_goal))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        AddExistingGoalBody(
            goalListUiState = goalListUiState,
            onAddGoal = onAddGoalButtonClicked,
            onCancelClicked = onCancelButtonClicked,
            modifier = Modifier.padding(innerPadding),
            onCreateGoal = onCreateGoal
        )
    }
}

@Composable
fun AddExistingGoalBody(
    goalListUiState: GoalListUiState,
    //goalsListUiState: ScheduledGoalsListUiState,
    onAddGoal: () -> Unit,
    onCreateGoal: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedGoalId by rememberSaveable { mutableStateOf<Int?>(null) }

        if (goalListUiState.goalList.isEmpty()){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = stringResource(R.string.no_generic_goals_exist_create_one),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
                Spacer(modifier = Modifier.height(16.dp))
                AddGoalButton(
                    onAddGoal = onCreateGoal,
                    text = stringResource(R.string.create_a_goal_from_scratch)
                )
            }
        } else {
            GoalTemplateList(
                goals = goalListUiState.goalList,
                onGoalClick = {goal ->
                    selectedGoalId = goal.goalID
                },
                selectedGoalId = selectedGoalId,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium_large)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),

            )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.33f)
        ) {
            //Cancel Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onCancelClicked()
                    }
                    .padding(12.dp)
                    .width(200.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ){
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        stringResource(R.string.Cancel),
                        textAlign = TextAlign.Center
                    )
                }
            }

            //Add Goal Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onAddGoal()
                    }
                    .padding(12.dp)
                    .width(200.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            ){
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        stringResource(R.string.add_goal),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AddExistingGoalBodyPreview(){
    TimeManagementAppTheme {
        AddExistingGoalBody(
            goalListUiState = GoalListUiState(
                goalList = listOf()
            ),
            onAddGoal = {},
            onCancelClicked = {},
            modifier = Modifier,
            onCreateGoal = {}
        )
    }
}