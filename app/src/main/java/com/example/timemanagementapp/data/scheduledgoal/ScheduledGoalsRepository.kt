package com.example.timemanagementapp.data.scheduledgoal

import com.example.timemanagementapp.data.goal.GoalStatus
import kotlinx.coroutines.flow.Flow

interface ScheduledGoalsRepository{
    suspend fun insertScheduledGoal(scheduledGoal: ScheduledGoal)

    suspend fun updateScheduledGoal(scheduledGoal: ScheduledGoal)

    suspend fun deleteScheduledGoal(scheduledGoal: ScheduledGoal)

    suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal

    /*suspend fun getScheduledGoalsByEvent(eventId: Int): Flow<List<ScheduledGoal>>

    suspend fun getScheduledGoalById(goalId: Int): ScheduledGoal?

    suspend fun updateScheduledGoalStatus(id: Int, status: GoalStatus)

    suspend fun updateCompletedMillis(id: Int, millis: Long)*/

    fun getScheduledGoalsWithGoal(id: Int): Flow<List<ScheduledGoalWithGoal>>

    fun getScheduledGoalWithGoal(id: Int): Flow<ScheduledGoalWithGoal>
}
