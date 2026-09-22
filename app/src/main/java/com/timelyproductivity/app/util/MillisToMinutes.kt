package com.timelyproductivity.app.util

fun millisToMinutes(millis: Long): Int
{
    return (millis / 60_000L).toInt()
}