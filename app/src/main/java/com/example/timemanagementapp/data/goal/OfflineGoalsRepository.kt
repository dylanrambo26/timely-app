package com.example.timemanagementapp.data.goal

import androidx.room.withTransaction
import com.example.timemanagementapp.data.goal.recurrence.GoalWithRecurrence
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceRule
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceRuleDao
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalDao
import kotlinx.coroutines.flow.Flow

class OfflineGoalsRepository(
    private val goalDao: GoalDao,
    private val recurrenceRuleDao: RecurrenceRuleDao,
    private val scheduledGoalDao: ScheduledGoalDao,
    private val database: GoalsDatabase
): GoalsRepository {
    override fun getAllGoalsStream(): Flow<List<Goal>> = goalDao.getAllGoals()

    override fun getGoalStream(id: Int): Flow<Goal?> = goalDao.getGoal(id)

    override suspend fun getGoalOnce(id: Int): Goal = goalDao.getGoalOnce(id)

    override fun getTotalMinutesStream(): Flow<Int> = goalDao.getSumOfTotalMinutes()

    override suspend fun insertGoal(goal: Goal): Int{
        return goalDao.insert(goal).toInt()
    }

    override suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)

    override suspend fun deleteGoalAndScheduledGoals(goal: Goal){
        database.withTransaction {
            scheduledGoalDao.deleteScheduledGoalsByGoalId(goal.goalID)
            goalDao.delete(goal)
        }
    }

    override suspend fun updateGoal(goal: Goal) = goalDao.update(goal)

    override suspend fun insertRecurrenceRule(recurrenceRule: RecurrenceRule): Long{
        return recurrenceRuleDao.insertRecurrenceRule(recurrenceRule)
    }

    override suspend fun updateRecurrenceRule(recurrenceRule: RecurrenceRule) = recurrenceRuleDao.updateRecurrenceRule(recurrenceRule)

    override suspend fun deleteRecurrenceRule(recurrenceRule: RecurrenceRule) = recurrenceRuleDao.deleteRecurrenceRule(recurrenceRule)

    override fun getAllGoalsWithRecurrence(): Flow<List<GoalWithRecurrence>> = goalDao.getAllGoalsWithRecurrence()

    /*override suspend fun updateGoalStatus(id: Int, status: GoalStatus) = goalDao.updateGoalStatus(id, status)

    override suspend fun updateCompletedMillis(id: Int, millis: Long) = goalDao.updateCompletedMillis(id, millis)*/
}