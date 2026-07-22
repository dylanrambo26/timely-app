package com.example.timemanagementapp.data.scheduledgoal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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


    @Query("SELECT * from scheduled_goals WHERE scheduledGoalId = :id")
    suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE eventId = :eventId
        """
    )
    fun getScheduledGoals(eventId: Int): Flow<List<ScheduledGoal>>

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE scheduledGoalId = :id
        """
    )
    fun getScheduledGoal(id: Int): Flow<ScheduledGoal>

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE eventId = :eventId
        """
    )
    suspend fun getScheduledGoalsOnce(eventId: Int): List<ScheduledGoal>

}