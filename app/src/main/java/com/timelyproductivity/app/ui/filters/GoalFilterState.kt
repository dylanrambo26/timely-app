package com.timelyproductivity.app.ui.filters

import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.ui.components.sorting.GoalSortOption

data class GoalFilterState(
    val searchQuery: String = "",
    val categoryIds: Set<Int> = emptySet(),
    val priorities: Set<GoalPriority?> = emptySet(),
    val completionFilter: CompletionFilter = CompletionFilter.ALL,
    val recurrenceFilter: RecurrenceFilter = RecurrenceFilter.ALL,

    val sortOption: GoalSortOption = GoalSortOption.DEFAULT,
    val sortAscending: Boolean = true,
)

enum class CompletionFilter{
    ALL,
    COMPLETED,
    INCOMPLETE
}

enum class RecurrenceFilter{
    ALL,
    RECURRING,
    NON_RECURRING
}
