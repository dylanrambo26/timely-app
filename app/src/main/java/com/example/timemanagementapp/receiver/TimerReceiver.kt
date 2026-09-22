package com.example.timemanagementapp.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyApplication
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.goal.GoalsDatabase
import com.example.timemanagementapp.data.notification.TimelyNotificationChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TimerReceiver : BroadcastReceiver(){
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val scheduledGoalId = intent.getIntExtra("scheduledGoalId", -1)
        val goalTitle = intent.getStringExtra("scheduledGoalTitle") ?: return
        val reminderMinutes = intent.getIntExtra("reminderMinutes", -1)


        if(scheduledGoalId == -1){
            return
        }

        Log.d("TimerReceiver", "Timer finished for $goalTitle")

        //Ensure room update finishes before onReceive finishes
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch{
            try{
                if (reminderMinutes == -1){
                    handleCompletion(
                        context = context,
                        scheduledGoalId = scheduledGoalId,
                        goalTitle = goalTitle
                    )
                } else {
                    handleCountdownReminder(
                        context = context,
                        scheduledGoalId = scheduledGoalId,
                        goalTitle = goalTitle,
                        reminderMinutes = reminderMinutes
                    )
                }
            } catch (exception: Exception){
                Log.e(
                    "TimerReceiver",
                    "Failed to complete task timer",
                    exception
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun handleCompletion(
        context: Context,
        scheduledGoalId: Int,
        goalTitle: String
    ){
        val db = GoalsDatabase.getDatabase(context)
        val scheduledGoal = db.scheduledGoalDao().getScheduledGoalOnce(scheduledGoalId) ?: return

        db.scheduledGoalDao()
            .update(
                scheduledGoal.copy(
                    completedMillis = (scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60_000L,
                    startTimeMillis = 0L,
                    status = GoalStatus.COMPLETED
                )
            )

        val application = context.applicationContext as TimelyApplication

        val completionNotificationsEnabled = application.container.userPreferencesRepository.taskCompletionNotificationsEnabled.first()

        val notificationSettings = getNotificationSettings(context)
        if(
            completionNotificationsEnabled
            && notificationSettings.notificationsAllowed
        ){
            showNotification(
                scheduledGoalId = scheduledGoalId,
                context = context,
                channelId = notificationSettings.channelId,
                priority = notificationSettings.priority,
                setSilent = !notificationSettings.soundEnabled,
                iconResource = R.drawable.outline_calendar_check_24,
                contentTitle = "Task Complete",
                contentText = "Your \"$goalTitle\" task is done."
            )
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun handleCountdownReminder(
        context: Context,
        scheduledGoalId: Int,
        goalTitle: String,
        reminderMinutes: Int
    ){
        val notificationSettings = getNotificationSettings(context)

        if(!notificationSettings.notificationsAllowed){
            return
        }

        showNotification(
            scheduledGoalId = scheduledGoalId,
            context = context,
            channelId = notificationSettings.channelId,
            priority = notificationSettings.priority,
            setSilent = !notificationSettings.soundEnabled,
            iconResource = R.drawable.outline_hourglass,
            contentTitle = "Task Reminder",
            contentText = "$reminderMinutes minutes remaining on task: \"$goalTitle\""
        )
    }

    private suspend fun getNotificationSettings(
        context: Context
    ): NotificationSettings {
        val application = context.applicationContext as TimelyApplication
        val userPreferencesRepository = application.container.userPreferencesRepository

        val hasNotificationPermission =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        val systemNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

        val soundEnabled = userPreferencesRepository.taskNotificationSoundEnabled.first()

        val channelId = if(soundEnabled){
            TimelyNotificationChannels.TASK_ALERTS_SOUND
        } else {
            TimelyNotificationChannels.TASK_ALERTS_SILENT
        }

        val priority = if(soundEnabled){
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_LOW
        }

        return NotificationSettings(
            notificationsAllowed = hasNotificationPermission && systemNotificationsEnabled,
            soundEnabled = soundEnabled,
            channelId = channelId,
            priority = priority
        )
    }

    private data class NotificationSettings(
        val notificationsAllowed: Boolean,
        val soundEnabled: Boolean,
        val channelId: String,
        val priority: Int
    )

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(
        scheduledGoalId: Int,
        context: Context,
        channelId: String,
        priority: Int,
        setSilent: Boolean,
        iconResource: Int,
        contentTitle: String,
        contentText: String
    ){
        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(iconResource)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(priority)
            .setAutoCancel(true)
            .setSilent(setSilent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(scheduledGoalId, notification)
    }
}