package com.example.timemanagementapp.util

import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.createGoal.GoalDetails

fun GoalDetails.validate(): Int? {
    val h = hours.toIntOrNull() ?: return R.string.invalid_hours
    val m = minutes.toIntOrNull() ?: return R.string.invalid_minutes

    if (title.isBlank()) return R.string.invalid_title
    if (h !in 0..23) return R.string.invalid_hours_0_23
    if (m !in 0..59) return R.string.invalid_minutes_0_59

    return null
}