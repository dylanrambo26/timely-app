package com.example.timemanagementapp.data.goal.recurrence

import com.example.timemanagementapp.data.goal.GoalsRepository
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoalsRepository
import java.time.DayOfWeek
import java.time.LocalDate

class UpdateRecurrenceUseCase(
    private val goalsRepository: GoalsRepository,
    private val scheduledGoalsRepository: ScheduledGoalsRepository
) {

    suspend operator fun invoke(
        originalRule: RecurrenceRule,
        recurringDays: Set<DayOfWeek>,
        startDate: LocalDate,
        endDate: LocalDate?,
        deleteOldScheduledGoals: Boolean
    ){
        //Update the recurrence rule values
        val updatedRule = originalRule.copy(
            recurringDays = recurringDays,
            startDate = startDate,
            endDate = endDate
        )

        goalsRepository.updateRecurrenceRule(updatedRule)

        val today = LocalDate.now()

        //Used to prevent generation in the past, ex. if the start date was in the past, today will be used for start date
        val generationStart = maxOf(
            today,
            updatedRule.startDate
        )

        //Only generate 3 months from today, this is the lazy generation window
        val generationEnd = updatedRule.endDate?.coerceAtMost(today.plusMonths(3)) ?: today.plusMonths(3)

        //Delete the scheduled goals that held the old recurrence rule values if specified
        if (deleteOldScheduledGoals) {
            scheduledGoalsRepository.deleteFutureIncompleteRecurringGoalsByRecurrenceId(
                recurrenceRuleId = updatedRule.recurrenceRuleId,
                startDate = today
            )
        }

        //Schedule the goals based on the updated recurrence rule as long as the generation start is after generation end.
        if(!generationStart.isAfter(generationEnd)) {
            scheduledGoalsRepository.scheduleRuleForRange(
                rule = updatedRule,
                startDate = generationStart,
                endDate = generationEnd
            )
        }
    }
}