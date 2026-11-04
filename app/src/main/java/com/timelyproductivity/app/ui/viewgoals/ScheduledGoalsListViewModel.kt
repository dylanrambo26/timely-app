package com.timelyproductivity.app.ui.viewgoals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.calendar.CalendarEventsRepository
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.filters.applyFilters
import com.timelyproductivity.app.ui.sorting.applySorting
import com.timelyproductivity.app.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduledGoalsListViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val goalCategoriesRepository: GoalCategoriesRepository
): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val eventIdFromRoute: Int? = savedStateHandle[ViewGoalsDestination.eventIdArg]
    var calendarEventId: Int = 0
        private set
    private val _calendarEventId = MutableStateFlow<Int?>(null)
    private val _date = MutableStateFlow<LocalDate?>(null)

    private val _filters = MutableStateFlow(GoalFilterState())

    private val _addExistingGoalUiState = MutableStateFlow(AddExistingGoalUiState())
    val addExistingGoalUiState: StateFlow<AddExistingGoalUiState> = _addExistingGoalUiState.asStateFlow()

    init{
        viewModelScope.launch {
            calendarEventId = eventIdFromRoute ?: calendarEventsRepository.getOrCreateEventIdForDate(LocalDate.now())

            _calendarEventId.value = calendarEventId
            _date.value = calendarEventsRepository.getEventById(calendarEventId)?.date
        }
    }

    val filters: StateFlow<GoalFilterState> = combine(
        _filters,
        userPreferencesRepository.goalSortOption,
        userPreferencesRepository.goalSortAscending
    ){localFilters, sortOption, sortAscending ->
        localFilters.copy(
            sortOption = sortOption,
            sortAscending = sortAscending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = GoalFilterState()
    )

    val scheduledGoalsListUiState =
        _calendarEventId
            .filterNotNull()
            .flatMapLatest { eventId ->
                combine(
                    scheduledGoalsRepository.getScheduledGoals(eventId),
                    _date.filterNotNull(),
                    userPreferencesRepository.useCategoryColorsEnabled,
                    goalCategoriesRepository.getCategories()
                ){scheduledGoals, date, useCategoryColorsEnabled, goalCategories ->
                    val totalMinutes = scheduledGoals.sumOf {
                        val hours = it.scheduledHours
                        val minutes = it.scheduledMinutes

                        hours * 60 + minutes
                    }

                    ScheduledGoalsListUiState(
                        scheduledGoalsList = scheduledGoals,
                        categories = goalCategories,
                        calendarEventId = eventId,
                        date = date,
                        totalMinutes = totalMinutes,
                        remainingMinutesInDay = MINUTES_IN_24_HOUR_DAY - totalMinutes,
                        useCategoryColorsEnabled = useCategoryColorsEnabled
                    )
                }.combine(filters){uiState, filters ->
                    uiState.copy(
                        filteredScheduledGoals = uiState.scheduledGoalsList
                            .applyFilters(filters)
                            .applySorting(
                                sortOption = filters.sortOption,
                                ascending = filters.sortAscending,
                                categories = uiState.categories
                            )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ScheduledGoalsListUiState()
            )

    fun deleteScheduledGoal(scheduledGoal: ScheduledGoal){
        viewModelScope.launch {
            if(scheduledGoal.recurrenceRuleId != null){
                scheduledGoalsRepository.insertRecurrenceException(scheduledGoal.recurrenceRuleId, scheduledGoalsListUiState.value.date)
            }
            scheduledGoalsRepository.deleteScheduledGoal(scheduledGoal = scheduledGoal)
        }
    }

    suspend fun addScheduledGoalsFromExistingGoals(
        goalIds: Set<Int>,
        onNavigate: (Int) -> Unit = {}
    ){
        if(goalIds.isEmpty()) return

        val success = scheduledGoalsRepository.validInsertScheduledGoals(
            goalIds = goalIds,
            eventId = calendarEventId
        )

        _addExistingGoalUiState.update { state ->
            state.copy(
                showSelectionDurationError = !success,
                selectedGoalIds = if(success) emptySet() else state.selectedGoalIds
            )
        }

        if(success){
            onNavigate(calendarEventId)
        }
    }

    fun isPastDate(): Boolean{
        return _date.value?.isBefore(LocalDate.now()) ?: false
    }

    fun viewPreviousDay(onNavigate: (Int) -> Unit){
        viewModelScope.launch {
            val currentDate = scheduledGoalsListUiState.value.date ?: return@launch

            val previousDate = currentDate.minusDays(1)

            val previousEventId = calendarEventsRepository.getOrCreateEventIdForDate(previousDate)

            onNavigate(previousEventId)
        }
    }

    fun viewNextDay(onNavigate: (Int) -> Unit){
        viewModelScope.launch {
            val currentDate = scheduledGoalsListUiState.value.date ?: return@launch

            val nextDate = currentDate.plusDays(1)

            val previousEventId = calendarEventsRepository.getOrCreateEventIdForDate(nextDate)

            onNavigate(previousEventId)
        }
    }

    fun setComplete(scheduledGoal: ScheduledGoal, isCompleted: Boolean){
        viewModelScope.launch {
            val scheduledDurationMillis = (scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60000L

            val completedDuration = scheduledGoal.completedMillis >= scheduledDurationMillis

            //Naturally finished task that cannot be reactivated with checkbox
            if (!isCompleted && completedDuration){
                return@launch
            }

            val newStatus = if (isCompleted) {
                GoalStatus.COMPLETED
            } else {
                if (scheduledGoal.completedMillis > 0L) {
                    GoalStatus.PAUSED
                } else {
                    GoalStatus.NOT_STARTED
                }
            }

            scheduledGoalsRepository.updateScheduledGoal(scheduledGoal.copy(status = newStatus))
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

    fun toggleGoalSelection(
        goal: Goal,
        goals: List<Goal>,
        remainingMinutes: Int
    ){
        val state = _addExistingGoalUiState.value
        val goalId = goal.goalID

        //Deselect goal
        if (goalId in state.selectedGoalIds){
            _addExistingGoalUiState.value = state.copy(
                selectedGoalIds = state.selectedGoalIds - goalId,
                showSelectionDurationError = false
            )
            return
        }

        val selectedMinutes = goals
            .filter { it.goalID in state.selectedGoalIds }
            .sumOf { it.hours * 60 + it.minutes }

        val newTotalMinutes = selectedMinutes + goal.hours * 60 + goal.minutes

        if(newTotalMinutes > remainingMinutes){
            _addExistingGoalUiState.value = state.copy(
                showSelectionDurationError = true
            )
        } else {
            _addExistingGoalUiState.value = state.copy(
                selectedGoalIds = state.selectedGoalIds + goalId,
                showSelectionDurationError = false
            )
        }
    }
}

data class ScheduledGoalsListUiState(
    val scheduledGoalsList: List<ScheduledGoal> = emptyList(),
    val filteredScheduledGoals: List<ScheduledGoal> = emptyList(),
    val categories: List<GoalCategory> = emptyList(),
    val calendarEventId: Int? = null,
    val date: LocalDate = LocalDate.now(),
    val totalMinutes: Int = 0,
    val remainingMinutesInDay: Int = MINUTES_IN_24_HOUR_DAY - totalMinutes,
    val remainingTimeError: Boolean = false,
    val useCategoryColorsEnabled: Boolean = false
)


data class AddExistingGoalUiState(
    val selectedGoalIds: Set<Int> = emptySet(),
    val showSelectionDurationError: Boolean = false,
)