package com.timelyproductivity.app.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.timelyproductivity.app.data.goal.category.GoalCategory

/**
 * Data Class used to store information about a Goal
 */

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = GoalCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Goal(

    @PrimaryKey(autoGenerate = true)
    val goalID: Int = 0,

    val hours: Int,
    val minutes: Int,
    val goalTitle: String,

    val categoryId: Int? = null
)