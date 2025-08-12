package com.example.timemanagementapp.data

object TestData {
    val goals = listOf(
        Goal(TimeDuration(3, 0), "Studying"),
        Goal(TimeDuration(1, 0), "Video Games"),
        Goal(TimeDuration(1, 0), "Workout"),
        Goal(TimeDuration(0, 30), "Reading"),
        Goal(TimeDuration(4, 0), "Phone Screen Time"),
    )

    val goalText = goals.joinToString (separator = "\n"){ goal ->
        val hours = goal.timeLimit.hours
        val minutes = goal.timeLimit.minutes
        val timeText = buildString {
            if (hours > 0) append("${hours}h")
            if (minutes > 0) append("${minutes}m")
        }.trim()

        "${goal.goalTitle} - $timeText"
    }
}

