package com.example.timemanagementapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.util.getTimeRemainingInDay
import com.example.timemanagementapp.util.millisUntilNextMinute
import kotlinx.coroutines.delay

@Composable
fun RemainingTaskTime(goal: Goal){
    var remainingMinutes by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        while(true){
            val totalMillis = (goal.hours * 60L + goal.minutes) * 60_000L

            val remainingMillis =
                if (goal.startTimeMillis != 0L) {
                    val endTime = goal.startTimeMillis + totalMillis
                    (endTime - System.currentTimeMillis()).coerceAtLeast(0)
                } else {
                    totalMillis
                }

            remainingMinutes = (remainingMillis / 60_000).toInt()
            delay(millisUntilNextMinute())
        }
    }

    DisplayTime(
        duration = remainingMinutes,
        title = stringResource(R.string.current_task_time_remaining)
    )
}