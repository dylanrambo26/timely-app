package com.example.timemanagementapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.timemanagementapp.TimelyApplication
import com.example.timemanagementapp.ui.analytics.AnalyticsViewModel
import com.example.timemanagementapp.ui.calendar.CalendarViewModel
import com.example.timemanagementapp.ui.createGoal.CreateGoalViewModel
import com.example.timemanagementapp.ui.currenttask.CurrentTaskViewModel
import com.example.timemanagementapp.ui.editReusable.EditReusableGoalViewModel
import com.example.timemanagementapp.ui.editScheduled.EditScheduledGoalViewModel
import com.example.timemanagementapp.ui.goal.GoalListViewModel
import com.example.timemanagementapp.ui.goal.ManageReusableGoalsViewModel
import com.example.timemanagementapp.ui.home.HomeViewModel
import com.example.timemanagementapp.ui.viewgoals.ScheduledGoalsListViewModel


//View model factory for each view model in Timely App
object AppViewModelProvider{
    val Factory = viewModelFactory {

        initializer {
            GoalListViewModel(
                timelyApplication().container.goalsRepository,
            )
        }

        initializer {
            CreateGoalViewModel(
                this.createSavedStateHandle(),
                timelyApplication().container.goalsRepository,
                timelyApplication().container.scheduledGoalsRepository,
                timelyApplication().container.calendarEventsRepository
            )
        }

        initializer {
            EditScheduledGoalViewModel(
                this.createSavedStateHandle(),
                timelyApplication().container.scheduledGoalsRepository
            )
        }

        initializer {
            CurrentTaskViewModel(
                timelyApplication().container.userPreferencesRepository,
                timelyApplication().container.scheduledGoalsRepository,
                timelyApplication().container.alarmManagerGoalsRepository
            )
        }

        initializer {
            CalendarViewModel(
                timelyApplication().container.calendarEventsRepository,
                timelyApplication().container.scheduledGoalsRepository
            )
        }

        initializer {
            ScheduledGoalsListViewModel(
                this.createSavedStateHandle(),
                timelyApplication().container.scheduledGoalsRepository,
                timelyApplication().container.calendarEventsRepository
            )
        }

        initializer {
            HomeViewModel(
                timelyApplication().container.calendarEventsRepository,
            )
        }

        initializer {
            EditReusableGoalViewModel(
                this.createSavedStateHandle(),
                timelyApplication().container.goalsRepository,
                timelyApplication().container.scheduledGoalsRepository
            )
        }
        initializer {
            AnalyticsViewModel(
                timelyApplication().container.analyticsRepository
            )
        }

        initializer {
            ManageReusableGoalsViewModel(
                goalsRepository = timelyApplication().container.goalsRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [TimelyApplication].
 */
fun CreationExtras.timelyApplication(): TimelyApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TimelyApplication)