package com.example.timemanagementapp.util

import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalWithGoal

//Filter goals by each status given in the set of GoalStatus
fun List<ScheduledGoalWithGoal>.filterByStatus(goalStatusFilters: Set<GoalStatus>): List<ScheduledGoalWithGoal> {
    return if (goalStatusFilters.isEmpty()) {
        this
    } else {
        filter {it.scheduledGoal.status in goalStatusFilters}
    }
}

fun List<ScheduledGoalWithGoal>.completedGoals() = filter{it.scheduledGoal.status == GoalStatus.COMPLETED}

fun List<ScheduledGoalWithGoal>.incompleteGoals() = filter{it.scheduledGoal.status != GoalStatus.COMPLETED}

fun List<ScheduledGoalWithGoal>.nonActiveGoals() = filter{it.scheduledGoal.status == GoalStatus.NOT_STARTED || it.scheduledGoal.status == GoalStatus.PAUSED}