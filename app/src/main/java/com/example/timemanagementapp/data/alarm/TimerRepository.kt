package com.example.timemanagementapp.data.alarm

import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal

interface TimerRepository {
    fun scheduleCompletionAlarm(
        scheduledGoal: ScheduledGoal
    )

    fun cancelCompletionAlarm(
        scheduledGoalId: Int
    )

    fun scheduleCountdownReminders(
        scheduledGoal: ScheduledGoal,
        reminderMinutes: List<Int>
    )

    fun cancelCountdownReminders(
        scheduledGoalId: Int,
        reminderMinutes: List<Int>
    )
}