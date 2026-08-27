package com.example.timemanagementapp.data.goal.recurrence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface RecurrenceRuleDao {

    @Insert
    suspend fun insertRecurrenceRule(
        recurrenceRule: RecurrenceRule
    ): Long

    @Update
    suspend fun updateRecurrenceRule(
        recurrenceRule: RecurrenceRule
    )

    @Delete
    suspend fun deleteRecurrenceRule(
        recurrenceRule: RecurrenceRule
    )

    //Query for fetching a list of recurrence rules whose time periods fall within the requested range
    @Query("""
        SELECT * FROM RecurrenceRule
        WHERE startDate <= :endDate
        AND (
            endDate IS NULL
            OR endDate >= :startDate
        )
    """)
    suspend fun getRecurrenceRulesOverlappingRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<RecurrenceRule>
}