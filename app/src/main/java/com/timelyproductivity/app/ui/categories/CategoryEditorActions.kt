package com.timelyproductivity.app.ui.categories

import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategory

interface CategoryEditorActions {
    fun openAddCategoryEditor()
    fun updateCategoryName(input: String)
    fun updateCategoryColor(color: CategoryColor)
    fun closeCategoryDialog()
    fun saveCategory()
}