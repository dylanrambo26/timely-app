package com.timelyproductivity.app.data.scheduledgoal

import androidx.room.Embedded
import androidx.room.Relation
import com.timelyproductivity.app.data.goal.Goal

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
