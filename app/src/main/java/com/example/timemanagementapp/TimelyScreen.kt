package com.example.timemanagementapp

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.timemanagementapp.ui.GoalsViewModel
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.composable
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.timemanagementapp.data.TestData
import com.example.timemanagementapp.data.TimeDuration
import com.example.timemanagementapp.ui.AddLogScreen
import com.example.timemanagementapp.ui.HomeScreen

/**
 * @param title - String Res value so that title refers to a string resource for navigation
 *
 * Each value corresponds to a UI screen to be used by the navController
 */
enum class TimelyScreen(@StringRes val title: Int){
    Home(title = R.string.home_screen),
    Calendar(title = R.string.calendar),
    Analytics(title = R.string.analytics),
    Settings(title = R.string.settings),
    AddLog(title = R.string.add_log)
}

/**
 * @param viewModel - Currently attached to a GoalsViewModel but multiple view models may be used later
 * @param navController - A navController used to navigate between UI screens
 * Each value corresponds to a UI screen to be used by the navController
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelyApp(
    viewModel: GoalsViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
){
    val backStackEntry by navController.currentBackStackEntryAsState() //Last screen visited
    val currentScreen = TimelyScreen.valueOf(
        backStackEntry?.destination?.route ?: TimelyScreen.Home.name
    )
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
                        text = "Welcome, User",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    //Settings Button
                    IconButton(
                        onClick = ({ /*TODO*/ })) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings Button"
                        )
                    }
                }

            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        //Calendar Button
                        IconButton(
                            onClick = ({/*TODO*/ })) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = "Settings Button",
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        Spacer(modifier = Modifier.width(64.dp))

                        //Home Button
                        IconButton(
                            onClick = {navController.navigate(TimelyScreen.Home.name)}
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Home,
                                contentDescription = "Settings Button",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(64.dp))

                        //Analytics Page Button
                        IconButton(
                            onClick = ({ /*TODO*/ })
                        )
                        {
                            Image(
                                painter = painterResource(id = R.drawable.graph_image),
                                contentDescription = "Settings Button",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }
            )
        }

    ){  innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = TimelyScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            //Home Screen composable
            composable(route = TimelyScreen.Home.name){
                HomeScreen(
                    goalsText = uiState.goals.joinToString("\n"){"${it.goalTitle} - ${it.timeLimit.toReadable()}"},
                    onAddButtonClicked = {navController.navigate(TimelyScreen.AddLog.name)},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            //Add Log composable
            composable(route = TimelyScreen.AddLog.name){
                AddLogScreen(
                    onUserHourChanged = {viewModel.updateNewUserHours(it)},
                    onUserMinutesChanged = {viewModel.updateNewUserMinutes(it)},
                    onUserGoalTitleChanged = {viewModel.updateNewUserGoalTitle(it)},
                    onClearButtonPressed = {viewModel.cleanGoalFields()},
                    onAddGoalButtonPressed = { title, hours, minutes ->
                        viewModel.addGoal(title, hours, minutes)
                    },
                    userHours = viewModel.userNewHours,
                    userMinutes = viewModel.userNewMinutes,
                    userGoalTitle = viewModel.userNewGoalTitle,

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
        }

    }
}

//Helper to convert TimeDuration to a string
private fun TimeDuration.toReadable(): String{
    return buildString {
        if (hours > 0) append("${hours}h")
        if (minutes > 0) append("${minutes}m")
    }.trim()
}