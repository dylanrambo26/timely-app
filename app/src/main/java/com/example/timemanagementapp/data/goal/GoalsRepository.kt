package com.example.timemanagementapp.data.goal

import kotlinx.coroutines.flow.Flow

interface GoalsRepository {

    fun getAllGoalsStream(): Flow<List<Goal>>

    fun getGoalStream(id: Int): Flow<Goal?>

    suspend fun getGoalOnce(id: Int): Goal

    fun getTotalMinutesStream(): Flow<Int>

    suspend fun insertGoal(goal: Goal): Int

    suspend fun deleteGoal(goal: Goal)

    suspend fun updateGoal(goal: Goal)

    suspend fun insertRecurrenceRule(recurrenceRule: RecurrenceRule): Long
    suspend fun updateRecurrenceRule(recurrenceRule: RecurrenceRule)
    suspend fun deleteRecurrenceRule(recurrenceRule: RecurrenceRule)

    /*suspend fun updateGoalStatus(id: Int, status: GoalStatus)

    suspend fun updateCompletedMillis(id: Int, millis: Long)*/
}