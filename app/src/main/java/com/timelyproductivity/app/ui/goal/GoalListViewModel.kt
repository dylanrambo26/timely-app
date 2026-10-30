package com.timelyproductivity.app.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.recurrence.GoalWithRecurrence
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.filters.applyFilters
import com.timelyproductivity.app.ui.sorting.applySorting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GoalListViewModel(
    private val goalsRepository: GoalsRepository,
    goalCategoriesRepository: GoalCategoriesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel(){
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _filters = MutableStateFlow(GoalFilterState())
    val filters: StateFlow<GoalFilterState> = combine(
        _filters,
        userPreferencesRepository.goalSortOption,
        userPreferencesRepository.goalSortAscending
    ){ localFilters, sortOption, sortAscending ->
        localFilters.copy(
            sortOption = sortOption,
            sortAscending = sortAscending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = GoalFilterState()
    )

    val goalListUiState: StateFlow<GoalListUiState> =
        combine(
            goalsRepository.getAllGoalsWithRecurrence(),
            goalsRepository.getTotalMinutesStream(),
            goalCategoriesRepository.getCategories(),
            userPreferencesRepository.useCategoryColorsEnabled
        ){ goals, totalMinutes, goalCategories, useCategoryColorsEnabled ->
            GoalListUiState(
                goalList = goals,
                categories = goalCategories,
                totalMinutes = totalMinutes,
                useCategoryColorsEnabled = useCategoryColorsEnabled
            )
        }.combine(filters){uiState, filters ->
            uiState.copy(
                filteredGoalList = uiState.goalList
                    .applyFilters(filters)
                    .applySorting(
                        sortOption = filters.sortOption,
                        ascending = filters.sortAscending,
                        categories = uiState.categories
                    )
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = GoalListUiState()
        )

    fun deleteGoal(goal: Goal){
        viewModelScope.launch {
            goalsRepository.deleteGoal(goal)
        }
    }

    fun updateFilters(newFilters: GoalFilterState){
        val currentFilters = filters.value

        _filters.value = newFilters

        viewModelScope.launch {
            if(newFilters.sortOption != currentFilters.sortOption){
                userPreferencesRepository.setGoalSortOption(
                    newFilters.sortOption
                )
            }

            if(newFilters.sortAscending != currentFilters.sortAscending){
                userPreferencesRepository.setGoalSortAscending(
                    newFilters.sortAscending
                )
            }
        }
    }
}

data class GoalListUiState(
    val goalList: List<GoalWithRecurrence> = listOf(),
    val filteredGoalList: List<GoalWithRecurrence> = listOf(),
    val categories: List<GoalCategory> = listOf(),
    val useCategoryColorsEnabled: Boolean = false,
    val totalMinutes: Int = 0,
)