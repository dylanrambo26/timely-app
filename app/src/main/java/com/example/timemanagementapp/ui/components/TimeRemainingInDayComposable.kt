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
fun TimeRemainingInDay(){
    var minutesLeft by remember {
        mutableIntStateOf(getTimeRemainingInDay())
    }

    LaunchedEffect(Unit) {
        while(true){
            minutesLeft = getTimeRemainingInDay()
            delay(millisUntilNextMinute())
        }
    }

    DisplayTime(
        duration = minutesLeft,
        title = stringResource(R.string.time_remaining_in_day)
    )
}