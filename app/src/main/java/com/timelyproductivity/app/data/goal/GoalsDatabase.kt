package com.timelyproductivity.app.data.goal

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timelyproductivity.app.data.Converters
import com.timelyproductivity.app.data.analytics.AnalyticsDao
import com.timelyproductivity.app.data.calendar.CalendarEvent
import com.timelyproductivity.app.data.calendar.CalendarEventDao
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.category.GoalCategoryDao
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
        RecurrenceException::class,
        GoalCategory::class
    ],
    version = 19,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 17,
            to = 18
        ),
        AutoMigration(
            from = 18,
            to = 19
        )
    ]
)
@TypeConverters(Converters::class)
abstract class GoalsDatabase : RoomDatabase(){
    abstract fun goalDao(): GoalDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun scheduledGoalDao(): ScheduledGoalDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun recurrenceRuleDao(): RecurrenceRuleDao
    abstract fun goalCategoryDao(): GoalCategoryDao
    companion object {

        @Volatile
        private var Instance: GoalsDatabase? = null

        fun getDatabase(context: Context): GoalsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GoalsDatabase::class.java, "item_database").build().also { Instance = it }
            }
        }
    }
}