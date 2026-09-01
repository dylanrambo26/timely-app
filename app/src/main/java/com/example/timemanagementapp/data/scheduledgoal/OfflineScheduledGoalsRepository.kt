package com.example.timemanagementapp.data.scheduledgoal

import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.goal.GoalDao
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceException
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceRuleDao
import com.example.timemanagementapp.util.MINUTES_IN_24_HOUR_DAY
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineScheduledGoalsRepository(
    private val scheduledGoalDao: ScheduledGoalDao,
    private val goalDao: GoalDao,
    private val recurrenceRuleDao: RecurrenceRuleDao,
    private val calendarEventsRepository: CalendarEventsRepository
): ScheduledGoalsRepository {
    override suspend fun insertScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.insert(scheduledGoal)

    override suspend fun updateScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.update(scheduledGoal)

    override suspend fun deleteScheduledGoal(scheduledGoal: ScheduledGoal) = scheduledGoalDao.delete(scheduledGoal)

    override suspend fun deleteScheduledGoalsByGoalId(goalId: Int) = scheduledGoalDao.deleteScheduledGoalsByGoalId(goalId)

    override suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal = scheduledGoalDao.getScheduledGoalOnce(id)

    override fun getScheduledGoals(eventId: Int): Flow<List<ScheduledGoal>> = scheduledGoalDao.getScheduledGoalsForDate(eventId)

    override fun getScheduledGoal(id: Int): Flow<ScheduledGoal> = scheduledGoalDao.getScheduledGoal(id)

    override suspend fun getScheduledGoalsOnce(eventId: Int): List<ScheduledGoal> = scheduledGoalDao.getScheduledGoalsOnce(eventId)

    //Used to validate existing goals against remaining time
    override suspend fun validInsertScheduledGoal(goalId: Int, eventId: Int): Boolean {
        val goal = goalDao.getGoalOnce(goalId)

        val scheduledGoals = getScheduledGoalsOnce(eventId)
        val usedMinutes = scheduledGoals.sumOf { it.scheduledHours * 60 + it.scheduledMinutes }

        val remainingMinutes = MINUTES_IN_24_HOUR_DAY - usedMinutes
        val newScheduledGoalTotalMinutes = goal.hours * 60 + goal.minutes

        if (newScheduledGoalTotalMinutes > remainingMinutes){
            return false
        }

        insertScheduledGoal(
            ScheduledGoal(
                goalId = goalId,
                eventId = eventId,
                scheduledGoalTitle = goal.goalTitle,
                scheduledHours = goal.hours,
                scheduledMinutes = goal.minutes,
            )
        )

        return true
    }

    //Used to validate new/edited goals against remaining time
    override suspend fun isValidDurationForDate(
        goalTotalMinutes: Int,
        eventId: Int,
        excludedScheduledGoalId: Int? //to exclude existing scheduled goal total when editing a scheduled goal
    ): Boolean {
        val scheduledGoals = getScheduledGoalsOnce(eventId)
        val usedMinutes = scheduledGoals
            .filter {it.scheduledGoalId != excludedScheduledGoalId}
            .sumOf {
                val hours = it.scheduledHours
                val minutes = it.scheduledMinutes

                hours * 60 + minutes
            }

        return usedMinutes + goalTotalMinutes <= MINUTES_IN_24_HOUR_DAY
    }

    override suspend fun updateFutureScheduledGoalsFromEditedTemplate(
        goalId: Int,
        title: String,
        hours: Int,
        minutes: Int,
        startDate: LocalDate
    ) {
        scheduledGoalDao.updateFutureScheduledGoalsFromEditedTemplate(goalId, title, hours, minutes, startDate)
    }

    override fun getDatesWithScheduledGoals(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>> = scheduledGoalDao.getDatesWithScheduledGoals(startDate, endDate)

    //Schedule goals according to their corresponding recurrence rules (if applicable)
    override suspend fun ensureRecurringGoalsScheduledForRange(
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        val recurrenceRules =
            recurrenceRuleDao.getRecurrenceRulesOverlappingRange(
                startDate,
                endDate
            )

        recurrenceRules.forEach { rule ->
            val goal = goalDao.getGoalOnce(rule.goalId)

            val rangeStart = maxOf(startDate, rule.startDate)
            val rangeEnd = rule.endDate?.let {
                minOf(endDate, it)
            } ?: endDate

            if(rangeStart.isAfter(rangeEnd)){
                return@forEach
            }

            var date = rangeStart

            //Used to not generate goals for scheduled goals that were deleted
            val exceptionDates = recurrenceRuleDao.getRecurrenceExceptionDatesForRange(
                recurrenceRuleId = rule.recurrenceRuleId,
                startDate = rangeStart,
                endDate = rangeEnd
            ).toSet()

            //
            val existingDates = scheduledGoalDao.getExistingRecurringDates(
                recurrenceRuleId = rule.recurrenceRuleId,
                startDate = rangeStart,
                endDate = rangeEnd
            ).toSet()

            while(!date.isAfter(rangeEnd)){
                if (date.dayOfWeek in rule.recurringDays && date !in exceptionDates && date !in existingDates){
                    val eventId = calendarEventsRepository.getOrCreateEventIdForDate(date)

                    scheduledGoalDao.insert(
                        ScheduledGoal(
                            goalId = goal.goalID,
                            eventId = eventId,
                            recurrenceRuleId = rule.recurrenceRuleId,
                            scheduledGoalTitle = goal.goalTitle,
                            scheduledHours = goal.hours,
                            scheduledMinutes = goal.minutes
                        )
                    )
                }
                date = date.plusDays(1)
            }
        }
    }

    override suspend fun insertRecurrenceException(recurrenceRuleId: Int, date: LocalDate) {
        recurrenceRuleDao.insertRecurrenceException(
            RecurrenceException(
                recurrenceRuleId = recurrenceRuleId,
                date = date
            )
        )
    }
}