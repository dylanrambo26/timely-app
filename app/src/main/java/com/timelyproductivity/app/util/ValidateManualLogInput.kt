package com.timelyproductivity.app.util

import com.timelyproductivity.app.R

fun validateManualLogInput(
    hours: String,
    minutes: String,
    scheduledGoalDuration: Int
): Int? {
    val h = hours.toIntOrNull() ?: return R.string.invalid_hours
    val m = minutes.toIntOrNull() ?: return R.string.invalid_minutes

    if (h !in 0..23) return R.string.invalid_hours_0_23
    if (m !in 0..59) return R.string.invalid_minutes_0_59

    val totalMinutes = h * 60L + m

    if (totalMinutes > scheduledGoalDuration) {
        return R.string.log_duration_must_be_less_than_scheduled_duration
    }

    return null
}