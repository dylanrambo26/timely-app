package com.example.timemanagementapp.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.timemanagementapp.R

class TimerReceiver : BroadcastReceiver(){
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val goalId = intent.getIntExtra("goalId", -1)
        val goalTitle = intent.getStringExtra("goalTitle") ?: return

        Log.d("TimerReceiver", "Timer finished for $goalTitle")

        showTaskFinishedNotification(goalId = goalId, goalTitle = goalTitle, context = context)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showTaskFinishedNotification(
        goalId: Int,
        goalTitle: String,
        context: Context
    ){
        val notification = NotificationCompat.Builder(
            context,
            "task_timer_channel"
        )
            .setSmallIcon(R.drawable.outline_calendar_check_24)
            .setContentTitle("Task Complete")
            .setContentText("Your $goalTitle task is done.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(goalId, notification)
    }
}