package com.example.timemanagementapp.data.analytics

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface AnalyticsDao {
    @Query("""
        SELECT COUNT(*)
        FROM scheduled_goals sg
        INNER JOIN calendar_events ce
            ON sg.eventId = ce.eventId
        WHERE sg.status = 'COMPLETED'
        AND ce.date BETWEEN :startDate AND :endDate
    """)
    fun getCompletedGoalsCount(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Int>

    @Query("""
        SELECT SUM(completedMillis)
        FROM scheduled_goals sg
        INNER JOIN calendar_events ce
            ON sg.eventId = ce.eventId
        WHERE sg.status = 'COMPLETED'
        AND ce.date BETWEEN :startDate AND :endDate
    """)
    fun getTotalCompletedMillis(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Long>
}