package com.timelyproductivity.app.data.goal.category

import kotlinx.coroutines.flow.Flow

class OfflineGoalCategoriesRepository(
    private val goalCategoriesDao: GoalCategoryDao
): GoalCategoriesRepository {
    override fun getCategories(): Flow<List<GoalCategory>> = goalCategoriesDao.getAllGoalCategories()

    override suspend fun updateCategory(category: GoalCategory) = goalCategoriesDao.update(category)

    override suspend fun deleteCategory(category: GoalCategory) = goalCategoriesDao.delete(category)

    override suspend fun insertCategory(category: GoalCategory): Long = goalCategoriesDao.insert(category)
}