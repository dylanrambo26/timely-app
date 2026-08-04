package com.example.timemanagementapp.data.alarm

import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal

interface TimerRepository {
    fun scheduleTimer(scheduledGoal: ScheduledGoal)
    fun cancelTimer(scheduledGoalId: Int)
}