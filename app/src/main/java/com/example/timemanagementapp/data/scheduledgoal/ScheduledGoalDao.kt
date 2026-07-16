package com.example.timemanagementapp.data.scheduledgoal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus
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
    fun getScheduledGoalsWithGoals(eventId: Int): Flow<List<ScheduledGoalWithGoal>>

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE scheduledGoalId = :id
        """
    )
    fun getScheduledGoalWithGoal(id: Int): Flow<ScheduledGoalWithGoal>

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE eventId = :eventId
        """
    )
    suspend fun getScheduledGoalsWithGoalsOnce(eventId: Int): List<ScheduledGoalWithGoal>

}