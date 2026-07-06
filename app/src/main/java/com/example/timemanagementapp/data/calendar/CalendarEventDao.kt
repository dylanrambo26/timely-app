package com.example.timemanagementapp.data.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CalendarEventDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(event: CalendarEvent): Long

    @Update
    suspend fun update(event: CalendarEvent)

    @Delete
    suspend fun delete(event: CalendarEvent)

    @Query(
        """
            SELECT * FROM calendar_events WHERE date = :date
        """
    )
    suspend fun getEventByDate(date: LocalDate): CalendarEvent?

    @Query(
        """
            SELECT * FROM calendar_events WHERE date = :date
        """
    )
    fun getEventByDateFlow(date: LocalDate): Flow<CalendarEvent?>

    @Query(
        """
            SELECT * FROM calendar_events WHERE eventId = :eventId
        """
    )
    suspend fun getEventById(eventId: Int): CalendarEvent?
}