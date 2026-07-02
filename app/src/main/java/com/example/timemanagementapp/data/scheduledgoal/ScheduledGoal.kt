package com.example.timemanagementapp.data.scheduledgoal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.timemanagementapp.data.calendar.CalendarEvent
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CalendarEvent::class,
            parentColumns = ["eventId"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["goalID"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["eventId", "goalId"], unique = true)
    ],
    tableName = "scheduled_goals"
)
data class ScheduledGoal(
    @PrimaryKey(autoGenerate = true)
    val scheduledGoalId: Int = 0,

    val eventId: Int,
    val goalId: Int,

    val status: GoalStatus = GoalStatus.NOT_STARTED,
    val startTimeMillis: Long = 0L,
    val completedMillis: Long = 0L
)