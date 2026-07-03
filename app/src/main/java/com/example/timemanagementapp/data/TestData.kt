package com.example.timemanagementapp.data

import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalWithGoal

val testScheduledGoalsSizeThree: List<ScheduledGoalWithGoal>
    get() = listOf(
        ScheduledGoalWithGoal(
            scheduledGoal = ScheduledGoal(
                scheduledGoalId = 0,
                eventId = 3,
                goalId = 1,
                status = GoalStatus.NOT_STARTED,
                startTimeMillis = 123456L,
                completedMillis = 60000L
            ),
            goal = Goal(
                goalID = 1,
                goalTitle = "Study",
                hours = 1,
                minutes = 30,
            )
        ),
        ScheduledGoalWithGoal(
            scheduledGoal = ScheduledGoal(
                scheduledGoalId = 1,
                eventId = 3,
                goalId = 2,
                status = GoalStatus.COMPLETED,
                startTimeMillis = 146000L,
                completedMillis = 80000L
            ),
            goal = Goal(
                goalID = 2,
                goalTitle = "work",
                hours = 1,
                minutes = 30,
            )
        ),
        ScheduledGoalWithGoal(
            scheduledGoal = ScheduledGoal(
                scheduledGoalId = 2,
                eventId = 3,
                goalId = 3,
                status = GoalStatus.NOT_STARTED,
                startTimeMillis = 156456L,
                completedMillis = 60000L
            ),
            goal = Goal(
                goalID = 3,
                goalTitle = "test",
                hours = 1,
                minutes = 30,
            )
        ),
    )

val testGoalsSizeThree: List<Goal>
    get() = listOf(
        Goal(
            goalID = 1,
            goalTitle = "Study",
            hours = 1,
            minutes = 30,
        ),
        Goal(
            goalID = 2,
            goalTitle = "work",
            hours = 1,
            minutes = 30,
        ),
        Goal(
            goalID = 3,
            goalTitle = "test",
            hours = 1,
            minutes = 30,
        ),
    )

/*
Goal(
goalID = 1,
hours = 1,
minutes = 30,
goalTitle = "Study",
),
Goal(
goalID = 2,
hours = 1,
minutes = 30,
goalTitle = "work",
),
Goal(
goalID = 3,
hours = 1,
minutes = 30,
goalTitle = "test",
)*/
