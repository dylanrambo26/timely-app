package com.timelyproductivity.app.data.goal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timelyproductivity.app.data.Converters
import com.timelyproductivity.app.data.analytics.AnalyticsDao
import com.timelyproductivity.app.data.calendar.CalendarEvent
import com.timelyproductivity.app.data.calendar.CalendarEventDao
import com.timelyproductivity.app.data.goal.recurrence.RecurrenceException
import com.timelyproductivity.app.data.goal.recurrence.RecurrenceRule
import com.timelyproductivity.app.data.goal.recurrence.RecurrenceRuleDao
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalDao

@Database(
    entities = [
        Goal::class,
        CalendarEvent::class,
        ScheduledGoal::class,
        RecurrenceRule::class,
        RecurrenceException::class
    ],
    version = 17,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GoalsDatabase : RoomDatabase(){
    abstract fun goalDao(): GoalDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun scheduledGoalDao(): ScheduledGoalDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun recurrenceRuleDao(): RecurrenceRuleDao
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