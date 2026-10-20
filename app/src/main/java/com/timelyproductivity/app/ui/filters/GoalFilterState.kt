package com.timelyproductivity.app.ui.filters

import com.timelyproductivity.app.data.goal.priority.GoalPriority

data class GoalFilterState(
    val searchQuery: String = "",
    val categoryIds: Set<Int> = emptySet(),
    val priorities: Set<GoalPriority?> = emptySet(),
    val completionFilter: CompletionFilter = CompletionFilter.ALL
)

enum class CompletionFilter{
    ALL,
    COMPLETED,
    INCOMPLETE
}
