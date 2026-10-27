package com.timelyproductivity.app.data.analytics

import androidx.room.Embedded
import com.timelyproductivity.app.data.goal.category.GoalCategory

data class CategoryActivity(
    @Embedded
    val category: GoalCategory?,

    val scheduledMillis: Long,
    val completedMillis: Long,
)