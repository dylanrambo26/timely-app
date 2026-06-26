package com.example.timemanagementapp.ui.calendar

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.timemanagementapp.util.CALENDARGRIDSIZE

class CalendarViewModel : ViewModel() {
    var displayedMonth by mutableStateOf(YearMonth.now())
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    val calendarCells: List<LocalDate?>
        get() = generateCalendarCells(displayedMonth)

    fun nextMonth(){
        displayedMonth = displayedMonth.plusMonths(1)
    }

    fun previousMonth(){
        displayedMonth = displayedMonth.minusMonths(1)
    }

    fun selectDate(date: LocalDate){
        selectedDate = date
    }

    fun generateCalendarCells(
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
}