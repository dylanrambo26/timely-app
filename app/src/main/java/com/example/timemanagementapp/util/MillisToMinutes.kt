package com.example.timemanagementapp.util

fun millisToMinutes(millis: Long): Int
{
    return (millis / 60_000L).toInt()
}