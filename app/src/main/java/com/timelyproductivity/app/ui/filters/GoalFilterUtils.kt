package com.timelyproductivity.app.ui.filters

import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal

fun List<ScheduledGoal>.applyFilters(
    filters: GoalFilterState
): List<ScheduledGoal> {
    return filter { goal ->
        val matchesName = goal.scheduledGoalTitle.contains(
            filters.searchQuery.trim(),
            ignoreCase = true
        )

        val matchesCategory =
            filters.categoryIds.isEmpty() ||
                    goal.scheduledCategoryId in filters.categoryIds

        val matchesPriority =
            filters.priorities.isEmpty() ||
                    goal.scheduledPriority in filters.priorities

        val matchesCompletion = when (filters.completionFilter) {
            CompletionFilter.ALL -> true
            CompletionFilter.COMPLETED ->
                goal.status == GoalStatus.COMPLETED
            CompletionFilter.INCOMPLETE ->
                goal.status != GoalStatus.COMPLETED
        }

        matchesName &&
                matchesCategory &&
                matchesPriority &&
                matchesCompletion
    }.sortedByDescending { goal ->
        goal.status == GoalStatus.COMPLETED
    }
}