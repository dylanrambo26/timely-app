package com.example.timemanagementapp.util

data class RemainingTimeState(
    val remainingMinutes: Int,
    val isDone: Boolean
)

fun calculateRemainingTime(
    startTimeMillis: Long,
    hours: Int,
    minutes: Int,
    currentTimeMillis: Long
): RemainingTimeState {
    val totalMillis = (hours * 60L + minutes) * 60_000L

    //Clamp endTime to 0 if less than 0
    val remainingMillis = if (startTimeMillis != 0L){
        val endTime = startTimeMillis + totalMillis
        (endTime - currentTimeMillis).coerceAtLeast(0)
    } else{
        totalMillis
    }

    return RemainingTimeState(
        remainingMinutes = (remainingMillis / 60_000).toInt(),
        isDone = remainingMillis == 0L
    )
}