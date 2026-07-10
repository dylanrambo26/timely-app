package com.example.timemanagementapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import com.example.timemanagementapp.ui.edit.EditScheduledGoalsDestination
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
import com.example.timemanagementapp.ui.createGoal.CreateGoalViewModel
import com.example.timemanagementapp.ui.currenttask.CurrentTaskViewModel
import com.example.timemanagementapp.ui.edit.EditScheduledGoalDestination
import com.example.timemanagementapp.ui.edit.EditScheduledGoalsScreen
import com.example.timemanagementapp.ui.edit.EditScheduledGoalScreen
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.home.HomeViewModel
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
            composable(route = HomeDestination.route)
            { backStackEntry ->
                val currentTaskViewModel: CurrentTaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)

                HomeScreen(
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToSettings = {/*TODO*/},
                    navigateToAnalytics = {/*TODO*/},
                    navigateToViewGoals = {eventId ->
                        navController.navigate("${ViewGoalsDestination.route}/$eventId")
                    },
                    navigateToChangeCurrentTask = {navController.navigate(CurrentTaskDestination.route)},
                    homeViewModel = homeViewModel,
                    currentTaskViewModel = currentTaskViewModel
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
            composable(
                route = EditScheduledGoalsDestination.routeWithArgs,
                arguments = listOf(
                    navArgument(EditScheduledGoalsDestination.eventIdArg){
                        type = NavType.IntType
                    }
                )
            ){ backStackEntry ->
                val viewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory)
                EditScheduledGoalsScreen(
                    onAddGoalButtonClicked = { eventId ->
                        navController.navigate("${AddGoalSelectionDestination.route}/$eventId")
                    },
                    onEditGoal = { navController.navigate("${EditScheduledGoalDestination.route}/${it.scheduledGoal.scheduledGoalId}")},
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    viewModel = viewModel
                )
            }
            composable(
                route = EditScheduledGoalDestination.routeWithArgs,
                arguments = listOf(navArgument(EditScheduledGoalDestination.scheduledGoalIdArg) {
                    type = NavType.IntType
                })
            ) {
                EditScheduledGoalScreen(
                    navigateBack = {navController.popBackStack()},
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {/*TODO*/},
                    navigateToAnalytics = {/*TODO*/},
                )
            }
            composable(
                route = CreateGoalDestination.routeWithArgs,
                arguments = listOf(
                    navArgument(CreateGoalDestination.eventIdArg){
                        type = NavType.IntType
                    }
                )
            ){

                val createGoalViewModel: CreateGoalViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val goalListViewModel: GoalListViewModel = viewModel(factory = AppViewModelProvider.Factory)
                CreateGoalScreen(
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    createGoalViewModel = createGoalViewModel,
                    goalListViewModel = goalListViewModel,
                    navigateToViewGoals = {eventId ->
                        navController.navigate("${ViewGoalsDestination.route}/$eventId")},
                    navigateBack = {navController.popBackStack()}
                )
            }
            composable(
                route = CurrentTaskDestination.route
            ){
                val scheduledGoalsListViewModel: ScheduledGoalsListViewModel = viewModel(factory = AppViewModelProvider.Factory)

                //possibly share currentTaskViewmodel between this screen and home for consistency
                CurrentTaskScreen(
                    scheduledGoalsListViewModel = scheduledGoalsListViewModel,
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
                        navController.navigate("${EditScheduledGoalsDestination.route}/$eventId")
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
                    goalListViewModel = viewModel,
                    navigateToHome = {navController.navigate(HomeDestination.route)},
                    navigateToCalendar = {navController.navigate(CalendarDestination.route)},
                    navigateToAnalytics = {/*TODO*/},
                    navigateToViewGoals = { eventId->
                        navController.navigate("${ViewGoalsDestination.route}/$eventId")
                    },
                    navigateToCreateGoal = { eventId ->
                        navController.navigate("${CreateGoalDestination.route}/$eventId")
                    }
                )
            }
        }
    }
}