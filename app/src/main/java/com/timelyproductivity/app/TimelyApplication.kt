package com.timelyproductivity.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.timelyproductivity.app.data.AppContainer
import com.timelyproductivity.app.data.AppDataContainer
import com.timelyproductivity.app.data.notification.TimelyNotificationChannels

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