package com.example.timemanagementapp.data.alarm

import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalWithGoal

interface TimerRepository {
    fun scheduleTimer(scheduledGoalWithGoal: ScheduledGoalWithGoal)
    fun cancelTimer(scheduledGoalId: Int)
}