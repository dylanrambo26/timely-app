package com.example.timemanagementapp.data.analytics

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface AnalyticsDao {

    //Get the number of goals that have been completed within the date range
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


    //Get the total time that has been completed from completed goals in the date range
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

    //Get the total time scheduled from completed goals in the date range
    @Query("""
        SELECT COALESCE(
            SUM(
                ((sg.scheduledHours * 60 + sg.scheduledMinutes) * 60000)
            ),
            0
        )
        FROM scheduled_goals sg
        INNER JOIN calendar_events ce
            ON sg.eventId = ce.eventId
        WHERE sg.status == 'COMPLETED'
        AND ce.date BETWEEN :startDate AND :endDate 
    """)
    fun getCompletedScheduledMillis(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Long>

    //Get the original scheduled amount of time for completed goals within the date range
    @Query("""
        SELECT SUM((scheduledHours * 60 + scheduledMinutes) * 60000)
        FROM scheduled_goals sg
        INNER JOIN calendar_events ce
            ON sg.eventId = ce.eventId
        WHERE sg.status == 'COMPLETED'
        AND ce.date BETWEEN :startDate AND :endDate
    """)
    fun getTotalScheduledMillisForCompleteGoals(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Long>


    //Get the completedMillis from goals that are not completed showing partial progress in the date range
    @Query("""
        SELECT SUM(completedMillis)
        FROM scheduled_goals sg
        INNER JOIN calendar_events ce
            ON sg.eventId = ce.eventId
        WHERE sg.status != 'COMPLETED'
        AND ce.date BETWEEN :startDate AND :endDate
    """)
    fun getPartialCompletedMillis(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Long>


    //Get the amount of time not completed on incomplete goals in the date range
    @Query("""
        SELECT COALESCE(
            SUM(
                ((sg.scheduledHours * 60 + sg.scheduledMinutes) * 60000) - sg.completedMillis
            ),
            0
        )
        FROM scheduled_goals sg
        INNER JOIN calendar_events ce
            ON sg.eventId = ce.eventId
        WHERE sg.status != 'COMPLETED'
        AND ce.date BETWEEN :startDate AND :endDate
    """)
    fun getUnfinishedMillis(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Long>

    @Query("""
        SELECT
            ce.date AS date,
            
            -- Sum of the durations of goals that are completed
            COALESCE(SUM(
                CASE
                    WHEN sg.status = 'COMPLETED'
                    THEN (sg.scheduledHours * 60 + sg.scheduledMinutes) * 60000
                    ELSE 0
                END
            ), 0) AS completedScheduledMillis,

            -- Sum of the completedMillis from goals that are unfinished
            COALESCE(SUM(
                CASE
                    WHEN sg.status != 'COMPLETED'
                    THEN sg.completedMillis
                    ELSE 0
                END
            ), 0) AS partialMillis,
            
            -- Sum of the time that has not been completed on unfinished goals
            COALESCE(SUM(
                CASE
                    WHEN sg.status != 'COMPLETED'
                    THEN ((sg.scheduledHours * 60 + sg.scheduledMinutes) * 60000)
                         - sg.completedMillis
                    ELSE 0
                END
            ), 0) AS unfinishedMillis
    
        FROM calendar_events ce
        INNER JOIN scheduled_goals sg
            ON sg.eventId = ce.eventId
    
        WHERE ce.date BETWEEN :startDate AND :endDate
    
        GROUP BY ce.date
        ORDER BY ce.date
    """)
    fun getDailyAnalytics(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DailyAnalytics>>
}