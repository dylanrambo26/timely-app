package com.example.timemanagementapp.data.scheduledgoal

import androidx.room.Embedded
import androidx.room.Relation
import com.example.timemanagementapp.data.goal.Goal

//May be used later for analytics
data class ScheduledGoalWithGoal(
    @Embedded
    val scheduledGoal: ScheduledGoal,

    @Relation(
        parentColumn = "goalId",
        entityColumn = "goalID"
    )
    val goal: Goal
)
