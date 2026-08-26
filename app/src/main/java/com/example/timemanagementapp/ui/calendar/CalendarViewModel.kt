package com.example.timemanagementapp.ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import com.example.timemanagementapp.util.CALENDARGRIDSIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(
    val calendarEventsRepository: CalendarEventsRepository,
    val scheduledGoalsRepository: ScheduledGoalsRepository
) : ViewModel() {

    private val _displayedMonth = MutableStateFlow(YearMonth.now())
    val displayedMonth = _displayedMonth.asStateFlow()

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    val calendarCells: List<LocalDate?>
        get() = generateCalendarCells(displayedMonth.value)

    @OptIn(ExperimentalCoroutinesApi::class)
    val datesWithGoals: StateFlow<Set<LocalDate>> =
        displayedMonth
            .flatMapLatest { month ->
                scheduledGoalsRepository.getDatesWithScheduledGoals(
                    month.atDay(1),
                    month.atEndOfMonth()
                )
            }
            .map { it.toSet() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptySet()
            )

    fun nextMonth(){
        _displayedMonth.update { it.plusMonths(1) }
    }

    fun previousMonth(){
        _displayedMonth.update { it.minusMonths(1) }
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

    fun viewGoalsForSelectedDate(onNavigate: (Int) -> Unit){
        viewModelScope.launch {

            val eventId = calendarEventsRepository.getOrCreateEventIdForDate(selectedDate)
            onNavigate(eventId)
        }
    }
}