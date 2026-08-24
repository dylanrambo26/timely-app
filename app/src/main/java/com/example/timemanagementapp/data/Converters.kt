package com.example.timemanagementapp.data

import androidx.room.TypeConverter
import com.example.timemanagementapp.data.goal.GoalStatus
import java.time.DayOfWeek
import java.time.LocalDate

class Converters {

    //Type Converters to use enum class GoalStatus in Room
    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String {
        return status.name
    }

    @TypeConverter
    fun toGoalStatus(status: String): GoalStatus {
        return GoalStatus.valueOf(status)
    }

    //Type Converters to use LocalDate in Room
    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(epochDay: Long): LocalDate {
        return epochDay.let {LocalDate.ofEpochDay(it)}
    }

    //Type converters to use DayOfWeek in Room
    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>): String {
        return days.joinToString(",") {it.name}
    }

    @TypeConverter
    fun toDayOfWeekSet(value: String): Set<DayOfWeek> {
        if(value.isBlank()){
            return emptySet()
        }

        return value
            .split(",")
            .map { DayOfWeek.valueOf(it) }
            .toSet()
    }
}