package com.timelyproductivity.app.ui.editReusable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.data.goal.recurrence.CreateRecurrenceUseCase
import com.timelyproductivity.app.data.goal.recurrence.RecurrenceRule
import com.timelyproductivity.app.data.goal.recurrence.UpdateRecurrenceUseCase
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import com.timelyproductivity.app.ui.categories.CategoryEditorActions
import com.timelyproductivity.app.ui.categories.CategoryEditorState
import com.timelyproductivity.app.ui.createGoal.GoalDetails
import com.timelyproductivity.app.ui.createGoal.GoalUiState
import com.timelyproductivity.app.ui.createGoal.toGoal
import com.timelyproductivity.app.ui.goal.withAllRecurringDays
import com.timelyproductivity.app.ui.goal.withGoalRecurring
import com.timelyproductivity.app.ui.goal.withRecurrenceEndDate
import com.timelyproductivity.app.ui.goal.withRecurrenceEndDateEnabled
import com.timelyproductivity.app.ui.goal.withRecurrenceStartDate
import com.timelyproductivity.app.ui.goal.withRecurringDay
import com.timelyproductivity.app.util.validate
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class EditReusableGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val createRecurrenceUseCase: CreateRecurrenceUseCase,
    private val updateRecurrenceUseCase: UpdateRecurrenceUseCase,
    private val goalCategoriesRepository: GoalCategoriesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(), CategoryEditorActions {

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val categoryEditor = CategoryEditorState()

    val categoryEditorUiState = categoryEditor.uiState

    private val goalId: Int = checkNotNull(savedStateHandle[EditReusableGoalDestination.goalIdArg])

    private var originalRecurrenceRule: RecurrenceRule? = null

    init {
        viewModelScope.launch {
            goalCategoriesRepository.getCategories()
                .collect { categories ->
                    goalUiState = goalUiState.copy(
                        goalCategories = categories
                    )
                }
        }

        viewModelScope.launch {
            userPreferencesRepository.useCategoryColorsEnabled
                .collect { enabled ->
                    goalUiState = goalUiState.copy(
                        useCategoryColorsEnabled = enabled
                    )
                }
        }

        viewModelScope.launch {
            val goalWithRecurrence = goalsRepository
                .getGoalWithRecurrenceStream(goalId)
                .filterNotNull()
                .first()

            val goal = goalWithRecurrence.goal
            val recurrenceRule = goalWithRecurrence.recurrenceRule
            originalRecurrenceRule = recurrenceRule
            val isRecurring = recurrenceRule != null

            val details = GoalDetails(
                id = goal.goalID,
                title = goal.goalTitle,
                hours = goal.hours.toString(),
                minutes = goal.minutes.toString()
            )

            val originalDays = goalWithRecurrence.recurrenceRule?.recurringDays.orEmpty()

            goalUiState = goalUiState.copy(
                goalDetails = details,
                isEntryValid = details.validate() == null,
                isGoalRecurring = isRecurring,
                wasOriginallyRecurring = isRecurring,
                recurringDays = originalDays,
                originalRecurringDays = originalDays,
                recurrenceEndDate = recurrenceRule?.endDate,
                hasRecurrenceEndDate = recurrenceRule?.endDate != null,
                selectedCategoryId = goal.categoryId,
                originalCategoryId = goal.categoryId,
            )
        }
    }

    fun updateIsGoalRecurring(isRecurring: Boolean){
        goalUiState = goalUiState.withGoalRecurring(isRecurring)
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

    fun updateHasRecurrenceEndDate(hasRecurrenceEndDate: Boolean){
        goalUiState = goalUiState.withRecurrenceEndDateEnabled(hasRecurrenceEndDate)
    }

    fun updateRecurrenceStartDate(recurrenceStartDate: LocalDate){
        goalUiState = goalUiState.withRecurrenceStartDate(recurrenceStartDate)
    }

    fun updateRecurrenceEndDate(recurrenceEndDate: LocalDate?){
        goalUiState = goalUiState.withRecurrenceEndDate(recurrenceEndDate)
    }

    fun updateUiState(goalDetails: GoalDetails) {
        val error = goalDetails.validate()
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    suspend fun updateReusableGoal(
        onNavigate: () -> Unit = {},
        updateFutureScheduledGoals: Boolean
    ) {
        val error = goalUiState.goalDetails.validate()
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val goal = goalUiState.goalDetails.toGoal(
            categoryId = goalUiState.selectedCategoryId
        )
        goalsRepository.updateGoal(goal)

        //Updates goal values to the new ones, does not depend on recurring status
        if(updateFutureScheduledGoals){
            scheduledGoalsRepository.updateFutureScheduledGoalsFromEditedTemplate(
                goalId = goal.goalID,
                title = goal.goalTitle,
                hours = goal.hours,
                minutes = goal.minutes,
                startDate = LocalDate.now(),
                categoryId = goal.categoryId
            )
        }

        //Recurring to Recurring case
        if(goalUiState.wasOriginallyRecurring){

            val existingRule = originalRecurrenceRule ?: return

            updateRecurrenceUseCase(
                originalRule = existingRule,
                recurringDays = goalUiState.recurringDays,
                startDate = goalUiState.recurrenceStartDate,
                endDate = goalUiState.recurrenceEndDate,
                deleteOldScheduledGoals = updateFutureScheduledGoals
            )
        }

        if (!goalUiState.wasOriginallyRecurring && goalUiState.isGoalRecurring){
            createRecurrenceUseCase(
                recurringDays = goalUiState.recurringDays,
                goal = goal,
                startDate = goalUiState.recurrenceStartDate,
                endDate = goalUiState.recurrenceEndDate
            )
        }
        onNavigate()
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
            name = name,
            color = editorState.selectedColor
        )

        viewModelScope.launch {
            val categoryId = goalCategoriesRepository.insertCategory(category)

            selectCategory(categoryId.toInt())

            categoryEditor.closeCategoryDialog()
        }
    }

    fun selectPriority(priority: GoalPriority?){
        goalUiState = goalUiState.copy(
            priority = priority
        )
    }
}