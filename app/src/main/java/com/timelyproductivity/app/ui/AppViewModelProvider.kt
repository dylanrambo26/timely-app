package com.timelyproductivity.app.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.timelyproductivity.app.TimelyApplication
import com.timelyproductivity.app.ui.analytics.AnalyticsViewModel
import com.timelyproductivity.app.ui.calendar.CalendarViewModel
import com.timelyproductivity.app.ui.createGoal.CreateGoalViewModel
import com.timelyproductivity.app.ui.currenttask.CurrentTaskViewModel
import com.timelyproductivity.app.ui.editReusable.EditReusableGoalViewModel
import com.timelyproductivity.app.ui.editScheduled.EditScheduledGoalViewModel
import com.timelyproductivity.app.ui.goal.GoalListViewModel
import com.timelyproductivity.app.ui.goal.ManageReusableGoalsViewModel
import com.timelyproductivity.app.ui.home.HomeViewModel
import com.timelyproductivity.app.ui.settings.SettingsViewModel
import com.timelyproductivity.app.ui.viewgoals.ScheduledGoalsListViewModel


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
                timelyApplication().container.calendarEventsRepository,
                timelyApplication().container.createRecurrenceUseCase
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
                timelyApplication().container.scheduledGoalsRepository,
                timelyApplication().container.createRecurrenceUseCase,
                timelyApplication().container.updateRecurrenceUseCase
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

        initializer {
            SettingsViewModel(
                userPreferencesRepository = timelyApplication().container.userPreferencesRepository
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