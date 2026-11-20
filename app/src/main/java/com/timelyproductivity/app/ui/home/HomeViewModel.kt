package com.timelyproductivity.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.calendar.CalendarEventsRepository
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val calendarEventsRepository: CalendarEventsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val goalCategoriesRepository: GoalCategoriesRepository
) : ViewModel(){

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val homeUiState: StateFlow<HomeUiState> = combine(
        goalCategoriesRepository.getCategories(),
        userPreferencesRepository.useCategoryColorsEnabled
    ){categories, useCategoryColors ->
        HomeUiState(
            goalCategories = categories,
            useCategoryColorsEnabled = useCategoryColors
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    fun viewGoalsForToday(onNavigate: (Int) -> Unit){
        viewModelScope.launch {
            val eventId = calendarEventsRepository.getOrCreateEventIdForDate(LocalDate.now())
            onNavigate(eventId)
        }
    }
}

data class HomeUiState(
    val goalCategories: List<GoalCategory> = emptyList(),
    val useCategoryColorsEnabled: Boolean = false,
)