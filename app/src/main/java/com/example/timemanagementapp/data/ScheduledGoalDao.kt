package com.example.timemanagementapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledGoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledGoal: ScheduledGoal)

    @Update
    suspend fun update(scheduledGoal: ScheduledGoal)

    @Delete
    suspend fun delete(scheduledGoal: ScheduledGoal)

    @Query(
        """
            SELECT * FROM scheduled_goals WHERE eventId = :eventId
        """
    )
    suspend fun getScheduledGoalsByEvent(
        eventId: Int
    ): Flow<List<ScheduledGoal>>

    @Query("""
        SELECT * FROM scheduled_goals WHERE goalId = :goalId
    """)
    suspend fun getScheduledGoalById(
        goalId: Int
    ): ScheduledGoal?

    @Query(
        """
            UPDATE scheduled_goals
            SET status = :status
            WHERE scheduledGoalId = :id
        """
    )
    suspend fun updateScheduledGoalStatus(
        id: Int,
        status: GoalStatus
    )

    @Query(
        """
            UPDATE scheduled_goals
            SET completedMillis = :millis
            WHERE scheduledGoalId = :id
        """
    )
    suspend fun updateCompletedMillis(
        id: Int,
        millis: Long
    )
}