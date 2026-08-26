package com.example.timemanagementapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.timemanagementapp.data.alarm.AlarmManagerGoalsRepository
import com.example.timemanagementapp.data.analytics.AnalyticsRepository
import com.example.timemanagementapp.data.analytics.OfflineAnalyticsRepository
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.calendar.OfflineCalendarEventsRepository
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalsDatabase
import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.goal.OfflineGoalsRepository
import com.example.timemanagementapp.data.scheduledgoal.OfflineScheduledGoalsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository


private const val CURRENT_TASK_PREFERENCE_NAME = "current_task_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CURRENT_TASK_PREFERENCE_NAME
)

/**
 * App container for Dependency injection.
 */
interface AppContainer{
    val goalsRepository: GoalsRepository
    val userPreferencesRepository: UserPreferencesRepository
    val alarmManagerGoalsRepository: AlarmManagerGoalsRepository
    val scheduledGoalsRepository: ScheduledGoalsRepository
    val calendarEventsRepository: CalendarEventsRepository
    val analyticsRepository: AnalyticsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: GoalsDatabase by lazy {
        GoalsDatabase.getDatabase(context)
    }

    override val goalsRepository: GoalsRepository by lazy {
        OfflineGoalsRepository(
            goalDao = database.goalDao(),
            recurrenceRuleDao = database.recurrenceRuleDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

    override val alarmManagerGoalsRepository: AlarmManagerGoalsRepository by lazy {
        AlarmManagerGoalsRepository(context)
    }

    override val calendarEventsRepository: CalendarEventsRepository by lazy {
        OfflineCalendarEventsRepository(database.calendarEventDao())
    }

    override val scheduledGoalsRepository: ScheduledGoalsRepository by lazy {
        OfflineScheduledGoalsRepository(
            database.scheduledGoalDao(),
            database.goalDao(),
            database.recurrenceRuleDao(),
            calendarEventsRepository = calendarEventsRepository,
        )
    }
    override val analyticsRepository: AnalyticsRepository by lazy {
        OfflineAnalyticsRepository(database.analyticsDao())
    }
}