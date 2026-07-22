package com.example.timemanagementapp.data.scheduledgoal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.timemanagementapp.data.calendar.CalendarEvent
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalStatus

@Entity(
    // Don't delete scheduled goals when associated reusable goal is deleted
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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    tableName = "scheduled_goals"
)
data class ScheduledGoal(
    @PrimaryKey(autoGenerate = true)
    val scheduledGoalId: Int = 0,

    val eventId: Int,
    val goalId: Int?,

    val status: GoalStatus = GoalStatus.NOT_STARTED,
    val startTimeMillis: Long = 0L,
    val completedMillis: Long = 0L,

    //Can be based of the reusable goal from goalid or its own values
    val scheduledGoalTitle: String,
    val scheduledHours: Int,
    val scheduledMinutes: Int,

    //Optional overrides for user when editing scheduled goal for a specific day
    /*val customTitle: String? = null,
    val customHours: Int? = null,
    val customMinutes: Int? = null*/
)