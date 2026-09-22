package com.timelyproductivity.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.timelyproductivity.app.data.alarm.AlarmManagerGoalsRepository
import com.timelyproductivity.app.data.analytics.AnalyticsRepository
import com.timelyproductivity.app.data.analytics.OfflineAnalyticsRepository
import com.timelyproductivity.app.data.calendar.CalendarEventsRepository
import com.timelyproductivity.app.data.calendar.OfflineCalendarEventsRepository
import com.timelyproductivity.app.data.goal.GoalsDatabase
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.goal.OfflineGoalsRepository
import com.timelyproductivity.app.data.goal.recurrence.CreateRecurrenceUseCase
import com.timelyproductivity.app.data.goal.recurrence.UpdateRecurrenceUseCase
import com.timelyproductivity.app.data.scheduledgoal.OfflineScheduledGoalsRepository
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository


private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
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
    val createRecurrenceUseCase: CreateRecurrenceUseCase
    val updateRecurrenceUseCase: UpdateRecurrenceUseCase
}

class AppDataContainer(
    private val context: Context
) : AppContainer {

    private val database: GoalsDatabase by lazy {
        GoalsDatabase.getDatabase(context)
    }

    override val goalsRepository: GoalsRepository by lazy {
        OfflineGoalsRepository(
            goalDao = database.goalDao(),
            recurrenceRuleDao = database.recurrenceRuleDao(),
            scheduledGoalDao = database.scheduledGoalDao(),
            database = database
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

    override val createRecurrenceUseCase: CreateRecurrenceUseCase by lazy {
        CreateRecurrenceUseCase(
            goalsRepository = goalsRepository,
            scheduledGoalsRepository = scheduledGoalsRepository
        )
    }

    override val updateRecurrenceUseCase: UpdateRecurrenceUseCase by lazy {
        UpdateRecurrenceUseCase(
            goalsRepository = goalsRepository,
            scheduledGoalsRepository = scheduledGoalsRepository
        )
    }
}