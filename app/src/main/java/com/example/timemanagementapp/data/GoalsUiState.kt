package com.example.timemanagementapp.data

/**
 * List of Goals maintained as a state
 */
data class GoalsUiState(
    val goals: List<Goal> = listOf(),
    val totalMinutesTaken: Int = 0,
    val remainingMinutesInDay: Int = 24 * 60
)
