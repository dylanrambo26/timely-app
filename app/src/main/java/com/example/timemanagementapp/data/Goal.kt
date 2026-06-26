package com.example.timemanagementapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data Class used to store information about a Goal
 */

@Entity(tableName = "goals")
data class Goal(

    @PrimaryKey(autoGenerate = true)
    val goalID: Int = 0,

    val hours: Int,
    val minutes: Int,
    val goalTitle: String,

    val status: GoalStatus = GoalStatus.NOT_STARTED,
    val startTimeMillis: Long = 0L,
    val completedMillis: Long = 0L
)
