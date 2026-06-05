package com.example.timemanagementapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.util.calculateRemainingTime
import com.example.timemanagementapp.util.millisUntilNextMinute
import kotlinx.coroutines.delay

@Composable
fun RemainingTaskTime(goal: Goal){
    var remainingMinutes by remember {
        mutableIntStateOf(0)
    }

    var isDone by remember { mutableStateOf(false) }

    //Need keyed LaunchedEffect when a new goal is selected for current task to recompose with new coroutine
    LaunchedEffect(goal.goalID) {
        while(true){
            val remainingTimeState = calculateRemainingTime(
                startTimeMillis = goal.startTimeMillis,
                hours = goal.hours,
                minutes = goal.minutes,
                currentTimeMillis = System.currentTimeMillis()
            )
            remainingMinutes = remainingTimeState.remainingMinutes
            isDone = remainingTimeState.isDone

            delay(millisUntilNextMinute())
        }
    }

    DisplayTimer(
        duration = remainingMinutes,
        isDone = isDone,
        title = stringResource(R.string.current_task_time_remaining)
    )
}