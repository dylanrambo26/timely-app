package com.example.timemanagementapp.util

import java.time.LocalTime

fun getTimeRemainingInDay(): Int {
    val now = LocalTime.now()
    return (23 - now.hour) * 60 +
            (59 - now.minute)
}