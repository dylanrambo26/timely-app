package com.example.timemanagementapp.ui.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import java.time.LocalDate

object AddGoalSelectionDestination : NavigationDest{
    override val route = "add_goal_selection"
    override val titleRes = R.string.Add_a_goal
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@Composable
fun AddGoalSelectionScreen(
    addExistingGoalButtonClicked: () -> Unit = {},
    createGoalButtonClicked: () -> Unit = {},
    returnToEditGoalsClicked: () -> Unit = {},
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.Add_a_goal))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) { innerPadding ->
        AddGoalSelectionBody(
            addExistingGoalClicked = addExistingGoalButtonClicked,
            createGoalClicked = createGoalButtonClicked,
            navigateBack = returnToEditGoalsClicked,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AddGoalSelectionBody(
    addExistingGoalClicked: () -> Unit,
    createGoalClicked: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SelectionButton(
            action = addExistingGoalClicked,
            text = stringResource(R.string.add_an_existing_goal)
        )
        SelectionButton(
            action = createGoalClicked,
            text = stringResource(R.string.create_a_goal_from_scratch)
        )
        SelectionButton(
            action = navigateBack,
            text = stringResource(R.string.return_to_edit_goals)
        )
    }
}

@Composable
fun SelectionButton(
    action: () -> Unit,
    text: String
){
    Surface(
        modifier = Modifier
            .clickable {
                action()
            }
            .padding(16.dp)
            .width(300.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ){
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddGoalSelectionScreenPreview(){
    TimeManagementAppTheme {
        AddGoalSelectionScreen(
            addExistingGoalButtonClicked = {},
            createGoalButtonClicked = {},
            returnToEditGoalsClicked = {},
            navigateToHome = {},
            navigateToCalendar = {},
            navigateToAnalytics = {}
        )
    }
}