package com.example.timemanagementapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.timemanagementapp.data.AppContainer
import com.example.timemanagementapp.data.AppDataContainer
import com.example.timemanagementapp.data.UserPreferencesRepository
import com.example.timemanagementapp.data.notification.TimelyNotificationChannels

class TimelyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(
            context = this,
        )
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return
        }

        val soundChannel = NotificationChannel(
            TimelyNotificationChannels.TASK_ALERTS_SOUND,
            "Task alerts with sound",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Task countdown reminders and completion alerts with sound"
        }

        val silentChannel = NotificationChannel(
            TimelyNotificationChannels.TASK_ALERTS_SILENT,
            "Silent task alerts",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Task countdown reminders and completion alerts without sound"

            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )

        notificationManager.createNotificationChannels(
            listOf(
                soundChannel,
                silentChannel
            )
        )
    }
}