package com.timelyproductivity.app.ui.editScheduled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.UserPreferencesRepository
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategoriesRepository
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import com.timelyproductivity.app.ui.categories.CategoryEditorActions
import com.timelyproductivity.app.ui.categories.CategoryEditorState
import com.timelyproductivity.app.ui.createGoal.GoalDetails
import com.timelyproductivity.app.ui.createGoal.GoalUiState
import com.timelyproductivity.app.ui.createGoal.toGoal
import com.timelyproductivity.app.util.validate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditScheduledGoalViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val goalCategoriesRepository: GoalCategoriesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(), CategoryEditorActions{

    var goalUiState by mutableStateOf(GoalUiState())
        private set

    private val categoryEditor = CategoryEditorState()

    val categoryEditorUiState = categoryEditor.uiState

    private val scheduledGoalId: Int = checkNotNull(savedStateHandle[EditScheduledGoalDestination.scheduledGoalIdArg])

    private var currentScheduledGoal: ScheduledGoal? = null

    private val _calendarEventId = MutableStateFlow<Int?>(null)
    val calendarEventId: StateFlow<Int?> = _calendarEventId.asStateFlow()

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
            scheduledGoalsRepository
                .getScheduledGoal(scheduledGoalId)
                .collect { scheduledGoal ->
                    currentScheduledGoal = scheduledGoal
                    _calendarEventId.value = scheduledGoal.eventId

                    val isCurrentOrPaused =
                        (scheduledGoal.status == GoalStatus.RUNNING) || (scheduledGoal.status == GoalStatus.PAUSED)

                    val details = GoalDetails(
                        id = scheduledGoal.scheduledGoalId,
                        title = scheduledGoal.scheduledGoalTitle,
                        hours = scheduledGoal.scheduledHours.toString(),
                        minutes = scheduledGoal.scheduledMinutes.toString()
                    )

                    goalUiState = goalUiState.copy(
                        goalDetails = details,
                        selectedCategoryId = scheduledGoal.scheduledCategoryId,
                        originalCategoryId = scheduledGoal.scheduledCategoryId,
                        isEntryValid = details.validate() == null,
                        isDurationEditable = !isCurrentOrPaused
                    )
                }
        }
    }
    fun updateUiState(goalDetails: GoalDetails) {
        val error = goalDetails.validate()
        goalUiState =
            goalUiState.copy(goalDetails = goalDetails, isEntryValid = error == null, errorMessage = error)
    }

    suspend fun updateScheduledGoal(onNavigate: (Int) -> Unit = {}) {
        val error = goalUiState.goalDetails.validate()
        if(error != null){
            goalUiState = goalUiState.copy(
                errorMessage = error,
                isEntryValid = false
            )
            return
        }

        val existingScheduledGoal = currentScheduledGoal ?: return
        val isDurationLocked = existingScheduledGoal.status == GoalStatus.RUNNING || existingScheduledGoal.status == GoalStatus.PAUSED
        if(!isDurationLocked){
            val goal = goalUiState.goalDetails.toGoal()

            val goalTotalMinutes = goal.hours * 60 + goal.minutes

            val eventId = existingScheduledGoal.eventId
            val isValidDuration = scheduledGoalsRepository.isValidDurationForDate(
                goalTotalMinutes = goalTotalMinutes,
                eventId = eventId,
                excludedScheduledGoalId = existingScheduledGoal.scheduledGoalId
            )

            if(!isValidDuration){
                goalUiState = goalUiState.copy(
                    errorMessage = R.string.selected_goal_exceeds_remaining_time,
                    isEntryValid = false
                )
                return
            }
        }

        val updatedHours = if(isDurationLocked){
            existingScheduledGoal.scheduledHours
        } else {
            goalUiState.goalDetails.hours.toInt()
        }

        val updatedMinutes = if(isDurationLocked){
            existingScheduledGoal.scheduledMinutes
        } else {
            goalUiState.goalDetails.minutes.toInt()
        }

        scheduledGoalsRepository.updateScheduledGoal(
            existingScheduledGoal.copy(
                scheduledGoalTitle = goalUiState.goalDetails.title,
                scheduledHours = updatedHours,
                scheduledMinutes = updatedMinutes,
                scheduledCategoryId = goalUiState.selectedCategoryId,
                isCustomized = true
            )
        )

        onNavigate(existingScheduledGoal.eventId)
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
}

