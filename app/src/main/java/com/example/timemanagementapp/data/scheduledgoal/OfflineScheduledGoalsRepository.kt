package com.example.timemanagementapp.data.scheduledgoal

import com.example.timemanagementapp.data.goal.GoalDao
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.Flow

class OfflineScheduledGoalsRepository(
    private val scheduledGoalDao: ScheduledGoalDao,
    private val goalDao: GoalDao
): ScheduledGoalsRepository {
    override suspend fun insertScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.insert(scheduledGoal)

    override suspend fun updateScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.update(scheduledGoal)

    override suspend fun deleteScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.delete(scheduledGoal)

    override suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal = scheduledGoalDao.getScheduledGoalOnce(id)

    override fun getScheduledGoalsWithGoal(eventId: Int): Flow<List<ScheduledGoalWithGoal>> = scheduledGoalDao.getScheduledGoalsWithGoals(eventId)

    override fun getScheduledGoalWithGoal(id: Int): Flow<ScheduledGoalWithGoal> = scheduledGoalDao.getScheduledGoalWithGoal(id)

    override suspend fun getScheduledGoalsWithGoalsOnce(eventId: Int): List<ScheduledGoalWithGoal> = scheduledGoalDao.getScheduledGoalsWithGoalsOnce(eventId)

    override suspend fun validInsertScheduledGoal(goalId: Int, eventId: Int): Boolean {
        val goal = goalDao.getGoalOnce(goalId)

        val scheduledGoals = getScheduledGoalsWithGoalsOnce(eventId)
        val usedMinutes = scheduledGoals.sumOf { it.goal.hours * 60 + it.goal.minutes }

        val remainingMinutes = MINUTES_IN_24_HOUR_DAY - usedMinutes
        val newScheduledGoalTotalMinutes = goal.hours * 60 + goal.minutes

        if (newScheduledGoalTotalMinutes > remainingMinutes){
            return false
        }

        insertScheduledGoal(
            ScheduledGoal(
                goalId = goalId,
                eventId = eventId
            )
        )

        return true
    }
}