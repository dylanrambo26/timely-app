package com.timelyproductivity.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageCategoriesViewModel(
    private val goalCategoriesRepository: GoalCategoriesRepository
): ViewModel(), CategoryEditorActions{

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val categoryEditor = CategoryEditorState()
    val categoryEditorUiState = categoryEditor.uiState

    val categories: StateFlow<List<GoalCategory>> =
        goalCategoriesRepository.getCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )


    override fun openAddCategoryEditor() {
        categoryEditor.openAddCategoryDialog()
    }

    override fun updateCategoryName(input: String) {
        categoryEditor.updateCategoryInput(input)
    }

    override fun updateCategoryColor(color: CategoryColor) {
        categoryEditor.updateSelectedColor(color)
    }

    override fun closeCategoryDialog() {
        categoryEditor.closeCategoryDialog()
    }

    override fun saveCategory() {
        val editorState = categoryEditor.uiState.value

        val name = categoryEditor.validateCategoryInput(
            existingCategories = categories.value
        ) ?: return

        val category = GoalCategory(
            categoryId = editorState.originalCategoryId ?: 0,
            name = name,
            color = editorState.selectedColor
        )

        viewModelScope.launch {
            if (editorState.originalCategoryId == null) {
                goalCategoriesRepository.insertCategory(category)
            } else {
                goalCategoriesRepository.updateCategory(category)
            }

            categoryEditor.closeCategoryDialog()
        }
    }

    fun openEditCategoryEditor(category: GoalCategory){
        categoryEditor.openEditCategoryDialog(category)
    }

    fun deleteCategory(category: GoalCategory){
        viewModelScope.launch {
            goalCategoriesRepository.deleteCategory(category)
        }
    }

}

data class ManageCategoriesUiState(
    val categories: List<GoalCategory> = emptyList()
)