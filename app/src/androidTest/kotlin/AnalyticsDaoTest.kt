package com.example.timemanagementapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.timemanagementapp.data.analytics.AnalyticsDao
import com.example.timemanagementapp.data.calendar.CalendarEventDao
import com.example.timemanagementapp.data.goal.GoalDao
import com.example.timemanagementapp.data.goal.GoalsDatabase
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalDao
import com.example.timemanagementapp.data.testCalendarEvents
import com.example.timemanagementapp.data.testGoals
import com.example.timemanagementapp.data.testScheduledGoals
import com.example.timemanagementapp.data.testScheduledGoalsDifferentMillis
import com.example.timemanagementapp.data.testScheduledGoalsMixedStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AnalyticsDaoTest {
    private lateinit var analyticsDao: AnalyticsDao
    private lateinit var goalsDatabase: GoalsDatabase
    private lateinit var calendarEventDao: CalendarEventDao
    private lateinit var scheduledGoalDao: ScheduledGoalDao
    private lateinit var goalDao: GoalDao

    private val weekStartDate = LocalDate.of(2026, 8, 9)
    private val monthStartDate = LocalDate.of(2026, 8, 1)
    private val yearStartDate = LocalDate.of(2026, 1, 1)
    private val endDate = LocalDate.of(2026, 8, 15)
    @Before
    fun createDb(){
        val context: Context = ApplicationProvider.getApplicationContext()
        goalsDatabase = Room.inMemoryDatabaseBuilder(context, GoalsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        analyticsDao = goalsDatabase.analyticsDao()
        calendarEventDao = goalsDatabase.calendarEventDao()
        scheduledGoalDao = goalsDatabase.scheduledGoalDao()
        goalDao = goalsDatabase.goalDao()
    }

    private suspend fun insertParentTestData(scheduledGoals: List<ScheduledGoal>){
        testGoals.forEach {
            goalDao.insert(it)
        }

        testCalendarEvents.forEach {
            calendarEventDao.insert(it)
        }

        scheduledGoals.forEach {
            scheduledGoalDao.insert(it)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        goalsDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedGoalsForWeek_returnsCorrectNumberGoals() = runTest {
        insertParentTestData(testScheduledGoals)

        val result = analyticsDao.getCompletedGoalsCount(weekStartDate, endDate).first()

        assertEquals(1, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedGoalsForMonth_returnsCorrectNumberGoals() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getCompletedGoalsCount(monthStartDate, endDate).first()
        assertEquals(2, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedGoalsForYear_returnsCorrectNumberGoals() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getCompletedGoalsCount(yearStartDate, endDate).first()
        assertEquals(3, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisForWeek_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalCompletedMillis(weekStartDate,endDate).first()
        val expected = 60 * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisForMonth_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalCompletedMillis(monthStartDate,endDate).first()
        val expected = 2 * 60 * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisForYear_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalCompletedMillis(yearStartDate,endDate).first()
        val expected = 3 * 60 * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedGoalsWithEmptyDateRange_returnsZero() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getCompletedGoalsCount(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 8)).first()
        assertEquals(0, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisWithEmptyDateRange_returnsZero() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalCompletedMillis(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 8)).first()
        assertEquals(0, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedGoalsWithMixedStatuses_ignoresIncompleteGoals() = runTest {
        insertParentTestData(testScheduledGoalsMixedStatus)
        val result = analyticsDao.getCompletedGoalsCount(yearStartDate, endDate).first()
        assertEquals(1, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisWithMixedStatuses_ignoresIncompleteGoals() = runTest {
        insertParentTestData(testScheduledGoalsMixedStatus)
        val result = analyticsDao.getTotalCompletedMillis(yearStartDate, endDate).first()
        val expected = 60 * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisWithDifferentMillisForWeek_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoalsDifferentMillis)
        val result = analyticsDao.getTotalCompletedMillis(weekStartDate, endDate).first()
        val expected = 15 * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisWithDifferentMillisForMonth_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoalsDifferentMillis)
        val result = analyticsDao.getTotalCompletedMillis(monthStartDate, endDate).first()
        val expected = (15 + 30) * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getCompletedMillisWithDifferentMillisForYear_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoalsDifferentMillis)
        val result = analyticsDao.getTotalCompletedMillis(yearStartDate, endDate).first()
        val expected = (15 + 30 + 45) * 60_000L
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getScheduledMillisForWeek_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalScheduledMillisForCompleteGoals(weekStartDate, endDate).first()
        val expected = ((1 * 60) + 30) * 60_000L // 1hr 30m
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getScheduledMillisForMonth_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalScheduledMillisForCompleteGoals(monthStartDate, endDate).first()
        val expected = (3 * 60) * 60_000L // 3hr
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getScheduledMillisForYear_returnsCorrectMillis() = runTest {
        insertParentTestData(testScheduledGoals)
        val result = analyticsDao.getTotalScheduledMillisForCompleteGoals(yearStartDate, endDate).first()
        val expected = ((4 * 60)+30) * 60_000L // 4hr 30min
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getDailyAnalyticsForWeek_returnsCorrectDailyTotals() = runTest {
        insertParentTestData(testScheduledGoalsMixedStatus)
        val result = analyticsDao.getDailyAnalytics(weekStartDate, endDate).first()

        assertEquals(1, result.size)

        val expected = mapOf(
            LocalDate.of(2026, 8, 10) to Triple(
                0L,
                60 * 60_000L,
                30 * 60_000L
            )
        )

        expected.forEach {(date, values) ->
            val actual = result.first{it.date == date}

            assertEquals(values.first, actual.completedScheduledMillis)
            assertEquals(values.second, actual.partialMillis)
            assertEquals(values.third, actual.unfinishedMillis)
        }
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getDailyAnalyticsForMonth_returnsCorrectDailyTotals() = runTest {
        insertParentTestData(testScheduledGoalsMixedStatus)
        val result = analyticsDao.getDailyAnalytics(monthStartDate, endDate).first()

        assertEquals(2, result.size)

        val expected = mapOf(
            LocalDate.of(2026, 8, 10) to Triple(
                0L,
                60 * 60_000L,
                30 * 60_000L
            ),
            LocalDate.of(2026, 8, 3) to Triple(
                90 * 60_000L,
                0L,
                0L
            )
        )

        expected.forEach {(date, values) ->
            val actual = result.first{it.date == date}

            assertEquals(values.first, actual.completedScheduledMillis)
            assertEquals(values.second, actual.partialMillis)
            assertEquals(values.third, actual.unfinishedMillis)
        }
    }

    @Test
    @Throws(Exception::class)
    fun analyticsDao_getDailyAnalyticsForYear_returnsCorrectDailyTotals() = runTest {
        insertParentTestData(testScheduledGoalsMixedStatus)
        val result = analyticsDao.getDailyAnalytics(yearStartDate, endDate).first()

        assertEquals(3, result.size)

        val expected = mapOf(
            LocalDate.of(2026, 8, 10) to Triple(
                0L,
                60 * 60_000L,
                30 * 60_000L
            ),
            LocalDate.of(2026, 8, 3) to Triple(
                90 * 60_000L,
                0L,
                0L
            ),
            LocalDate.of(2026, 5, 10) to Triple(
                0L,
                60 * 60_000L,
                30 * 60_000L
            )
        )

        expected.forEach {(date, values) ->
            val actual = result.first{it.date == date}

            assertEquals(values.first, actual.completedScheduledMillis)
            assertEquals(values.second, actual.partialMillis)
            assertEquals(values.third, actual.unfinishedMillis)
        }
    }
}