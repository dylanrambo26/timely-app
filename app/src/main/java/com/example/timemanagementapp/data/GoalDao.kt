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

    //Get all goals in ascending order of least amount of time to highest remaining
    @Query("SELECT * from goals ORDER BY goalTitle ASC")
    fun getAllGoals(): Flow<List<Goal>>

    //COALESCE used to turn null values to 0 in order to handle nulls when table is empty
    @Query("SELECT COALESCE(SUM(hours * 60 + minutes), 0) FROM goals")
    fun getSumOfTotalMinutes(): Flow<Int>
}