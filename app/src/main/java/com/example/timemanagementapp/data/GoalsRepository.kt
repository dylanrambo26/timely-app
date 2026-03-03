package com.example.timemanagementapp.data

import kotlinx.coroutines.flow.Flow

interface GoalsRepository {

    fun getAllGoalsStream(): Flow<List<Goal>>

    fun getGoalStream(id: Int): Flow<Goal?>

    fun getTotalMinutesStream(): Flow<Int>

    suspend fun insertGoal(goal: Goal)

    suspend fun deleteGoal(goal: Goal)

    suspend fun updateGoal(goal: Goal)
}