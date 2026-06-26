package com.example.timemanagementapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Goal::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GoalsDatabase : RoomDatabase(){
    abstract fun goalDao(): GoalDao
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