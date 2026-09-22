package com.timelyproductivity.app.data.goal.recurrence

import androidx.room.Embedded
import androidx.room.Relation
import com.timelyproductivity.app.data.goal.Goal

data class GoalWithRecurrence(
    @Embedded
    val goal: Goal,

    @Relation(
        parentColumn = "goalID",
        entityColumn = "goalId"
    )

    val recurrenceRule: RecurrenceRule?

)
