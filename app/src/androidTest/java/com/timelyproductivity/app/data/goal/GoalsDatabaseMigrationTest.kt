package com.timelyproductivity.app.data.goal


import android.database.sqlite.SQLiteConstraintException
import androidx.room.testing.MigrationTestHelper
import androidx.room.util.getColumnIndexOrThrow
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoalsDatabaseMigrationTest {

    companion object {
        private const val TEST_DB = "migration-test"

        private const val INSERT_GOAL = """
        INSERT INTO goals (goalID, hours, minutes, goalTitle)
        VALUES (1, 1, 30, 'Study')
    """

        private const val INSERT_CALENDAR_EVENT = """
        INSERT INTO calendar_events (eventId, date)
        VALUES (1, 20000)
    """

        private const val INSERT_SCHEDULED_GOAL = """
        INSERT INTO scheduled_goals (
            scheduledGoalId,
            eventId,
            goalId,
            status,
            startTimeMillis,
            completedMillis,
            isCustomized,
            scheduledGoalTitle,
            scheduledHours,
            scheduledMinutes,
            recurrenceRuleId
        )
        VALUES (1, 1, 1, 'PAUSED', 0, 1800000,
                0, 'Study', 1, 30, NULL)
    """
        private const val INSERT_RECURRENCE_RULE = """
        INSERT INTO recurrence_rules (
            recurrenceRuleId,
            goalId,
            recurringDays,
            startDate,
            endDate
        )
        VALUES (
            1, 1, 'MONDAY,WEDNESDAY,FRIDAY', 20000, NULL
        )
    """
        private const val INSERT_RECURRENCE_EXCEPTION = """
        INSERT INTO recurrence_exceptions (
            recurrenceRuleId,
            date
        )
        VALUES (1, 20001)
    """
        private const val EXPECTED_GOAL_TITLE = "Study"
        private const val EXPECTED_GOAL_HOURS = 1
        private const val EXPECTED_GOAL_MINUTES = 30
    }


    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        GoalsDatabase::class.java
    )

    @Test
    fun migrate17to18(){
        helper.createDatabase(TEST_DB, 17).close()

        helper.runMigrationsAndValidate(
            TEST_DB,
            18,
            true
        )
    }

    @Test
    fun migrate17To18_preservesExistingGoals(){
        helper.createDatabase(TEST_DB, 17).apply {
            execSQL(INSERT_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
            18,
            true
        )

        db.query(
            "SELECT * FROM goals WHERE goalID = 1"
        ).use { cursor ->
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
        }
    }

    @Test
    fun migrate17To18_enforcesUniqueCategoryNames() {
        helper.createDatabase(TEST_DB, 17).close()

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
            18,
            true
        )

        db.execSQL(
            """
            INSERT INTO goal_categories (name, color)
            VALUES ('Work', 'ORANGE')
            """.trimIndent()
        )

        assertThrows(
            SQLiteConstraintException::class.java
        ) {
            db.execSQL(
                """
                INSERT INTO goal_categories (name, color)
                VALUES ('Work', 'BLUE')
                """.trimIndent()
            )
        }
    }

    @Test
    fun migrate18To19() {
        helper.createDatabase(TEST_DB, 18).close()

        helper.runMigrationsAndValidate(
            TEST_DB,
            19,
            true,
        ).close()
    }

    @Test
    fun migrate18To19_preservesGoals() {
        helper.createDatabase(TEST_DB, 18).apply {
            execSQL(INSERT_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
            19,
            true,
        )
        assertGoalPreserved(
            db = db,
            goalId = 1,

        )
        db.close()
    }

    @Test
    fun migrate18To19_preservesScheduledGoals(){
        helper.createDatabase(TEST_DB, 18).apply {
            execSQL(INSERT_GOAL.trimIndent())
            execSQL(INSERT_CALENDAR_EVENT.trimIndent())
            execSQL(INSERT_SCHEDULED_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
            19,
            true,
        )

        assertScheduledGoalPreserved(
            db = db,
            scheduledGoalId = 1
        )

        db.close()
    }

    @Test
    fun migrate18To19_categoryDeletionSetsNull() {
        helper.createDatabase(TEST_DB, 18).close()

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
            19,
            true,
        )

        db.execSQL("PRAGMA foreign_keys = ON")

        db.execSQL(
            """
        INSERT INTO goal_categories (categoryId, name, color)
        VALUES (1, 'Programming', 'BLUE')
        """.trimIndent()
        )

        db.execSQL(
            """
        INSERT INTO goals (
            goalID, hours, minutes, goalTitle, categoryId
        )
        VALUES (1, 1, 30, 'Study', 1)
        """.trimIndent()
        )

        db.execSQL(INSERT_CALENDAR_EVENT.trimIndent())
        db.execSQL(INSERT_SCHEDULED_GOAL.trimIndent())
        db.execSQL(
            """
                UPDATE scheduled_goals
                SET scheduledCategoryId = 1
                WHERE scheduledGoalId = 1
            """.trimIndent()
        )

        assertCategoryId(
            db = db,
            tableName = "goals",
            idColumn = "goalID",
            id = 1,
            categoryColumn = "categoryId",
            expectedCategoryId = 1
        )

        assertCategoryId(
            db = db,
            tableName = "scheduled_goals",
            idColumn = "scheduledGoalId",
            id = 1,
            categoryColumn = "scheduledCategoryId",
            expectedCategoryId = 1
        )

        db.execSQL(
            "DELETE FROM goal_categories WHERE categoryId = 1"
        )

        assertGoalPreserved(
            db = db,
            goalId = 1,
        )

        assertScheduledGoalPreserved(
            db = db,
            scheduledGoalId = 1
        )

        assertCategoryId(
            db = db,
            tableName = "goals",
            idColumn = "goalID",
            id = 1,
            categoryColumn = "categoryId",
            expectedCategoryId = null
        )

        assertCategoryId(
            db = db,
            tableName = "scheduled_goals",
            idColumn = "scheduledGoalId",
            id = 1,
            categoryColumn = "scheduledCategoryId",
            expectedCategoryId = null
        )
        db.close()
    }

    @Test
    fun migrate17To19_preservesExistingData() {
        helper.createDatabase(TEST_DB, 17).apply {
            execSQL(INSERT_GOAL.trimIndent())
            execSQL(INSERT_CALENDAR_EVENT.trimIndent())
            execSQL(INSERT_SCHEDULED_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
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
        helper.createDatabase(TEST_DB, 17).apply {
            execSQL(INSERT_GOAL.trimIndent())
            execSQL(INSERT_RECURRENCE_RULE.trimIndent())
            execSQL(INSERT_RECURRENCE_EXCEPTION.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB,
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

    private fun assertScheduledGoalPreserved(
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

    private fun assertGoalPreserved(
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

    private fun assertCategoryId(
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


}