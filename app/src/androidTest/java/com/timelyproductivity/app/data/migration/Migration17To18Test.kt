package com.timelyproductivity.app.data.migration

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration17To18Test : BaseMigrationTest(){

    @Test
    fun migrate17to18(){
        helper.createDatabase(testDbName, 17).close()

        helper.runMigrationsAndValidate(
            testDbName,
            18,
            true
        )
    }

    @Test
    fun migrate17To18_preservesExistingGoals(){
        helper.createDatabase(testDbName, 17).apply {
            execSQL(MigrationTestData.INSERT_GOAL.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(
            testDbName,
            18,
            true
        )

        db.query(
            "SELECT * FROM goals WHERE goalID = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            assertEquals(
                MigrationTestData.EXPECTED_GOAL_TITLE,
                cursor.getString(
                    cursor.getColumnIndexOrThrow("goalTitle")
                )
            )

            assertEquals(
                MigrationTestData.EXPECTED_GOAL_HOURS,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("hours")
                )
            )

            assertEquals(
                MigrationTestData.EXPECTED_GOAL_MINUTES,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("minutes")
                )
            )
        }
    }

    @Test
    fun migrate17To18_enforcesUniqueCategoryNames() {
        helper.createDatabase(testDbName, 17).close()

        val db = helper.runMigrationsAndValidate(
            testDbName,
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