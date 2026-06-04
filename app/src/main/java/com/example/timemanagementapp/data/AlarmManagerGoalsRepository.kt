package com.example.timemanagementapp.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.work.WorkManager
import com.example.timemanagementapp.receiver.TimerReceiver

class WorkManagerGoalsRepository(
   private val context: Context
) : TimerRepository {
    companion object {
        private const val CURRENT_TASK_TIMER = "current_task_timer"
    }
    val alarmManager = context.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager

    override fun scheduleTimer(goal: Goal) {
        val durationMillis = (goal.hours * 60L + goal.minutes) * 60_000L

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (!alarmManager.canScheduleExactAlarms()){
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply{
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }

        val intent = Intent(
            context,
            TimerReceiver::class.java
        ).apply{
            putExtra("goalId", goal.goalID)
            putExtra("goalTitle", goal.goalTitle)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                goal.goalID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val triggerTime = System.currentTimeMillis() + durationMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    override fun cancelTimer(goalId: Int){
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(
            context,
            TimerReceiver::class.java
        )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                goalId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)
    }
}
