package com.timelyproductivity.app.data.migration

internal object MigrationTestData {
    const val TEST_DB = "migration-test"

    const val INSERT_GOAL = """
        INSERT INTO goals (goalID, hours, minutes, goalTitle)
        VALUES (1, 1, 30, 'Study')
    """

    const val INSERT_CALENDAR_EVENT = """
        INSERT INTO calendar_events (eventId, date)
        VALUES (1, 20000)
    """

    const val INSERT_SCHEDULED_GOAL = """
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
    const val INSERT_RECURRENCE_RULE = """
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
    const val INSERT_RECURRENCE_EXCEPTION = """
        INSERT INTO recurrence_exceptions (
            recurrenceRuleId,
            date
        )
        VALUES (1, 20001)
    """
    const val EXPECTED_GOAL_TITLE = "Study"
    const val EXPECTED_GOAL_HOURS = 1
    const val EXPECTED_GOAL_MINUTES = 30
}