package com.example.timemanagementapp.util

import java.time.LocalDate
import java.time.YearMonth

fun generateCalendarCellsPreview(
    month: YearMonth
): List<LocalDate?>{
    val cells = mutableListOf<LocalDate?>()

    //get the local date for the first day of the month
    val firstOfMonth = month.atDay(1)

    //Make sunday index 0, every thing else is the same, find day of week for the 1st of the month
    val startIndex = firstOfMonth.dayOfWeek.value % 7

    //load empty cells from previous month
    repeat(startIndex){
        cells.add(null)
    }

    //Days of current month
    for(day in 1..month.lengthOfMonth()) {
        cells.add(month.atDay(day))
    }

    while (cells.size < CALENDARGRIDSIZE){
        cells.add(null)
    }

    return cells
}