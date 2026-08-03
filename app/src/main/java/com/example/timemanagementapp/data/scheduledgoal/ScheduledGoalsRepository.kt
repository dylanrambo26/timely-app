package com.example.timemanagementapp.data.scheduledgoal

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ScheduledGoalsRepository{
    suspend fun insertScheduledGoal(scheduledGoal: ScheduledGoal)

    suspend fun updateScheduledGoal(scheduledGoal: ScheduledGoal)

    suspend fun deleteScheduledGoal(scheduledGoal: ScheduledGoal)

    suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal

    /*suspend fun getScheduledGoalsByEvent(eventId: Int): Flow<List<ScheduledGoal>>

    suspend fun getScheduledGoalById(goalId: Int): ScheduledGoal?

    suspend fun updateScheduledGoalStatus(id: Int, status: GoalStatus)

    suspend fun updateCompletedMillis(id: Int, millis: Long)*/

    fun getScheduledGoals(eventId: Int): Flow<List<ScheduledGoal>>

    fun getScheduledGoal(id: Int): Flow<ScheduledGoal>

    suspend fun getScheduledGoalsOnce(eventId: Int): List<ScheduledGoal>

    suspend fun validInsertScheduledGoal(goalId: Int, eventId: Int): Boolean

    suspend fun isValidDurationForDate(goalTotalMinutes: Int, eventId: Int, excludedScheduledGoalId: Int?): Boolean

    suspend fun updateFutureScheduledGoalsFromEditedTemplate(goalId: Int, title: String, hours: Int, minutes: Int, startDate: LocalDate)
}
