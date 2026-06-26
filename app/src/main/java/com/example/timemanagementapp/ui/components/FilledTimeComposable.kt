package com.example.timemanagementapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.timemanagementapp.R
import com.example.timemanagementapp.util.getTimeRemainingInDay
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.timemanagementapp.util.millisUntilNextMinute
import java.time.LocalTime


@Composable
fun FilledTime(remaining: Int){
    var filledTime by remember {
        mutableIntStateOf(
            minOf(
                (60*24) - remaining,
                getTimeRemainingInDay()
            )
        )
    }

    LaunchedEffect(remaining) {
        while(true){
            filledTime = minOf(
                (60*24) - remaining,
                getTimeRemainingInDay()
            )
            delay(millisUntilNextMinute())
        }
    }

    DisplayTime(
        duration = filledTime,
        title = stringResource(R.string.time_filled_of_remaining_time_in_day) + "\n"
    )
}