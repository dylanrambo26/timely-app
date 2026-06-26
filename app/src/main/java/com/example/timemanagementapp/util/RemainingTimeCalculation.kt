package com.example.timemanagementapp.util

import android.util.Log

data class RemainingTimeState(
    val remainingMinutes: Int,
    val isDone: Boolean
)

fun calculateRemainingTime(
    startTimeMillis: Long,
    hours: Int,
    minutes: Int,
    currentTimeMillis: Long,
    completedMillis: Long
): RemainingTimeState {
    val totalMillis = (hours * 60L + minutes) * 60_000L

    val activeSessionMillis = if(startTimeMillis != 0L){
        currentTimeMillis - startTimeMillis
    } else{
        0L
    }

    val elapsedMillis = completedMillis + activeSessionMillis

    //Clamp endTime to 0 if less than 0
    val remainingMillis = (totalMillis - elapsedMillis).coerceAtLeast(0L)

    Log.d(
        "Remaining Time",
        """
            totalMillis = $totalMillis
            completedMillis = $completedMillis
            startTimeMillis = $startTimeMillis
            remainingMillis = $remainingMillis
            remainingMinutes = ${remainingMillis / 60_000}
        """.trimIndent()
    )

    return RemainingTimeState(
        remainingMinutes = (remainingMillis / 60_000).toInt(),
        isDone = remainingMillis == 0L
    )
}