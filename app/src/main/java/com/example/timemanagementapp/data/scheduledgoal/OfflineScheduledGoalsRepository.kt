package com.example.timemanagementapp.data.scheduledgoal

import com.example.timemanagementapp.data.goal.GoalStatus
import kotlinx.coroutines.flow.Flow

class OfflineScheduledGoalsRepository(private val scheduledGoalDao: ScheduledGoalDao): ScheduledGoalsRepository {
    override suspend fun insertScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.insert(scheduledGoal)

    override suspend fun updateScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.update(scheduledGoal)

    override suspend fun deleteScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.delete(scheduledGoal)

    override suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal = scheduledGoalDao.getScheduledGoalOnce(id)

    /*override suspend fun getScheduledGoalsByEvent(eventId: Int): Flow<List<ScheduledGoal>> = scheduledGoalDao.getScheduledGoalsByEvent(eventId)

    override suspend fun getScheduledGoalById(goalId: Int): ScheduledGoal? = scheduledGoalDao.getScheduledGoalById(goalId)

    override suspend fun updateScheduledGoalStatus(id: Int, status: GoalStatus) = scheduledGoalDao.updateScheduledGoalStatus(id, status)

    override suspend fun updateCompletedMillis(id: Int, millis: Long) = scheduledGoalDao.updateCompletedMillis(id, millis)*/

    override fun getScheduledGoalsWithGoal(id: Int): Flow<List<ScheduledGoalWithGoal>> = scheduledGoalDao.getScheduledGoalsWithGoals(id)

    override fun getScheduledGoalWithGoal(id: Int): Flow<ScheduledGoalWithGoal> = scheduledGoalDao.getScheduledGoalWithGoal(id)
}