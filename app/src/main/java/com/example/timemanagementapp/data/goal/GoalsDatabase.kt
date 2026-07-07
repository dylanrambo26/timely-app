package com.example.timemanagementapp.data.goal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timemanagementapp.data.calendar.CalendarEvent
import com.example.timemanagementapp.data.Converters
import com.example.timemanagementapp.data.calendar.CalendarEventDao
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalDao

@Database(
    entities = [
        Goal::class,
        CalendarEvent::class,
        ScheduledGoal::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GoalsDatabase : RoomDatabase(){
    abstract fun goalDao(): GoalDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun scheduledGoalDao(): ScheduledGoalDao
    companion object {

        @Volatile
        private var Instance: GoalsDatabase? = null

        fun getDatabase(context: Context): GoalsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GoalsDatabase::class.java, "item_database").fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}