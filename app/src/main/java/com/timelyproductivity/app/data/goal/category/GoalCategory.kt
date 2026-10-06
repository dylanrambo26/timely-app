package com.timelyproductivity.app.data.goal.category

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goal_categories",
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class GoalCategory(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val name: String,
    val color: CategoryColor
)