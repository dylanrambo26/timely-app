package com.example.timemanagementapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(goal: Goal)

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

    //Set the completed field of the goal to true
    @Query(
        """
            UPDATE goals
            SET completed = 1
            WHERE goalID = :id
        """
    )
    suspend fun markGoalCompleted(id: Int)

    //Get all goals that are completed in ascending order of goalIds
    /*@Query(
        """
            SELECT * from goals
            WHERE completed = 1
            ORDER BY goalID ASC
        """
    )
    fun getCompletedGoals(): Flow<List<Goal>>*/

    /*//Increment the completed minutes by 1, will happen every minute passed
    @Query("""
        UPDATE goals
        SET completedMinutes = completedMinutes + 1
        WHERE goalID = :id
    """)
    suspend fun incrementCompletedMinutes(id: Int)*/
}