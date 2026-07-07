package com.example.timemanagementapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import com.example.timemanagementapp.ui.edit.EditGoalsDestination
import com.example.timemanagementapp.ui.home.HomeDestination
import com.example.timemanagementapp.ui.home.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.add.AddExistingGoalDestination
import com.example.timemanagementapp.ui.add.AddExistingGoalScreen
import com.example.timemanagementapp.ui.add.AddGoalSelectionDestination
import com.example.timemanagementapp.ui.add.AddGoalSelectionScreen
import com.example.timemanagementapp.ui.calendar.CalendarDestination
import com.example.timemanagementapp.ui.calendar.CalendarScreen
import com.example.timemanagementapp.ui.calendar.CalendarViewModel
import com.example.timemanagementapp.ui.currenttask.CurrentTaskDestination
import com.example.timemanagementapp.ui.currenttask.CurrentTaskScreen
import com.example.timemanagementapp.ui.createGoal.CreateGoalDestination
import com.example.timemanagementapp.ui.createGoal.CreateGoalScreen
import com.example.timemanagementapp.ui.edit.EditGoalDestination
import com.example.timemanagementapp.ui.edit.EditGoalsScreen
import com.example.timemanagementapp.ui.edit.EditOneGoalScreen
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListViewModel
import com.example.timemanagementapp.ui.viewgoals.ViewGoalsDestination
import com.example.timemanagementapp.ui.viewgoals.ViewGoalsScreen

//Parent back-stack entry for goalListViewModel in order to use the same instance on the home and edit goals screens
object GoalListGraph{
    const val route = "goals_graph"
}

@Composable
fun TimelyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = GoalListGraph.route,
        modifier = modifier
    ){
        navigation(
            route = GoalListGraph.route,
            startDestination = HomeDestination.route
        ){
            composable(route = HomeDestination.route){ backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry(GoalListGraph.route)
                }
                val sharedViewModel: GoalListViewModel =
                    viewModel(parentEntry, factory = AppViewModelProvider.Factory)

                HomeScreen(
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToSettings = {/*TODO*/},
                    navigateToAnalytics = {/*TODO*/},
                    navigateToViewGoals = {navController.navigate(ViewGoalsDestination.route)},
                    navigateToChangeCurrentTask = {navController.navigate(CurrentTaskDestination.route)},
                    goalListViewModel = sharedViewModel
                )
            }
            composable(route = CalendarDestination.route){ backStackEntry ->
                val viewModel: CalendarViewModel = viewModel(
                    viewModelStoreOwner = backStackEntry,
                    factory = AppViewModelProvider.Factory
                )
                
                CalendarScreen(
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    onViewGoalsClicked = {eventId ->
                        navController.navigate("${ViewGoalsDestination.route}/$eventId")},
                    viewModel = viewModel
                )
            }
            composable(route = EditGoalsDestination.route){ backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry(GoalListGraph.route)
                }
                val sharedViewModel: ScheduledGoalsListViewModel =
                    viewModel(parentEntry, factory = AppViewModelProvider.Factory)

                EditGoalsScreen(
                    onAddGoalButtonClicked = {navController.navigate(CreateGoalDestination.route)},
                    onEditGoal = { navController.navigate("${EditGoalDestination.route}/${it.scheduledGoal.scheduledGoalId}")},
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    viewModel = sharedViewModel
                )
            }
            composable(
                route = EditGoalDestination.routeWithArgs,
                arguments = listOf(navArgument(EditGoalDestination.goalIdArg) {
                    type = NavType.IntType
                })
            ) {
                EditOneGoalScreen(
                    navigateBack = {navController.popBackStack()},
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {/*TODO*/},
                    navigateToAnalytics = {/*TODO*/},
                )
            }
            composable(
                route = CreateGoalDestination.route
            ){ backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(GoalListGraph.route)
                }
                /*val sharedViewModel: ScheduledGoalsListViewModel =
                    viewModel(parentEntry, factory = AppViewModelProvider.Factory)*/

                CreateGoalScreen(
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    //scheduledGoalsListViewModel = sharedViewModel
                )
            }
            composable(
                route = CurrentTaskDestination.route
            ){ backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry(GoalListGraph.route)
                }
                val sharedViewModel: ScheduledGoalsListViewModel =
                    viewModel(parentEntry, factory = AppViewModelProvider.Factory)

                //possibly share currentTaskViewmodel between this screen and home for consistency
                CurrentTaskScreen(
                    scheduledGoalsListViewModel = sharedViewModel,
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    navigateBack = {navController.popBackStack()}
                )
            }
            composable(
                route = ViewGoalsDestination.routeWithArgs,
                arguments = listOf(
                    navArgument(ViewGoalsDestination.eventIdArg){
                        type = NavType.IntType
                    }
                )
            ){
                val viewModel: ScheduledGoalsListViewModel =
                    viewModel(factory = AppViewModelProvider.Factory)

                ViewGoalsScreen(
                    onAddGoalButtonClicked = { eventId ->
                        navController.navigate("${AddGoalSelectionDestination.route}/$eventId")
                    },
                    onEditGoalsButtonClicked = { eventId ->
                        navController.navigate("${EditGoalsDestination.route}/$eventId")
                    },
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    viewModel = viewModel
                )
            }
            composable(
                route = AddGoalSelectionDestination.routeWithArgs,
                arguments = listOf(
                    navArgument(AddGoalSelectionDestination.eventIdArg){
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val eventId = checkNotNull(
                    backStackEntry.arguments?.getInt(AddGoalSelectionDestination.eventIdArg)
                )

                AddGoalSelectionScreen(
                    addExistingGoalButtonClicked = {
                        navController.navigate("${AddExistingGoalDestination.route}/$eventId")
                    },
                    createGoalButtonClicked = {
                        navController.navigate("${CreateGoalDestination.route}/$eventId")
                    },
                    returnToEditGoalsClicked = {
                        navController.popBackStack()
                    },
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                )
            }
            composable(
                route = AddExistingGoalDestination.routeWithArgs,
                arguments = listOf(
                    navArgument(AddExistingGoalDestination.eventIdArg){
                        type = NavType.IntType
                    }
                )
            ) {
                val viewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory)

                AddExistingGoalScreen(
                    onAddGoalButtonClicked = {navController.popBackStack()},
                    onCancelButtonClicked = {navController.popBackStack()},
                    viewModel = viewModel,
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    onCreateGoal = {navController.navigate(CreateGoalDestination.route)}
                )
            }
        }
    }
}