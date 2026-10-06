package com.timelyproductivity.app.ui.createGoal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.goal.recurrence.CreateRecurrenceUseCase
import com.timelyproductivity.app.data.calendar.CalendarEventsRepository
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import com.timelyproductivity.app.ui.categories.CategoryEditorActions
import com.timelyproductivity.app.ui.categories.CategoryEditorState
import com.timelyproductivity.app.ui.goal.validateRecurrence
import com.timelyproductivity.app.ui.goal.withAllRecurringDays
import com.timelyproductivity.app.ui.goal.withGoalRecurring
import com.timelyproductivity.app.ui.goal.withRecurrenceEndDate
import com.timelyproductivity.app.ui.goal.withRecurrenceEndDateEnabled
import com.timelyproductivity.app.ui.goal.withRecurrenceStartDate
import com.timelyproductivity.app.ui.goal.withRecurringDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class CreateGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository,
    private val createRecurrenceUseCase: CreateRecurrenceUseCase,
    private val goalCategoriesRepository: GoalCategoriesRepository
) : ViewModel(), CategoryEditorActions{

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val categoryEditor = CategoryEditorState()

    val categoryEditorUiState = categoryEditor.uiState

    val calendarEventId =
        savedStateHandle.get<Int>(CreateGoalDestination.eventIdArg)
            ?.takeIf { it != -1 }

    val copyFromGoalId = savedStateHandle.get<Int>(CreateGoalDestination.copyFromGoalIdArg)
        ?.takeIf { it != -1 }

    private val _date = MutableStateFlow<LocalDate?>(null)
    val date: StateFlow<LocalDate?> = _date.asStateFlow()

    val canAddGoalToDate: Boolean
        get() = calendarEventId != null

    init{
        viewModelScope.launch {
            goalCategoriesRepository.getCategories()
                .collect {categories ->
                    goalUiState = goalUiState.copy(
                        goalCategories = categories
                    )
                }
        }

        calendarEventId?.let {
            viewModelScope.launch {
                _date.value = calendarEventsRepository.getEventById(calendarEventId)?.date
            }
        }

        copyFromGoalId?.let {goalId ->
            viewModelScope.launch {
                val sourceGoal = goalsRepository.getGoalOnce(goalId)

                goalUiState = goalUiState.copy(
                    goalDetails = sourceGoal.toGoalDetails().copy(id = 0),
                    isGoalRecurring = false,
                    recurringDays = emptySet(),
                    hasRecurrenceEndDate = false,
                    recurrenceEndDate = null
                )
            }

        }
    }

    fun updateIsGoalRecurring(isRecurring: Boolean){
        goalUiState = goalUiState.withGoalRecurring(isRecurring)
    }

    fun updateHasRecurrenceEndDate(hasRecurrenceEndDate: Boolean){
        goalUiState = goalUiState.withRecurrenceEndDateEnabled(hasRecurrenceEndDate)
    }

    fun updateRecurrenceStartDate(recurrenceStartDate: LocalDate){
        goalUiState = goalUiState.withRecurrenceStartDate(recurrenceStartDate)
    }

    fun updateRecurrenceEndDate(recurrenceEndDate: LocalDate?){
        goalUiState = goalUiState.withRecurrenceEndDate(recurrenceEndDate)
    }

    fun updateAllRecurringDays(isChecked: Boolean){
        goalUiState = goalUiState.withAllRecurringDays(isChecked)
    }

    fun onRecurringDayChange(
        day: DayOfWeek,
        isChecked: Boolean
    ){
        goalUiState = goalUiState.withRecurringDay(day, isChecked)
    }

    fun updateUiState(goalDetails: GoalDetails){
        val error = validateInput(goalDetails)
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    fun clearUiState(){
        goalUiState =
            goalUiState.copy(goalDetails = GoalDetails(), isEntryValid = false, errorMessage = null)
    }

    private fun validateInput(uiState: GoalDetails): Int? {
        val h = uiState.hours.toIntOrNull() ?: return R.string.invalid_hours
        val m = uiState.minutes.toIntOrNull() ?: return R.string.invalid_minutes

        if (uiState.title.isBlank()) return R.string.invalid_title
        if (h !in 0..23) return R.string.invalid_hours_0_23
        if (m !in 0..59) return R.string.invalid_minutes_0_59

        return null
    }

    suspend fun saveGoal(
        onNavigateToViewGoals: (Int) -> Unit = {},
        onNavigateToManageReusableGoals: () -> Unit = {},
    ){

        val error = validateInput(goalUiState.goalDetails) ?: goalUiState.validateRecurrence()
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                //isEntryValid = false
            )
            return
        }

        val goal = goalUiState.goalDetails.toGoal()
        val goalId = goalsRepository.insertGoal(goal)

        val insertedGoal = goal.copy(
            goalID = goalId
        )

        if(goalUiState.isGoalRecurring){
            createRecurrenceUseCase(
                recurringDays = goalUiState.recurringDays,
                goal = insertedGoal,
                startDate = goalUiState.recurrenceStartDate,
                endDate = goalUiState.recurrenceEndDate
            )
        }

        if (calendarEventId != null){
            onNavigateToViewGoals(calendarEventId)
        } else {
            onNavigateToManageReusableGoals()
        }
    }

    suspend fun saveGoalAndAddToDate(onNavigate: (Int) -> Unit = {}){
        val eventId = calendarEventId ?: return

        val error = validateInput(goalUiState.goalDetails)
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val goal = goalUiState.goalDetails.toGoal()
        val goalTotalMinutes = goal.hours * 60 + goal.minutes

        val isValidDuration = scheduledGoalsRepository.isValidDurationForDate(
            goalTotalMinutes = goalTotalMinutes,
            eventId = eventId,
            excludedScheduledGoalId = null
        )

        if(!isValidDuration){
            goalUiState = goalUiState.copy(
                errorMessage = R.string.selected_goal_exceeds_remaining_time,
                isEntryValid = false
            )
            return
        }

        val goalId = goalsRepository.insertGoal(goal)
        scheduledGoalsRepository.insertScheduledGoal(
            ScheduledGoal(
                goalId = goalId,
                eventId = eventId,
                scheduledGoalTitle = goal.goalTitle,
                scheduledHours = goal.hours,
                scheduledMinutes = goal.minutes,
                recurrenceRuleId = null
            )
        )
        onNavigate(eventId)
    }

    fun selectCategory(categoryId: Int?){
        goalUiState = goalUiState.copy(
            selectedCategoryId = categoryId
        )
    }

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
            existingCategories = goalUiState.goalCategories
        ) ?: return

        val category = GoalCategory(
            categoryId = editorState.originalCategoryId ?: 0,
            name = name,
            color = editorState.selectedColor
        )

        viewModelScope.launch {
            if(editorState.originalCategoryId == null){
                val categoryId = goalCategoriesRepository.insertCategory(category)

                selectCategory(categoryId.toInt())
            } else {
                goalCategoriesRepository.updateCategory(category)
            }

            categoryEditor.closeCategoryDialog()
        }
    }
}


