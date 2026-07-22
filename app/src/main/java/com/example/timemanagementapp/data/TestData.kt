package com.example.timemanagementapp.data

import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal

val goal1 = Goal(
    goalID = 1,
    goalTitle = "Study",
    hours = 1,
    minutes = 30,
)

val goal2 = Goal(
    goalID = 2,
    goalTitle = "work",
    hours = 1,
    minutes = 30,
)

val goal3 = Goal(
    goalID = 3,
    goalTitle = "test",
    hours = 1,
    minutes = 30,
)

val testScheduledGoalsSizeThree: List<ScheduledGoal>
    get() = listOf(
            ScheduledGoal(
                scheduledGoalId = 0,
                eventId = 3,
                goalId = 1,
                status = GoalStatus.NOT_STARTED,
                startTimeMillis = 123456L,
                completedMillis = 60000L,
                scheduledGoalTitle = goal1.goalTitle,
                scheduledHours = goal1.hours,
                scheduledMinutes = goal1.minutes,
            ),
            ScheduledGoal(
                scheduledGoalId = 1,
                eventId = 3,
                goalId = 2,
                status = GoalStatus.COMPLETED,
                startTimeMillis = 146000L,
                completedMillis = 80000L,
                scheduledGoalTitle = goal2.goalTitle,
                scheduledHours = goal2.hours,
                scheduledMinutes = goal2.minutes,
            ),
        ScheduledGoal(
                scheduledGoalId = 2,
                eventId = 3,
                goalId = 3,
                status = GoalStatus.NOT_STARTED,
                startTimeMillis = 156456L,
                completedMillis = 60000L,
                scheduledGoalTitle = goal3.goalTitle,
                scheduledHours = goal3.hours,
                scheduledMinutes = goal3.minutes,
        ),
    )

val testGoalsSizeThree: List<Goal>
    get() = listOf(
        goal1,
        goal2,
        goal3
    )
