package com.timelyproductivity.app.ui.components.lists.preview

data class ScheduledGoalListPreviewConfig(
    val addCompletionColor: Boolean = false,
    val addCategoryColors: Boolean = false,
    val addCheckboxes: Boolean = false,
    val showReminders: Boolean = false,
    val showEditButtons: Boolean = false,
    val showDeleteButtons: Boolean = false,
    val selectedGoalId: Int? = null,
    val countdownReminders: Set<Int> = emptySet()
)

object ScheduledGoalListPreviewConfigs{
    val ViewGoals = ScheduledGoalListPreviewConfig(
        addCompletionColor = true,
        addCategoryColors = true,
        addCheckboxes = true,
    )

    val EditScheduledGoals = ScheduledGoalListPreviewConfig(
        addCategoryColors = true,
        showEditButtons = true,
        showDeleteButtons = true
    )

    val CurrentTaskSelection = ScheduledGoalListPreviewConfig(
        addCompletionColor = true,
        addCategoryColors = true,
        selectedGoalId = 1,
        showReminders = true,
        countdownReminders = setOf(1,5,10)
    )
    
    val Default = ScheduledGoalListPreviewConfig()
}