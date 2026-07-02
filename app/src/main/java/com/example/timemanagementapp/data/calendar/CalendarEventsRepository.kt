package com.example.timemanagementapp.data.calendar

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarEventsRepository {
    suspend fun insertCalendarEvent(event: CalendarEvent)

    suspend fun updateCalendarEvent(event: CalendarEvent)

    suspend fun deleteCalendarEvent(event: CalendarEvent)

    suspend fun getEventByDate(date: LocalDate): CalendarEvent?

    fun getEventByDateFlow(date: LocalDate): Flow<CalendarEvent?>
}