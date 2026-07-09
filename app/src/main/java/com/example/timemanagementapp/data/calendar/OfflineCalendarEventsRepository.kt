package com.example.timemanagementapp.data.calendar

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineCalendarEventsRepository(private val calendarEventDao: CalendarEventDao):
    CalendarEventsRepository {
    override suspend fun insertCalendarEvent(event: CalendarEvent): Int {
        return calendarEventDao.insert(event).toInt()
    }

    override suspend fun updateCalendarEvent(event: CalendarEvent) = calendarEventDao.update(event)

    override suspend fun deleteCalendarEvent(event: CalendarEvent) = calendarEventDao.delete(event)

    override suspend fun getEventByDate(date: LocalDate): CalendarEvent? = calendarEventDao.getEventByDate(date)

    override fun getEventByDateFlow(date: LocalDate): Flow<CalendarEvent?> = calendarEventDao.getEventByDateFlow(date)

    override suspend fun getEventById(eventId: Int): CalendarEvent? = calendarEventDao.getEventById(eventId)

    override suspend fun getOrCreateEventIdForDate(date: LocalDate): Int {
        val existingEvent = getEventByDate(date)

        return existingEvent?.eventId ?: insertCalendarEvent(
            CalendarEvent(
                date = date
            )
        )
    }
}