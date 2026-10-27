package com.timelyproductivity.app.ui.filters

import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.recurrence.GoalWithRecurrence
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal

@JvmName("applyScheduledGoalFilters")
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

@JvmName("applyReusableGoalFilters")
fun List<GoalWithRecurrence>.applyFilters(
    filters: GoalFilterState
): List<GoalWithRecurrence> {
    return filter { goalWithRecurrence ->
        val matchesName = goalWithRecurrence.goal.goalTitle.contains(
            filters.searchQuery.trim(),
            ignoreCase = true
        )

        val matchesCategory =
            filters.categoryIds.isEmpty() ||
                    goalWithRecurrence.goal.categoryId in filters.categoryIds

        val matchesPriority =
            filters.priorities.isEmpty() ||
                    goalWithRecurrence.goal.priority in filters.priorities

        val matchesRecurrence = when(filters.recurrenceFilter){
            RecurrenceFilter.ALL -> true

            RecurrenceFilter.RECURRING -> goalWithRecurrence.recurrenceRule != null

            RecurrenceFilter.NON_RECURRING -> goalWithRecurrence.recurrenceRule == null
        }

        matchesName &&
                matchesCategory &&
                matchesPriority&&
                matchesRecurrence
    }
}

fun getFilterCount(
    filters: GoalFilterState
): Int {
    return filters.categoryIds.size + filters.priorities.size +
                (if (filters.completionFilter != CompletionFilter.ALL) 1 else 0) +
                (if (filters.recurrenceFilter != RecurrenceFilter.ALL) 1 else 0) +
                (if (filters.searchQuery.isNotBlank()) 1 else 0)
}