package com.example.timemanagementapp.data.scheduledgoal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.timemanagementapp.data.goal.GoalStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ScheduledGoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledGoal: ScheduledGoal)

    @Update
    suspend fun update(scheduledGoal: ScheduledGoal)

    @Delete
    suspend fun delete(scheduledGoal: ScheduledGoal)


    @Query("SELECT * from scheduled_goals WHERE scheduledGoalId = :id")
    suspend fun getScheduledGoalOnce(id: Int): ScheduledGoal

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE eventId = :eventId
        """
    )
    fun getScheduledGoals(eventId: Int): Flow<List<ScheduledGoal>>

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE scheduledGoalId = :id
        """
    )
    fun getScheduledGoal(id: Int): Flow<ScheduledGoal>

    @Transaction
    @Query(
        """
            SELECT * FROM scheduled_goals
            WHERE eventId = :eventId
        """
    )
    suspend fun getScheduledGoalsOnce(eventId: Int): List<ScheduledGoal>

    //Used to update all scheduled goal durations that reference a reusable goal that was edited
    @Query(
        """
            UPDATE scheduled_goals
            SET scheduledGoalTitle = :title,
                scheduledHours = :hours,
                scheduledMinutes = :minutes
            WHERE goalId = :goalId
                AND isCustomized = 0
                AND status = :notStartedStatus
                AND eventId IN (
                    SELECT eventId
                    FROM calendar_events
                    WHERE date >= :startDate
                )
        """
    )
    suspend fun updateFutureScheduledGoalsDurations(
        goalId: Int,
        title: String,
        hours: Int,
        minutes: Int,
        startDate: LocalDate,
        notStartedStatus: GoalStatus
    )

    @Query(
        """
            UPDATE scheduled_goals
            SET scheduledGoalTitle = :title
            WHERE goalId = :goalId
                AND isCustomized = 0
                AND eventId IN (
                    SELECT eventId
                    FROM calendar_events
                    WHERE date >= :startDate
                )
        """
    )
    suspend fun updateFutureScheduledGoalTitles(
        goalId: Int,
        title: String,
        startDate: LocalDate
    )

    //Only update the durations and titles of scheduled goals that are not the current task, if it is a current task only update title
    @Transaction
    suspend fun updateFutureScheduledGoalsFromEditedTemplate(
        goalId: Int,
        title: String,
        hours: Int,
        minutes: Int,
        startDate: LocalDate
    ){
        updateFutureScheduledGoalTitles(
            goalId = goalId,
            title = title,
            startDate = startDate
        )

        updateFutureScheduledGoalsDurations(
            goalId = goalId,
            title = title,
            hours = hours,
            minutes = minutes,
            startDate = startDate,
            notStartedStatus = GoalStatus.NOT_STARTED
        )
    }
}