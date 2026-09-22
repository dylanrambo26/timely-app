package com.timelyproductivity.app.data.goal.recurrence

import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.GoalsRepository
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoalsRepository
import java.time.DayOfWeek
import java.time.LocalDate

class CreateRecurrenceUseCase(
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository,
) {

    suspend operator fun invoke(
        recurringDays: Set<DayOfWeek>,
        startDate: LocalDate,
        endDate: LocalDate?,
        goal: Goal,
    ){
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

        val today = LocalDate.now()
        val generationStart = maxOf(today, startDate)
        val generationEnd = today.plusMonths(3)

        scheduledGoalsRepository.scheduleRuleForRange(
            rule = insertedRecurrenceRule,
            startDate = generationStart,
            endDate = generationEnd
        )
    }
}