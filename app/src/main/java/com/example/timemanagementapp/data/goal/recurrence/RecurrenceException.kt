package com.example.timemanagementapp.data.goal.recurrence

import androidx.room.Entity
import java.time.LocalDate

@Entity(
    primaryKeys = ["recurrenceRuleId", "date"],
    tableName = "recurrence_exceptions"
)
data class RecurrenceException(
    val recurrenceRuleId: Int,
    val date: LocalDate
)
