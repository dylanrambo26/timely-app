package com.timelyproductivity.app.data.alarm

import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal

interface TimerRepository {
    fun scheduleCompletionAlarm(
        scheduledGoal: ScheduledGoal
    )

    fun cancelCompletionAlarm(
        scheduledGoalId: Int
    )

    fun scheduleCountdownReminders(
        scheduledGoal: ScheduledGoal,
        reminderMinutes: Set<Int>
    )

    fun cancelCountdownReminders(
        scheduledGoalId: Int,
        reminderMinutes: Set<Int>
    )
}