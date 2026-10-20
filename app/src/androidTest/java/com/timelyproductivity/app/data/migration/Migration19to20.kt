package com.timelyproductivity.app.data.migration

import junit.framework.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration19to20 : BaseMigrationTest() {
    @Test
    fun migrate19to20(){
        helper.createDatabase(testDbName, 19).close()

        helper.runMigrationsAndValidate(
            testDbName,
            20,
            true
        )
    }

    @Test
    fun migrate19To20_preservesGoals() {
        helper.createDatabase(testDbName, 19).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
            20,
            true
        )

        // Verify existing goal fields were preserved
        assertGoalPreserved(
            db = db,
            goalId = 1
        )

        // Verify the new priority field defaults to null
        db.query(
            "SELECT priority FROM goals WHERE goalID = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.isNull(0))
        }

        db.close()
    }

    @Test
    fun migrate19To20_preservesScheduledGoals(){
        helper.createDatabase(testDbName, 19).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            execSQL(MigrationTestData.INSERT_CALENDAR_EVENT.trimIndent())
            execSQL(MigrationTestData.INSERT_SCHEDULED_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
            20,
            true,
        )

        assertScheduledGoalPreserved(
            db = db,
            scheduledGoalId = 1
        )

        // Verify the new priority field defaults to null
        db.query(
            "SELECT scheduledPriority FROM scheduled_goals WHERE scheduledGoalId = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.isNull(0))
        }

        db.query("PRAGMA foreign_key_check").use { cursor ->
            assertEquals(0, cursor.count)
        }

        db.close()
    }
}