package com.example.timemanagementapp.data

interface TimerRepository {
    fun scheduleTimerJob(goal: Goal)
    fun cancelWork()
}