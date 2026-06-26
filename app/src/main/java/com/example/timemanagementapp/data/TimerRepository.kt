package com.example.timemanagementapp.data

interface TimerRepository {
    fun scheduleTimer(goal: Goal)
    fun cancelTimer(goalId: Int)
}