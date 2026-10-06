package com.timelyproductivity.app.data.goal


import android.database.sqlite.SQLiteConstraintException
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoalsDatabaseMigrationTest {

    private val testDb = "migration-test"


    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        GoalsDatabase::class.java
    )

    @Test
    fun migrate17to18(){
        helper.createDatabase(testDb, 17).close()

        helper.runMigrationsAndValidate(
            testDb,
            18,
            true
        )
    }

    @Test
    fun migrate17To18_preservesExistingGoals(){
        helper.createDatabase(testDb, 17).apply {
            execSQL(
                """
                    INSERT INTO goals (goalID, hours, minutes, goalTitle)
                    VALUES (1, 1, 30, 'Test Goal')
                """.trimIndent()
            )

            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDb,
            18,
            true
        )

        db.query(
            "SELECT * FROM goals WHERE goalID = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            assertEquals(
                "Test Goal",
                cursor.getString(
                    cursor.getColumnIndexOrThrow("goalTitle")
                )
            )

            assertEquals(
                1,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("hours")
                )
            )

            assertEquals(
                30,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("minutes")
                )
            )
        }
    }

    @Test
    fun migrate17To18_enforcesUniqueCategoryNames() {
        helper.createDatabase(testDb, 17).close()

        val db = helper.runMigrationsAndValidate(
            testDb,
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

}