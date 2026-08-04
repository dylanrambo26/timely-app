package com.example.timemanagementapp.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.receiver.TimerReceiver

//Used by CurrentTaskViewModel to schedule alarm notifications when the task is done
class AlarmManagerGoalsRepository(
   private val context: Context
) : TimerRepository {
    companion object {
        private const val CURRENT_TASK_TIMER = "current_task_timer"
    }
    val alarmManager = context.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager

    override fun scheduleTimer(scheduledGoal: ScheduledGoal) {
        val scheduledGoal = scheduledGoal

        val durationMillis = ((scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60_000L) - scheduledGoal.completedMillis

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
            putExtra("scheduledGoalId", scheduledGoal.scheduledGoalId)
            putExtra("scheduledGoalTitle", scheduledGoal.scheduledGoalTitle)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                scheduledGoal.scheduledGoalId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val triggerTime = System.currentTimeMillis() + durationMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        Log.d(CURRENT_TASK_TIMER, "timer set")
    }

    //Cancel the alarm broadcast
    override fun cancelTimer(scheduledGoalId: Int){
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
                scheduledGoalId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)

        Log.d(CURRENT_TASK_TIMER, "timer canceled")
    }
}