package com.example.timemanagementapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val CURRENT_TASK_ID = intPreferencesKey("current_task_id")
        const val TAG = "UserPreferencesRepo"
    }

    val currentTaskID: Flow<Int?> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
        preferences[CURRENT_TASK_ID]
    }

    suspend fun saveCurrentTaskID(currentTaskID: Int){
        dataStore.edit { preferences ->
            preferences[CURRENT_TASK_ID] = currentTaskID
        }
    }

    suspend fun getCurrentTaskID(): Int?{
        return currentTaskID.first()
    }
}