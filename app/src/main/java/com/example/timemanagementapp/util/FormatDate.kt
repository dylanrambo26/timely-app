package com.example.timemanagementapp.util

import java.time.LocalDate

fun formatLocalDateToString(date: LocalDate?): String {
    if(date == LocalDate.now()){
        return "Today's"
    }

    return date?.monthValue.toString() +
            "/" + date?.dayOfMonth.toString()
}