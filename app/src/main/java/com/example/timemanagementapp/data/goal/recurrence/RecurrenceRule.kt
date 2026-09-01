package com.example.timemanagementapp.data.goal.recurrence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.timemanagementapp.data.goal.Goal
import java.time.DayOfWeek
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["goalID"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    tableName = "recurrence_rules"
)
data class RecurrenceRule(
    @PrimaryKey(autoGenerate = true)
    val recurrenceRuleId: Int = 0,

    val goalId: Int,
    val recurringDays: Set<DayOfWeek>,
    val startDate: LocalDate,
    val endDate: LocalDate? = null
)
