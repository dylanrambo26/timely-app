package com.timelyproductivity.app.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

//Used to store the id of the goal that the user wants as the current task
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val CURRENT_TASK_ID = intPreferencesKey("current_task_id")

        val TASK_COMPLETION_NOTIFICATIONS_ENABLED = booleanPreferencesKey(
            "task_completion_notifications_enabled"
        )

        val COUNTDOWN_REMINDERS_ENABLED = booleanPreferencesKey(
            "countdown_reminders_enabled"
        )

        val COUNTDOWN_REMINDERS_MINUTES = stringSetPreferencesKey(
            "countdown_reminders_minutes"
        )

        val TASK_NOTIFICATION_SOUND_ENABLED = booleanPreferencesKey(
            "task_notification_sound_enabled"
        )

        const val TAG = "UserPreferencesRepo"
    }

    private val preferencesFlow = dataStore.data
        .catch {
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }

    val currentTaskID: Flow<Int?> =
        preferencesFlow.map { preferences ->
            preferences[CURRENT_TASK_ID]
        }

    val taskCompletionNotificationsEnabled: Flow<Boolean> =
        preferencesFlow.map {preferences ->
            preferences[TASK_COMPLETION_NOTIFICATIONS_ENABLED] ?: true
        }

    val countdownRemindersEnabled: Flow<Boolean> =
        preferencesFlow.map { preferences ->
            preferences[COUNTDOWN_REMINDERS_ENABLED] ?: false
        }

    val countdownRemindersMinutes: Flow<Set<Int>> =
        preferencesFlow.map { preferences ->
            preferences[COUNTDOWN_REMINDERS_MINUTES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: setOf(10, 5, 1)
        }

    val taskNotificationSoundEnabled: Flow<Boolean> =
        preferencesFlow.map { preferences ->
            preferences[TASK_NOTIFICATION_SOUND_ENABLED] ?: true
        }

    suspend fun saveCurrentTaskID(currentTaskID: Int){
        dataStore.edit { preferences ->
            preferences[CURRENT_TASK_ID] = currentTaskID
        }
    }

    suspend fun setTaskCompletionNotificationsEnabled(enabled: Boolean){
        dataStore.edit { preferences ->
            preferences[TASK_COMPLETION_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setCountdownRemindersEnabled(enabled: Boolean){
        dataStore.edit { preferences ->
            preferences[COUNTDOWN_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setCountdownRemindersMinutes(
        minutes: Set<Int>
    ){
        dataStore.edit { preferences ->
            preferences[COUNTDOWN_REMINDERS_MINUTES] =
                minutes
                    .filter { it > 0 }
                    .map { it.toString() }
                    .toSet()
        }
    }

    suspend fun setTaskNotificationSoundEnabled(enabled: Boolean){
        dataStore.edit { preferences ->
            preferences[TASK_NOTIFICATION_SOUND_ENABLED] = enabled
        }
    }
}