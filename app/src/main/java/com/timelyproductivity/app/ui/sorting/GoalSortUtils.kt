package com.timelyproductivity.app.ui.sorting

import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.data.goal.recurrence.GoalWithRecurrence
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.ui.components.sorting.GoalSortOption
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.util.enumNameToFirstLetterCapitalized

@JvmName("applyScheduledGoalSorting")
fun List<ScheduledGoal>.applySorting(
    sortOption: GoalSortOption,
    ascending: Boolean,
    categories: List<GoalCategory>
): List<ScheduledGoal> {
    val categoryNames = categories.associate {
        it.categoryId to it.name
    }

    val comparator = when (sortOption) {
        GoalSortOption.DEFAULT ->
            compareByDescending<ScheduledGoal> {
                it.status == GoalStatus.COMPLETED
            }

        GoalSortOption.NAME ->
            compareBy { it.scheduledGoalTitle.lowercase() }

        GoalSortOption.PRIORITY -> {
            val priorityOrder = compareBy<ScheduledGoal> {
                it.scheduledPriority == null
            }

            val priorityValue = compareBy<ScheduledGoal> {
                when (it.scheduledPriority) {
                    GoalPriority.LOW -> 1
                    GoalPriority.MEDIUM -> 2
                    GoalPriority.HIGH -> 3
                    null -> 0
                }
            }

            val directedPriority =
                if (ascending) {
                    priorityValue
                } else {
                    priorityValue.reversed()
                }

            return sortedWith(priorityOrder.then(directedPriority))
        }

        GoalSortOption.CATEGORY -> {
            val categoryOrder = compareBy<ScheduledGoal> {
                categoryNames[it.scheduledCategoryId] == null
            }

            val nameOrder = compareBy<ScheduledGoal> {
                categoryNames[it.scheduledCategoryId]
                    ?.lowercase() ?: ""
            }

            val directedNameOrder =
                if (ascending) nameOrder else nameOrder.reversed()

            return sortedWith(
                categoryOrder.then(directedNameOrder)
            )
        }

        GoalSortOption.DURATION ->
            compareBy {
                it.scheduledHours * 60 + it.scheduledMinutes
            }
    }

    return if (sortOption == GoalSortOption.DEFAULT || ascending) {
        sortedWith(comparator)
    } else {
        sortedWith(comparator.reversed())
    }
}

fun GoalFilterState.sortDisplayLabel(): String {
    if (sortOption == GoalSortOption.DEFAULT) return ""

    val directionLabel = when (sortOption) {
        GoalSortOption.NAME, GoalSortOption.CATEGORY ->
            if (sortAscending) "A to Z" else "Z to A"

        GoalSortOption.PRIORITY ->
            if (sortAscending) "Low to High" else "High to Low"

        GoalSortOption.DURATION ->
            if (sortAscending) "Shortest to Longest"
            else "Longest to Shortest"

        else -> ""
    }

    return "Sorted by: ${sortOption.name.enumNameToFirstLetterCapitalized()} ($directionLabel)"
}

@JvmName("applyReusableGoalSorting")
fun List<GoalWithRecurrence>.applySorting(
    sortOption: GoalSortOption,
    ascending: Boolean,
    categories: List<GoalCategory>
): List<GoalWithRecurrence> {

    if (sortOption == GoalSortOption.DEFAULT) return this

    val categoryNames = categories.associate {
        it.categoryId to it.name
    }

    val comparator = when (sortOption) {
        GoalSortOption.NAME ->
            compareBy<GoalWithRecurrence> {
                it.goal.goalTitle.lowercase()
            }

        GoalSortOption.PRIORITY -> {
            val priorityOrder = compareBy<GoalWithRecurrence> {
                it.goal.priority == null
            }

            val priorityValue = compareBy<GoalWithRecurrence> {
                when (it.goal.priority) {
                    GoalPriority.LOW -> 1
                    GoalPriority.MEDIUM -> 2
                    GoalPriority.HIGH -> 3
                    null -> 0
                }
            }

            val directedPriority =
                if (ascending) priorityValue
                else priorityValue.reversed()

            return sortedWith(
                priorityOrder.then(directedPriority)
            )
        }

        GoalSortOption.CATEGORY -> {
            val categoryOrder = compareBy<GoalWithRecurrence> {
                categoryNames[it.goal.categoryId] == null
            }

            val nameOrder = compareBy<GoalWithRecurrence> {
                categoryNames[it.goal.categoryId]
                    ?.lowercase() ?: ""
            }

            val directedNameOrder =
                if (ascending) nameOrder
                else nameOrder.reversed()

            return sortedWith(
                categoryOrder.then(directedNameOrder)
            )
        }

        GoalSortOption.DURATION ->
            compareBy<GoalWithRecurrence> {
                it.goal.hours * 60 + it.goal.minutes
            }

        GoalSortOption.DEFAULT -> return this
    }

    return if (ascending) {
        sortedWith(comparator)
    } else {
        sortedWith(comparator.reversed())
    }
}