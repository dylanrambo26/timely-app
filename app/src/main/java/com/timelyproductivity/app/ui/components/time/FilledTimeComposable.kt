package com.timelyproductivity.app.ui.components.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.timelyproductivity.app.R
import com.timelyproductivity.app.util.getTimeRemainingInDay
import com.timelyproductivity.app.util.millisUntilNextMinute
import kotlinx.coroutines.delay


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