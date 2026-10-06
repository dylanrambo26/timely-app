package com.timelyproductivity.app.ui.categories

import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.ui.components.settings.ReminderEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryEditorState {
    private val _uiState = MutableStateFlow(CategoryEditorUiState())

    val uiState: StateFlow<CategoryEditorUiState> = _uiState.asStateFlow()

    fun openAddCategoryDialog(){
        _uiState.value = CategoryEditorUiState(isVisible = true)
    }

    fun openEditCategoryDialog(category: GoalCategory){
        _uiState.value =
            CategoryEditorUiState(
                isVisible = true,
                input = category.name,
                selectedColor = category.color,
                originalCategoryId = category.categoryId
            )
    }

    fun updateCategoryInput(input: String){
        _uiState.value =
            _uiState.value.copy(
                input = input,
                errorMessage = null
            )
    }

    fun updateSelectedColor(color: CategoryColor){
        _uiState.value = _uiState.value.copy(
            selectedColor = color
        )
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message
        )
    }

    fun closeCategoryDialog(){
        _uiState.value =
            CategoryEditorUiState()
    }

    fun validateCategoryInput(
        existingCategories: List<GoalCategory>
    ): String?{
        val editorState = uiState.value
        val name = editorState.input.trim()

        val error = when {
            name.isBlank() ->
                "Category name cannot be empty."

            existingCategories.any{category ->
                category.categoryId != editorState.originalCategoryId && category.name.equals(name, ignoreCase = true)
            } ->
                "A category with that name already exists."
            else -> null
        }

        if(error != null){
            setError(error)
            return null
        }

        return name
    }
}


data class CategoryEditorUiState(
    val isVisible: Boolean = false,
    val input: String = "",
    val selectedColor: CategoryColor = CategoryColor.BLUE,
    val originalCategoryId: Int? = null,
    val errorMessage: String? = null
)