package com.timelyproductivity.app.data.goal.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalCategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(goalCategory: GoalCategory): Long

    @Update
    suspend fun update(goalCategory: GoalCategory)

    @Delete
    suspend fun delete(goalCategory: GoalCategory)

    @Query(
        """
            SELECT *
            FROM goal_categories
            ORDER BY name ASC
        """
    )
    fun getAllGoalCategories(): Flow<List<GoalCategory>>
}