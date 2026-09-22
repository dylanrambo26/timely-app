package com.timelyproductivity.app.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.receiver.TimerReceiver

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

    fun canScheduleExactAlarms(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()
    }

    fun requestExactAlarmPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun scheduleExactAlarm(
        scheduledGoal: ScheduledGoal,
        triggerTime: Long,
        requestCode: Int,
        reminderMinutes: Int? = null
    ) {
        val intent = Intent(
            context,
            TimerReceiver::class.java
        ).apply{
            putExtra("scheduledGoalId", scheduledGoal.scheduledGoalId)
            putExtra("scheduledGoalTitle", scheduledGoal.scheduledGoalTitle)
            putExtra("reminderMinutes", reminderMinutes)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        Log.d(
            "TaskAlarm",
            "Scheduling requestCode=$requestCode at $triggerTime"
        )
    }

    //Cancel the alarm broadcast
    fun cancelExactAlarm(
        requestCode: Int
    ){
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
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)

        Log.d("TaskAlarm",
            "Cancelling requestCode=$requestCode"
        )
    }

    override fun scheduleCompletionAlarm(
        scheduledGoal: ScheduledGoal
    ){
        val completionTime = calculateCompletionTimeMillis(scheduledGoal)
        scheduleExactAlarm(
            scheduledGoal = scheduledGoal,
            triggerTime = completionTime,
            requestCode = completionRequestCode(
                scheduledGoal.scheduledGoalId
            )
        )
    }

    override fun cancelCompletionAlarm(
        scheduledGoalId: Int
    ){
        cancelExactAlarm(
            completionRequestCode(
                scheduledGoalId
            )
        )
    }

    override fun scheduleCountdownReminders(
        scheduledGoal: ScheduledGoal,
        reminderMinutes: List<Int>
    ) {
        val completionTime = calculateCompletionTimeMillis(scheduledGoal)

        reminderMinutes.forEachIndexed {index, minutes ->
            val triggerTime = calculateCountdownReminderTimeMillis(
                completionTime,
                minutes
            )

            if(triggerTime <= System.currentTimeMillis()){
                return@forEachIndexed
            }

            scheduleExactAlarm(
                scheduledGoal,
                triggerTime,
                requestCode = countdownReminderRequestCode(
                    scheduledGoal.scheduledGoalId,
                    minutes
                ),
                reminderMinutes = minutes
            )
        }
    }



    override fun cancelCountdownReminders(
        scheduledGoalId: Int,
        reminderMinutes: List<Int>
    ){
        reminderMinutes.forEach{minutes->
            cancelExactAlarm(
                countdownReminderRequestCode(
                    scheduledGoalId,
                    minutes
                )
            )
        }
    }

    private fun completionRequestCode(
        scheduledGoalId: Int
    ): Int{
        return "$scheduledGoalId-completion".hashCode()
    }

    private fun countdownReminderRequestCode(
        scheduledGoalId: Int,
        reminderMinutes: Int
    ): Int{
        return "$scheduledGoalId-reminder-$reminderMinutes".hashCode()
    }

    private fun calculateRemainingDurationMillis(
        scheduledGoal: ScheduledGoal
    ): Long {
        return ((scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60_000L) - scheduledGoal.completedMillis
    }

    private fun calculateCompletionTimeMillis(
        scheduledGoal: ScheduledGoal
    ): Long{
        return System.currentTimeMillis() + calculateRemainingDurationMillis(scheduledGoal)
    }

    private fun calculateCountdownReminderTimeMillis(
        completionTimeMillis: Long,
        reminderMinutes: Int
    ): Long{
        return completionTimeMillis - reminderMinutes * 60_000L
    }
}