data class GoalUiState(
    val goalDetails: GoalDetails = GoalDetails(),
    val isEntryValid: Boolean = false,
    val errorMessage: Int? = null,
    val isDurationEditable: Boolean = true,
    val isGoalRecurring: Boolean = false,
    val wasOriginallyRecurring: Boolean = false,
    val recurringDays: Set<DayOfWeek> = emptySet(),
    val originalRecurringDays: Set<DayOfWeek> = emptySet(),

    val recurrenceStartDate: LocalDate = LocalDate.now(),
    val recurrenceEndDate: LocalDate? = null,
    val hasRecurrenceEndDate: Boolean = false,

    val goalCategories: List<GoalCategory> = emptyList(),
    val selectedCategoryId: Int? = null
)

data class GoalDetails(
    val id: Int = 0,
    val title: String = "",
    val hours: String = "",
    val minutes: String = ""
)

fun GoalDetails.toGoal(): Goal = Goal(
    goalID = id,
    goalTitle = title,
    hours = hours.toIntOrNull() ?: 0,
    minutes = minutes.toIntOrNull() ?: 0,
)

fun Goal.toGoalUiState(isEntryValid: Boolean = false): GoalUiState = GoalUiState(
    goalDetails = this.toGoalDetails(),
    isEntryValid = isEntryValid
)

fun Goal.toGoalDetails(): GoalDetails = GoalDetails(
    id = goalID,
    title = goalTitle,
    hours = hours.toString(),
    minutes = minutes.toString()
)