package com.timelyproductivity.app.data.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import com.timelyproductivity.app.data.migration.MigrationTestData.EXPECTED_GOAL_HOURS
import com.timelyproductivity.app.data.migration.MigrationTestData.EXPECTED_GOAL_MINUTES
import com.timelyproductivity.app.data.migration.MigrationTestData.EXPECTED_GOAL_TITLE
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertTrue

internal fun assertScheduledGoalPreserved(
    db: SupportSQLiteDatabase,
    scheduledGoalId: Int,
    expectedCategoryId: Int? = null
) {
    db.query(
        "SELECT * FROM scheduled_goals WHERE scheduledGoalId = $scheduledGoalId"
    ).use { cursor ->
        assertTrue(cursor.moveToFirst())

        assertEquals(
            1,
            cursor.getInt(cursor.getColumnIndexOrThrow("eventId"))
        )

        assertEquals(
            1,
            cursor.getInt(cursor.getColumnIndexOrThrow("goalId"))
        )

        assertEquals(
            "PAUSED",
            cursor.getString(cursor.getColumnIndexOrThrow("status"))
        )

        assertEquals(
            1800000L,
            cursor.getLong(cursor.getColumnIndexOrThrow("completedMillis"))
        )

        val categoryIndex =
            cursor.getColumnIndexOrThrow("scheduledCategoryId")

        if (expectedCategoryId == null) {
            assertTrue(cursor.isNull(categoryIndex))
        } else {
            assertEquals(
                expectedCategoryId,
                cursor.getInt(categoryIndex)
            )
        }
    }
}

internal fun assertGoalPreserved(
    db: SupportSQLiteDatabase,
    goalId: Int,
    expectedCategoryId: Int? = null
){
    db.query("SELECT * FROM goals WHERE goalID = $goalId").use { cursor ->
        assertTrue(cursor.moveToFirst())

        assertEquals(
            EXPECTED_GOAL_TITLE,
            cursor.getString(
                cursor.getColumnIndexOrThrow("goalTitle")
            )
        )

        assertEquals(
            EXPECTED_GOAL_HOURS,
            cursor.getInt(
                cursor.getColumnIndexOrThrow("hours")
            )
        )

        assertEquals(
            EXPECTED_GOAL_MINUTES,
            cursor.getInt(
                cursor.getColumnIndexOrThrow("minutes")
            )
        )

        val categoryIndex =
            cursor.getColumnIndexOrThrow("categoryId")

        if (expectedCategoryId == null) {
            assertTrue(cursor.isNull(categoryIndex))
        } else {
            assertEquals(
                expectedCategoryId,
                cursor.getInt(categoryIndex)
            )
        }
    }
}

internal fun assertCategoryId(
    db: SupportSQLiteDatabase,
    tableName: String,
    idColumn: String,
    id: Int,
    categoryColumn: String,
    expectedCategoryId: Int?
) {
    db.query(
        "SELECT $categoryColumn FROM $tableName WHERE $idColumn = $id"
    ).use { cursor ->
        assertTrue(cursor.moveToFirst())

        if (expectedCategoryId == null) {
            assertTrue(cursor.isNull(0))
        } else {
            assertEquals(
                expectedCategoryId,
                cursor.getInt(0)
            )
        }
    }
}