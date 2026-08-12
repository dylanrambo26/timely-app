package com.example.timemanagementapp.data

import com.example.timemanagementapp.data.calendar.CalendarEvent
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import java.time.LocalDate

val testCalendarEvents = listOf(
    CalendarEvent(
        eventId = 1,
        date = LocalDate.of(2026, 8, 10) // This week
    ),
    CalendarEvent(
        eventId = 2,
        date = LocalDate.of(2026, 8, 3)  // This month, not week
    ),
    CalendarEvent(
        eventId = 3,
        date = LocalDate.of(2026, 5, 10) // This year, not month
    ),
    CalendarEvent(
        eventId = 4,
        date = LocalDate.of(2025, 12, 10) // Previous year
    )
)

val testScheduledGoals = listOf(
    ScheduledGoal(
        scheduledGoalId = 1,
        eventId = 1,
        goalId = 3,
        status = GoalStatus.COMPLETED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test1"
    ),
    ScheduledGoal(
        scheduledGoalId = 2,
        eventId = 2,
        goalId = 1,
        status = GoalStatus.COMPLETED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test2"
    ),
    ScheduledGoal(
        scheduledGoalId = 3,
        eventId = 3,
        goalId = 2,
        status = GoalStatus.COMPLETED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test3"
    ),
    ScheduledGoal(
        scheduledGoalId = 4,
        eventId = 4,
        goalId = 5,
        status = GoalStatus.COMPLETED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test4"
    )
)

val testGoals = listOf(
    Goal(
        goalID = 3,
        hours = 1,
        minutes = 30,
        goalTitle = "Test1"
    ),
    Goal(
        goalID = 1,
        hours = 1,
        minutes = 30,
        goalTitle = "Test2"
    ),
    Goal(
        goalID = 2,
        hours = 1,
        minutes = 30,
        goalTitle = "Test3"
    ),
    Goal(
        goalID = 5,
        hours = 1,
        minutes = 30,
        goalTitle = "Test4"
    )
)

val testScheduledGoalsMixedStatus = listOf(
    ScheduledGoal(
        scheduledGoalId = 1,
        eventId = 1,
        goalId = 3,
        status = GoalStatus.PAUSED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test1"
    ),
    ScheduledGoal(
        scheduledGoalId = 2,
        eventId = 2,
        goalId = 1,
        status = GoalStatus.COMPLETED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test2"
    ),
    ScheduledGoal(
        scheduledGoalId = 3,
        eventId = 3,
        goalId = 2,
        status = GoalStatus.RUNNING,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test3"
    ),
    ScheduledGoal(
        scheduledGoalId = 4,
        eventId = 4,
        goalId = 5,
        status = GoalStatus.NOT_STARTED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test4"
    )
)

val testScheduledGoalsDifferentMillis = listOf(
    ScheduledGoal(
        scheduledGoalId = 1,
        eventId = 1,
        goalId = 3,
        status = GoalStatus.COMPLETED,
        completedMillis = 15 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test1"
    ),
    ScheduledGoal(
        scheduledGoalId = 2,
        eventId = 2,
        goalId = 1,
        status = GoalStatus.COMPLETED,
        completedMillis = 30 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test2"
    ),
    ScheduledGoal(
        scheduledGoalId = 3,
        eventId = 3,
        goalId = 2,
        status = GoalStatus.COMPLETED,
        completedMillis = 45 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test3"
    ),
    ScheduledGoal(
        scheduledGoalId = 4,
        eventId = 4,
        goalId = 5,
        status = GoalStatus.COMPLETED,
        completedMillis = 60 * 60_000L,
        scheduledHours = 1,
        scheduledMinutes = 30,
        scheduledGoalTitle = "Test4"
    )
)