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
        val goalTitle = intent.getStringExtra("goalTitle") ?: return

        Log.d("TimerReceiver", "Timer finished for $goalTitle")

        CoroutineScope(Dispatchers.IO).launch{
            val db = GoalsDatabase.getDatabase(context)
            val scheduledGoal = db.scheduledGoalDao().getScheduledGoalOnce(scheduledGoalId)
            val goal = db.goalDao().getGoalOnce(scheduledGoal.goalId)
            db.scheduledGoalDao().update(scheduledGoal.copy(
                completedMillis = (goal.hours * 60L + goal.minutes) * 60_000L,
                startTimeMillis = 0L,
                status = GoalStatus.COMPLETED
            ))
        }

        showTaskFinishedNotification(scheduledGoalId = scheduledGoalId, goalTitle = goalTitle, context = context)
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