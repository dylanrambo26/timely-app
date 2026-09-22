package com.timelyproductivity.app.ui.add

import androidx.lifecycle.ViewModel
import com.timelyproductivity.app.data.calendar.CalendarEventsRepository
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class AddScheduledGoalViewModel(
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
): ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())


}