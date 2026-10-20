package com.timelyproductivity.app.data.migration


import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration18to19Test : BaseMigrationTest(){
    @Test
    fun migrate18To19() {
        helper.createDatabase(testDbName, 18).close()

        helper.runMigrationsAndValidate(
            testDbName,
            19,
            true,
        ).close()
    }

    @Test
    fun migrate18To19_preservesGoals() {
        helper.createDatabase(testDbName, 18).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
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
        helper.createDatabase(testDbName, 18).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            execSQL(MigrationTestData.INSERT_CALENDAR_EVENT.trimIndent())
            execSQL(MigrationTestData.INSERT_SCHEDULED_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
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
        helper.createDatabase(testDbName, 18).close()

        val db = helper.runMigrationsAndValidate(
            testDbName,
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

        db.execSQL(MigrationTestData.INSERT_CALENDAR_EVENT.trimIndent())
        db.execSQL(MigrationTestData.INSERT_SCHEDULED_GOAL.trimIndent())
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
}