package com.example.timemanagementapp.data

import java.time.LocalDate

interface CalendarEventsRepository {
    suspend fun insertCalendarEvent(event: CalendarEvent)

    suspend fun updateCalendarEvent(event: CalendarEvent)

    suspend fun deleteCalendarEvent(event: CalendarEvent)

    suspend fun getEventByDate(date: LocalDate): CalendarEvent?
}