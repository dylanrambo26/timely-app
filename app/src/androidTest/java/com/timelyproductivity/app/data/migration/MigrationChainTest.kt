package com.timelyproductivity.app.data.migration

import junit.framework.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MigrationChainTest : BaseMigrationTest() {
    @Test
    fun migrate17To19_preservesExistingData() {
        helper.createDatabase(testDbName, 17).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            execSQL(MigrationTestData.INSERT_CALENDAR_EVENT.trimIndent())
            execSQL(MigrationTestData.INSERT_SCHEDULED_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
            19,
            true
        )

        assertGoalPreserved(
            db = db,
            goalId = 1
        )

        assertScheduledGoalPreserved(
            db = db,
            scheduledGoalId = 1
        )

        db.query("PRAGMA foreign_key_check").use { cursor ->
            assertEquals(0, cursor.count)
        }

        db.close()
    }

    @Test
    fun migrate17To19_preservesRecurrenceRules() {
        helper.createDatabase(testDbName, 17).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            execSQL(MigrationTestData.INSERT_RECURRENCE_RULE.trimIndent())
            execSQL(MigrationTestData.INSERT_RECURRENCE_EXCEPTION.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
            19,
            true
        )

        db.query(
            "SELECT * FROM recurrence_rules WHERE recurrenceRuleId = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            assertEquals(
                1,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("goalId")
                )
            )

            assertEquals(
                "MONDAY,WEDNESDAY,FRIDAY",
                cursor.getString(
                    cursor.getColumnIndexOrThrow("recurringDays")
                )
            )

            assertEquals(
                20000L,
                cursor.getLong(
                    cursor.getColumnIndexOrThrow("startDate")
                )
            )

            assertTrue(
                cursor.isNull(
                    cursor.getColumnIndexOrThrow("endDate")
                )
            )
        }

        db.query(
            """
                SELECT * FROM recurrence_exceptions
                WHERE recurrenceRuleId = 1 AND date = 20001
            """.trimIndent()
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
        }

        assertGoalPreserved(
            db = db,
            goalId = 1
        )

        db.query("PRAGMA foreign_key_check").use { cursor ->
            assertEquals(0, cursor.count)
        }

        db.close()
    }

    @Test
    fun migrate17To20_preservesExistingData() {
        helper.createDatabase(testDbName, 17).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            execSQL(MigrationTestData.INSERT_CALENDAR_EVENT.trimIndent())
            execSQL(MigrationTestData.INSERT_SCHEDULED_GOAL.trimIndent())
            execSQL(MigrationTestData.INSERT_RECURRENCE_RULE.trimIndent())
            execSQL(MigrationTestData.INSERT_RECURRENCE_EXCEPTION.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
            20,
            true
        )

        // Verify reusable goal
        assertGoalPreserved(
            db = db,
            goalId = 1
        )

        // Verify scheduled goal
        assertScheduledGoalPreserved(
            db = db,
            scheduledGoalId = 1
        )

        // Verify new priority columns default to NULL
        db.query(
            "SELECT priority FROM goals WHERE goalID = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.isNull(0))
        }

        db.query(
            "SELECT scheduledPriority FROM scheduled_goals WHERE scheduledGoalId = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.isNull(0))
        }

        // Verify recurrence rule survived
        db.query(
            "SELECT goalId FROM recurrence_rules WHERE recurrenceRuleId = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }

        // Verify recurrence exception survived
        db.query(
            """
        SELECT * FROM recurrence_exceptions
        WHERE recurrenceRuleId = 1 AND date = 20001
        """.trimIndent()
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
        }

        // Verify foreign-key integrity
        db.query("PRAGMA foreign_key_check").use { cursor ->
            assertEquals(0, cursor.count)
        }

        db.close()
    }
}