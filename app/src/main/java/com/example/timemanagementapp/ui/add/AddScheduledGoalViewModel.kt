package com.example.timemanagementapp.ui.add

import androidx.lifecycle.ViewModel
import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class AddScheduledGoalViewModel(
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
): ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())


}