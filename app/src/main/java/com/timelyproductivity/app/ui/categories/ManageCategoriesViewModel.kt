package com.timelyproductivity.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageCategoriesViewModel(
    private val goalCategoriesRepository: GoalCategoriesRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val goalsRepository: GoalsRepository,
): ViewModel(), CategoryEditorActions{

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val categoryEditor = CategoryEditorState()
    val categoryEditorUiState = categoryEditor.uiState

    private val _categoryPendingDeletion = MutableStateFlow<GoalCategory?>(null)
    val categoryPendingDeletion = _categoryPendingDeletion.asStateFlow()

    private val _sortOption = MutableStateFlow(CategorySortOption.NAME)
    private val _sortDescending = MutableStateFlow(false)

    val manageCategoriesUiState: StateFlow<ManageCategoriesUiState> = combine(
        goalCategoriesRepository.getCategories(),
        _sortOption,
        _sortDescending,
        goalsRepository.getGoalCountsByCategory(),
        scheduledGoalsRepository.getScheduledGoalCountsByCategory(),
    ) { categories, sortOption, sortDescending, goalCounts, scheduledGoalCounts ->

        val goalCountMap = goalCounts.associate {
            it.categoryId to it.goalCount
        }

        val scheduledGoalCountMap = scheduledGoalCounts.associate {
            it.categoryId to it.goalCount
        }

        val sortedCategories = when (sortOption) {
            CategorySortOption.NAME ->
                categories.sortedBy { it.name.lowercase() }

            CategorySortOption.COLOR ->
                categories.sortedBy { it.color.ordinal }

            CategorySortOption.GOAL_COUNT ->
                categories.sortedBy {
                    goalCountMap[it.categoryId] ?: 0
                }

            CategorySortOption.SCHEDULED_GOAL_COUNT ->
                categories.sortedBy {
                    scheduledGoalCountMap[it.categoryId] ?: 0
                }

        }

        ManageCategoriesUiState(
            categories = if (sortDescending) {
                sortedCategories.reversed()
            } else {
                sortedCategories
            },
            sortOption = sortOption,
            sortDescending = sortDescending,
            goalCounts = goalCountMap,
            scheduledGoalCounts = scheduledGoalCountMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ManageCategoriesUiState()
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
            existingCategories = manageCategoriesUiState.value.categories
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

    fun requestDeleteCategory(category: GoalCategory) {
        _categoryPendingDeletion.value = category
    }

    fun cancelDeleteCategory() {
        _categoryPendingDeletion.value = null
    }

    fun confirmDeleteCategory() {
        val category = _categoryPendingDeletion.value ?: return

        viewModelScope.launch {
            goalCategoriesRepository.deleteCategory(category)
            _categoryPendingDeletion.value = null
        }
    }

    fun updateSortOption(option: CategorySortOption) {
        _sortOption.value = option
    }

    fun updateSortDescending(descending: Boolean) {
        _sortDescending.value = descending
    }
}

data class ManageCategoriesUiState(
    val categories: List<GoalCategory> = emptyList(),
    val sortOption: CategorySortOption = CategorySortOption.NAME,
    val sortDescending: Boolean = false,
    val goalCounts: Map<Int, Int> = emptyMap(),
    val scheduledGoalCounts: Map<Int, Int> = emptyMap()
)