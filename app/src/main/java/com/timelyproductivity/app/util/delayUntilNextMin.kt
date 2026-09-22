package com.timelyproductivity.app.util

import java.time.LocalTime

fun millisUntilNextMinute(): Long {
    val now = LocalTime.now()
    return (60 - now.second) * 1000L
}