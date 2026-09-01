package com.example.timemanagementapp.data.goal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.timemanagementapp.data.goal.recurrence.GoalWithRecurrence
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("SELECT * from goals WHERE goalID = :id")
    fun getGoal(id: Int): Flow<Goal>

    @Query("SELECT * from goals WHERE goalID = :id")
    suspend fun getGoalOnce(id: Int): Goal

    //Get all goals in ascending order of goalIds
    @Query("SELECT * from goals ORDER BY goalID ASC")
    fun getAllGoals(): Flow<List<Goal>>

    //COALESCE used to turn null values to 0 in order to handle nulls when table is empty
    @Query("SELECT COALESCE(SUM(hours * 60 + minutes), 0) FROM goals")
    fun getSumOfTotalMinutes(): Flow<Int>

    //Get the goals that are recurring
    /*@Query("""
        SELECT goals.*,
            EXISTS(
                SELECT 1
                FROM recurrence_rules
                WHERE recurrence_rules.goalId = goals.goalID
            ) AS isRecurring
        FROM goals
    """
    )
    fun getGoalsWithRecurrenceStatus(): Flow<List<GoalWithRecurrenceStatus>>*/

    @Transaction
    @Query(
        """
            SELECT *
            FROM goals
            ORDER BY goalID ASC
        """
    )
    fun getAllGoalsWithRecurrence(): Flow<List<GoalWithRecurrence>>
}