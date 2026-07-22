package com.example.timemanagementapp.data.scheduledgoal

import com.example.timemanagementapp.data.goal.GoalDao
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

    override fun getScheduledGoals(eventId: Int): Flow<List<ScheduledGoal>> = scheduledGoalDao.getScheduledGoals(eventId)

    override fun getScheduledGoal(id: Int): Flow<ScheduledGoal> = scheduledGoalDao.getScheduledGoal(id)

    override suspend fun getScheduledGoalsOnce(eventId: Int): List<ScheduledGoal> = scheduledGoalDao.getScheduledGoalsOnce(eventId)

    //Used to validate existing goals against remaining time
    override suspend fun validInsertScheduledGoal(goalId: Int, eventId: Int): Boolean {
        val goal = goalDao.getGoalOnce(goalId)

        val scheduledGoals = getScheduledGoalsOnce(eventId)
        val usedMinutes = scheduledGoals.sumOf { it.scheduledHours * 60 + it.scheduledMinutes }

        val remainingMinutes = MINUTES_IN_24_HOUR_DAY - usedMinutes
        val newScheduledGoalTotalMinutes = goal.hours * 60 + goal.minutes

        if (newScheduledGoalTotalMinutes > remainingMinutes){
            return false
        }

        insertScheduledGoal(
            ScheduledGoal(
                goalId = goalId,
                eventId = eventId,
                scheduledGoalTitle = goal.goalTitle,
                scheduledHours = goal.hours,
                scheduledMinutes = goal.minutes
            )
        )

        return true
    }

    //Used to validate new/edited goals against remaining time
    override suspend fun isValidDurationForDate(
        goalTotalMinutes: Int,
        eventId: Int,
        excludedScheduledGoalId: Int? //to exclude existing scheduled goal total when editing a scheduled goal
    ): Boolean {
        val scheduledGoals = getScheduledGoalsOnce(eventId)
        val usedMinutes = scheduledGoals
            .filter {it.scheduledGoalId != excludedScheduledGoalId}
            .sumOf {
                val hours = it.scheduledHours
                val minutes = it.scheduledMinutes

                hours * 60 + minutes
            }

        return usedMinutes + goalTotalMinutes <= MINUTES_IN_24_HOUR_DAY
    }
}