package com.example.timemanagementapp.data

import com.example.timemanagementapp.data.calendar.CalendarEventsRepository
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.goal.recurrence.RecurrenceRule
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import java.time.DayOfWeek
import java.time.LocalDate

class CreateRecurrenceUseCase(
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
    private val calendarEventsRepository: CalendarEventsRepository
) {

    suspend operator fun invoke(
        recurringDays: Set<DayOfWeek>,
        startDate: LocalDate,
        endDate: LocalDate?,
        goal: Goal,
    ){
        //val startDate = calculateRecurrenceStartDate(recurringDays)

        val newRecurrenceRule = RecurrenceRule(
            goalId = goal.goalID,
            recurringDays = recurringDays,
            startDate = startDate,
            endDate = endDate
        )
        val recurrenceRuleId = goalsRepository.insertRecurrenceRule(newRecurrenceRule)
        val insertedRecurrenceRule = newRecurrenceRule.copy(
            recurrenceRuleId = recurrenceRuleId.toInt()
        )
        scheduleRecurringGoals(
            recurrenceRule = insertedRecurrenceRule,
            goal = goal
        )
    }

    private suspend fun scheduleRecurringGoals(
        recurrenceRule: RecurrenceRule,
        goal: Goal
    ){
        val lazyGenerationEnd = LocalDate.now().plusMonths(3)
        val schedulingEndDate = recurrenceRule.endDate?.coerceAtMost(lazyGenerationEnd) ?: lazyGenerationEnd

        if (recurrenceRule.startDate.isAfter(schedulingEndDate)){
            return
        }

        //end date is inclusive
        val dates = recurrenceRule.startDate.datesUntil(schedulingEndDate.plusDays(1))

        for (date in dates){
            if (date.dayOfWeek in recurrenceRule.recurringDays){
                val eventId = calendarEventsRepository.getOrCreateEventIdForDate(date)

                val scheduledGoal = ScheduledGoal(
                    goalId = recurrenceRule.goalId,
                    eventId = eventId,
                    scheduledGoalTitle = goal.goalTitle,
                    scheduledHours = goal.hours,
                    scheduledMinutes = goal.minutes,
                    recurrenceRuleId = recurrenceRule.recurrenceRuleId
                )
                scheduledGoalsRepository.insertScheduledGoal(scheduledGoal)
            }
        }
    }
}