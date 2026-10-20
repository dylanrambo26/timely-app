package com.timelyproductivity.app.data.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.timelyproductivity.app.data.goal.GoalsDatabase
import org.junit.Rule

abstract class BaseMigrationTest {
    protected val testDbName: String
        get() = "${javaClass.simpleName}-db"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        GoalsDatabase::class.java
    )
}