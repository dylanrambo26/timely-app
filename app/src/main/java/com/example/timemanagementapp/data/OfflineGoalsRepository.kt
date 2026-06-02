package com.example.timemanagementapp.data

import kotlinx.coroutines.flow.Flow

class OfflineGoalsRepository(private val goalDao: GoalDao): GoalsRepository{
    override fun getAllGoalsStream(): Flow<List<Goal>> = goalDao.getAllGoals()

    override fun getGoalStream(id: Int): Flow<Goal?> = goalDao.getGoal(id)

    override suspend fun getGoalOnce(id: Int): Goal = goalDao.getGoalOnce(id)

    override fun getTotalMinutesStream(): Flow<Int> = goalDao.getSumOfTotalMinutes()

    override suspend fun insertGoal(goal: Goal) = goalDao.insert(goal)

    override suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)

    override suspend fun updateGoal(goal: Goal) = goalDao.update(goal)

    //override suspend fun incrementCompletedMinutes(id: Int) = goalDao.incrementCompletedMinutes(id)
}