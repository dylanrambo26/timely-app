package com.example.timemanagementapp.data

val testGoalsSizeThree: List<Goal>
    get() = listOf(
        Goal(
            goalID = 1,
            hours = 1,
            minutes = 30,
            goalTitle = "Study",
            status = GoalStatus.NOT_STARTED
        ),
        Goal(
            goalID = 2,
            hours = 1,
            minutes = 30,
            goalTitle = "work",
            status = GoalStatus.COMPLETED
        ),
        Goal(
            goalID = 3,
            hours = 1,
            minutes = 30,
            goalTitle = "test",
            status = GoalStatus.NOT_STARTED
        )
    )