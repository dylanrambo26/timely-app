package com.example.timemanagementapp.data.goal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

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

    
}