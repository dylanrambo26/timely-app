package com.timelyproductivity.app.ui.components.lists.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testScheduledGoalsWithCategoriesSizeFour
import com.timelyproductivity.app.ui.components.lists.ScheduledGoalList
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

@Composable
private fun ScheduledGoalListConfiguredPreview(
    config: ScheduledGoalListPreviewConfig,
    goals: List<ScheduledGoal> = testScheduledGoalsWithCategoriesSizeFour
){
    TimeManagementAppTheme {
        ScheduledGoalList(
            goals = goals,
            categories = testGoalCategoriesSizeThreeWithColor,
            addCompletionColor = config.addCompletionColor,
            addCategoryColors = config.addCategoryColors,
            addCheckboxes = config.addCheckboxes,
            selectedGoalId = config.selectedGoalId,
            onCompleteChange = if (config.addCheckboxes) {
                { _, _ -> }
            } else {
                null
            },
            onEditGoal = if (config.showEditButtons) {
                {}
            } else {
                null
            },
            onDeleteGoal = if (config.showDeleteButtons) {
                {}
            } else {
                null
            },
            showCountdownReminders = config.showReminders,
            countdownReminders = config.countdownReminders,

        )
    }
}

@Preview(
    name = "Current Task Selection",
    showBackground = true,
    group = "Scheduled Goal List"
)
@Composable
private fun CurrentTaskListPreview() {
    ScheduledGoalListConfiguredPreview(
        config = ScheduledGoalListPreviewConfigs.CurrentTaskSelection
    )
}

@Preview(
    name = "View Goals",
    showBackground = true,
    group = "Scheduled Goal List"
)
@Composable
private fun ViewGoalsListPreview() {
    ScheduledGoalListConfiguredPreview(
        config = ScheduledGoalListPreviewConfigs.ViewGoals
    )
}