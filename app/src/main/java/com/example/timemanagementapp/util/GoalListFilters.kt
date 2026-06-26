package com.example.timemanagementapp.util

import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalStatus

//Filter goals by each status given in the set of GoalStatus
fun List<Goal>.filterByStatus(goalStatusFilters: Set<GoalStatus>): List<Goal> {
    return if (goalStatusFilters.isEmpty()) {
        this
    } else {
        filter {it.status in goalStatusFilters}
    }
}

fun List<Goal>.completedGoals() = filter{it.status == GoalStatus.COMPLETED}

fun List<Goal>.incompleteGoals() = filter{it.status != GoalStatus.COMPLETED}

fun List<Goal>.nonActiveGoals() = filter{it.status == GoalStatus.NOT_STARTED || it.status == GoalStatus.PAUSED}