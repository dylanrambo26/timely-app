package com.timelyproductivity.app.data.scheduledgoal

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.timelyproductivity.app.data.calendar.CalendarEvent
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.GoalsDatabase
import com.timelyproductivity.app.data.goal.recurrence.RecurrenceRule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ScheduledGoalDaoTest {

    private lateinit var database: GoalsDatabase
    private lateinit var scheduledGoalDao: ScheduledGoalDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            GoalsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        scheduledGoalDao = database.scheduledGoalDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun deleteFutureIncompleteRecurringGoals_preservesRunningAndCompleted() = runBlocking {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val goalId = database.goalDao().insert(
            Goal(
                goalTitle = "Test recurring goal",
                hours = 1,
                minutes = 0
            )
        ).toInt()

        val recurrenceRuleId = database.recurrenceRuleDao().insertRecurrenceRule(
            RecurrenceRule(
                goalId = goalId,
                recurringDays = setOf(
                    today.dayOfWeek,
                    tomorrow.dayOfWeek
                ),
                startDate = today
            )
        ).toInt()

        val todayEventId = database.calendarEventDao().insert(CalendarEvent(date = today)).toInt()
        val tomorrowEventId = database.calendarEventDao().insert(CalendarEvent(date = tomorrow)).toInt()

        val statuses = listOf(
            GoalStatus.NOT_STARTED,
            GoalStatus.RUNNING,
            GoalStatus.COMPLETED
        )

        statuses.forEach { status ->
            scheduledGoalDao.insert(
                ScheduledGoal(
                    eventId = todayEventId,
                    goalId = goalId,
                    recurrenceRuleId = recurrenceRuleId,
                    scheduledGoalTitle = "Test recurring goal",
                    scheduledHours = 1,
                    scheduledMinutes = 0,
                    status = status
                )
            )
        }

        scheduledGoalDao.insert(
            ScheduledGoal(
                eventId = tomorrowEventId,
                goalId = goalId,
                recurrenceRuleId = recurrenceRuleId,
                scheduledGoalTitle = "Tomorrow's goal",
                scheduledHours = 1,
                scheduledMinutes = 0,
                status = GoalStatus.NOT_STARTED
            )
        )

        // Execute the DAO method being tested.
        scheduledGoalDao
            .deleteFutureIncompleteRecurringGoalsByRecurrenceId(
                recurrenceRuleId = recurrenceRuleId,
                startDate = today
            )

        // Retrieve remaining goals.
        val remainingToday =
            scheduledGoalDao.getScheduledGoalsOnce(todayEventId)

        val remainingTomorrow =
            scheduledGoalDao.getScheduledGoalsOnce(tomorrowEventId)

        // Verify only RUNNING and COMPLETED remain.
        assertEquals(2, remainingToday.size)

        assertTrue(
            remainingToday.any {
                it.status == GoalStatus.RUNNING
            }
        )

        assertTrue(
            remainingToday.any {
                it.status == GoalStatus.COMPLETED
            }
        )

        assertTrue(remainingTomorrow.isEmpty())
    }

    @Test
    fun deleteFutureIncompleteRecurringGoals_preservesGoalsBeforeStartDate() =
        runBlocking {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)

            // Insert reusable goal
            val goalId = database.goalDao().insert(
                Goal(
                    goalTitle = "Test recurring goal",
                    hours = 1,
                    minutes = 0
                )
            ).toInt()

            // Insert recurrence rule
            val recurrenceRuleId = database.recurrenceRuleDao()
                .insertRecurrenceRule(
                    RecurrenceRule(
                        goalId = goalId,
                        recurringDays = setOf(
                            yesterday.dayOfWeek,
                            today.dayOfWeek,
                            tomorrow.dayOfWeek
                        ),
                        startDate = yesterday
                    )
                ).toInt()

            // Insert calendar events
            val yesterdayEventId = database.calendarEventDao()
                .insert(CalendarEvent(date = yesterday)).toInt()

            val todayEventId = database.calendarEventDao()
                .insert(CalendarEvent(date = today)).toInt()

            val tomorrowEventId = database.calendarEventDao()
                .insert(CalendarEvent(date = tomorrow)).toInt()

            // Insert one NOT_STARTED goal for each date
            listOf(
                yesterdayEventId,
                todayEventId,
                tomorrowEventId
            ).forEach { eventId ->
                scheduledGoalDao.insert(
                    ScheduledGoal(
                        eventId = eventId,
                        goalId = goalId,
                        recurrenceRuleId = recurrenceRuleId,
                        scheduledGoalTitle = "Test recurring goal",
                        scheduledHours = 1,
                        scheduledMinutes = 0,
                        status = GoalStatus.NOT_STARTED
                    )
                )
            }

            // Delete incomplete recurring goals starting today
            scheduledGoalDao
                .deleteFutureIncompleteRecurringGoalsByRecurrenceId(
                    recurrenceRuleId = recurrenceRuleId,
                    startDate = today
                )

            // Retrieve remaining scheduled goals
            val remainingYesterday =
                scheduledGoalDao.getScheduledGoalsOnce(yesterdayEventId)

            val remainingToday =
                scheduledGoalDao.getScheduledGoalsOnce(todayEventId)

            val remainingTomorrow =
                scheduledGoalDao.getScheduledGoalsOnce(tomorrowEventId)

            // Yesterday's goal should be preserved
            assertEquals(1, remainingYesterday.size)
            assertEquals(
                GoalStatus.NOT_STARTED,
                remainingYesterday.first().status
            )

            // Today's and tomorrow's goals should be deleted
            assertTrue(remainingToday.isEmpty())
            assertTrue(remainingTomorrow.isEmpty())
        }
}