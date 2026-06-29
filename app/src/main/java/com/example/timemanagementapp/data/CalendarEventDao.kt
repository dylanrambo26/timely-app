package com.example.timemanagementapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface CalendarEventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: CalendarEvent)

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
}