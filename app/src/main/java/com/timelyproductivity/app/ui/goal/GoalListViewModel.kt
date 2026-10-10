package com.timelyproductivity.app.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.recurrence.GoalWithRecurrence
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GoalListViewModel(
    private val goalsRepository: GoalsRepository,
    goalCategoriesRepository: GoalCategoriesRepository,
    userPreferencesRepository: UserPreferencesRepository
): ViewModel(){
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
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
}

data class GoalListUiState(
    val goalList: List<GoalWithRecurrence> = listOf(),
    val categories: List<GoalCategory> = listOf(),
    val useCategoryColorsEnabled: Boolean = false,
    val totalMinutes: Int = 0,
)