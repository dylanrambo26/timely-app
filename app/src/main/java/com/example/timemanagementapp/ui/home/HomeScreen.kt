package com.example.timemanagementapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
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
import com.example.timemanagementapp.ui.components.TimeFilled
import com.example.timemanagementapp.ui.components.TimeRemaining
import com.example.timemanagementapp.ui.navigation.NavigationDest
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.timemanagementapp.ui.goal.GoalListUiState
//import com.example.timemanagementapp.data.TestData
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
    modifier: Modifier = Modifier,
    viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val goalListUiState by viewModel.goalListUiState.collectAsState()
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
            goalListUiState = goalListUiState,
            modifier = modifier.padding(innerPadding),
            onEditButtonClicked = navigateToEditGoals,
            remaining = goalListUiState.remainingMinutesInDay
        )
}}

/**
 * @param goalsText - The list of current goals from the uiState in a joined String
 * @param onEditButtonClicked - function that will navigate to Add Log screen when clicked
 * @param modifier
 */
@Composable
fun HomeBody(
    goalListUiState: GoalListUiState,
    onEditButtonClicked: () -> Unit = {},
    remaining: Int,
    modifier: Modifier = Modifier
){
    Column (
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        //verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text= "Today's Goals:",
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
            items(goalListUiState.goalList){goal ->
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text= "${goal.goalTitle} - ${goal.hours}h ${goal.minutes}m"
                )
            }
        }
        TimeFilled(filled = (60 * 24) - remaining) //Total amount of minutes in a day - free time
        TimeRemaining(remaining = remaining)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            thickness = 4.dp
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            //Add Log Button
            IconButton (
                onClick = onEditButtonClicked,
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit today's goals",
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.edit_todays_goals),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview(){
    TimeManagementAppTheme{
        HomeBody(
            goalListUiState = GoalListUiState(listOf()),
            remaining = 870,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}


