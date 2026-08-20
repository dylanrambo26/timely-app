package com.example.timemanagementapp.ui.components.time

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.util.calculateRemainingTime
import kotlinx.coroutines.delay

@Composable
fun RemainingTaskTime(scheduledGoal: ScheduledGoal){
    var remainingMinutes by remember {
        mutableIntStateOf(0)
    }

    var isDone by remember { mutableStateOf(false) }

    val isGoalComplete = scheduledGoal.status == GoalStatus.COMPLETED

    //Need keyed LaunchedEffect when a new goal is selected for current task to recompose with new coroutine
    //also recompose when startTime and completedMillis are changed during pause
    LaunchedEffect(
        scheduledGoal.scheduledGoalId,
        scheduledGoal.startTimeMillis,
        scheduledGoal.completedMillis,
        scheduledGoal.status
    ) {

        if(isGoalComplete){
            return@LaunchedEffect
        }
        Log.d("Remaining Task Time: ", "Relaunch")
        while(true){
            val remainingTimeState = calculateRemainingTime(
                startTimeMillis = scheduledGoal.startTimeMillis,
                hours = scheduledGoal.scheduledHours,
                minutes = scheduledGoal.scheduledMinutes,
                currentTimeMillis = System.currentTimeMillis(),
                completedMillis = scheduledGoal.completedMillis
            )
            remainingMinutes = remainingTimeState.remainingMinutes
            isDone = remainingTimeState.isDone

            delay(1000L)
        }
    }

    DisplayTimer(
        duration = remainingMinutes,
        isDone = isDone,
        title = stringResource(R.string.current_task_time_remaining),
        isComplete = isGoalComplete
    )
}