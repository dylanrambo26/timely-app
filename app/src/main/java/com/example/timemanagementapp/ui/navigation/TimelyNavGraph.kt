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
import com.example.timemanagementapp.ui.edit.AddGoalDestination
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.edit.AddGoalScreen
import com.example.timemanagementapp.ui.edit.EditGoalDestination
import com.example.timemanagementapp.ui.edit.EditGoalsScreen
import com.example.timemanagementapp.ui.edit.EditOneGoalScreen
import com.example.timemanagementapp.ui.goal.GoalListViewModel

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
                    navigateToCalendar = {/*TODO*/},
                    navigateToSettings = {/*TODO*/},
                    navigateToAnalytics = {/*TODO*/},
                    navigateToEditGoals = {navController.navigate(EditGoalsDestination.route)},
                    viewModel = sharedViewModel
                )
            }
            composable(route = EditGoalsDestination.route){ backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry(GoalListGraph.route)
                }
                val sharedViewModel: GoalListViewModel =
                    viewModel(parentEntry, factory = AppViewModelProvider.Factory)

                EditGoalsScreen(
                    onAddGoalButtonClicked = {navController.navigate(AddGoalDestination.route)},
                    onEditGoal = { navController.navigate("${EditGoalDestination.route}/${it.goalID}")},
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {/*TODO*/},
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
                route = AddGoalDestination.route
            ){
                AddGoalScreen(
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {/*TODO*/},
                    navigateToAnalytics = {/*TODO*/},
                )
            }
        }
    }
}