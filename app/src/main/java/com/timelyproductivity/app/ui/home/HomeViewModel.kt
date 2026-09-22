package com.timelyproductivity.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timelyproductivity.app.data.calendar.CalendarEventsRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val calendarEventsRepository: CalendarEventsRepository,
) : ViewModel(){
    fun viewGoalsForToday(onNavigate: (Int) -> Unit){
        viewModelScope.launch {
            val eventId = calendarEventsRepository.getOrCreateEventIdForDate(LocalDate.now())

            onNavigate(eventId)
        }
    }
}