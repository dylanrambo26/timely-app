package com.timelyproductivity.app.ui.filters

import com.timelyproductivity.app.data.goal.priority.GoalPriority

data class GoalFilterState(
    val searchQuery: String = "",
    val categoryIds: Set<Int> = emptySet(),
    val priorities: Set<GoalPriority?> = emptySet(),
    val completionFilter: CompletionFilter = CompletionFilter.ALL,
    val recurrenceFilter: RecurrenceFilter = RecurrenceFilter.ALL
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
