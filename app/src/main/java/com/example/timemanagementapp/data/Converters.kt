package com.example.timemanagementapp.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String {
        return status.name
    }

    @TypeConverter
    fun toGoalStatus(status: String): GoalStatus {
        return GoalStatus.valueOf(status)
    }
}