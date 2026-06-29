package com.example.timemanagementapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data Class used to store goal information for a day on the calendar
 */
@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,

    val date: LocalDate,
)