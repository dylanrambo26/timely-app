package com.timelyproductivity.app.data.goal.category

import kotlinx.coroutines.flow.Flow

interface GoalCategoriesRepository {
    suspend fun insertCategory(category: GoalCategory): Long
    suspend fun updateCategory(category: GoalCategory)
    suspend fun deleteCategory(category: GoalCategory)
    fun getCategories(): Flow<List<GoalCategory>>
}