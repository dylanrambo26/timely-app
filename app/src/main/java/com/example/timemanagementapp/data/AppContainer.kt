package com.example.timemanagementapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore



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
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val goalsRepository: GoalsRepository by lazy {
        OfflineGoalsRepository(GoalsDatabase.getDatabase(context).goalDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

    override val alarmManagerGoalsRepository: AlarmManagerGoalsRepository by lazy {
        AlarmManagerGoalsRepository(context)
    }
}