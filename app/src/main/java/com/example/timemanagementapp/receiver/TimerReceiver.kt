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
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.goal.GoalsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimerReceiver : BroadcastReceiver(){
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val scheduledGoalId = intent.getIntExtra("scheduledGoalId", -1)
        val goalTitle = intent.getStringExtra("scheduledGoalTitle") ?: return

        Log.d("TimerReceiver", "Timer finished for $goalTitle")

        CoroutineScope(Dispatchers.IO).launch{
            val db = GoalsDatabase.getDatabase(context)
            val scheduledGoal = db.scheduledGoalDao().getScheduledGoalOnce(scheduledGoalId)
            //val goal = db.goalDao().getGoalOnce(scheduledGoal.goalId)
            db.scheduledGoalDao().update(scheduledGoal.copy(
                completedMillis = (scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60_000L,
                startTimeMillis = 0L,
                status = GoalStatus.COMPLETED
            ))
        }

        val canPostNotifications =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if(canPostNotifications){
            showTaskFinishedNotification(scheduledGoalId = scheduledGoalId, goalTitle = goalTitle, context = context)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showTaskFinishedNotification(
        scheduledGoalId: Int,
        goalTitle: String,
        context: Context
    ){
        val notification = NotificationCompat.Builder(
            context,
            "task_timer_channel"
        )
            .setSmallIcon(R.drawable.outline_calendar_check_24)
            .setContentTitle("Task Complete")
            .setContentText("Your \"$goalTitle\" task is done.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(scheduledGoalId, notification)
    }
}