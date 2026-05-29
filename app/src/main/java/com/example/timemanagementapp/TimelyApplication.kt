package com.example.timemanagementapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.timemanagementapp.data.AppContainer
import com.example.timemanagementapp.data.AppDataContainer
import com.example.timemanagementapp.data.UserPreferencesRepository
import androidx.datastore.preferences.core.Preferences


private const val CURRENT_TASK_PREFERENCE_NAME = "current_task_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CURRENT_TASK_PREFERENCE_NAME
)

class TimelyApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}