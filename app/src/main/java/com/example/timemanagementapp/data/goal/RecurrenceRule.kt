package com.example.timemanagementapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    ]
)
data class RecurrenceRule(
    @PrimaryKey(autoGenerate = true)
    val recurrenceRuleId: Int = 0,

    val goalId: Int,
    val recurringDays: Set<DayOfWeek>,
    val startDate: LocalDate,
    val endDate: LocalDate? = null
)
