package com.example.timemanagementapp.util

import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal

//Filter goals by each status given in the set of GoalStatus
fun List<ScheduledGoal>.filterByStatus(goalStatusFilters: Set<GoalStatus>): List<ScheduledGoal> {
    return if (goalStatusFilters.isEmpty()) {
        this
    } else {
        filter {it.status in goalStatusFilters}
    }
}

fun List<ScheduledGoal>.completedGoals() = filter{it.status == GoalStatus.COMPLETED}

fun List<ScheduledGoal>.incompleteGoals() = filter{it.status != GoalStatus.COMPLETED}

fun List<ScheduledGoal>.nonActiveGoals() = filter{it.status == GoalStatus.NOT_STARTED || it.status == GoalStatus.PAUSED}