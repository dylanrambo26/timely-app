package com.timelyproductivity.app.ui.components.time

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.timelyproductivity.app.R
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

@Composable
fun DisplayTimer(
    duration: Int,
    isDone: Boolean,
    title: String,
    isComplete: Boolean,
    paddingValue: Dp = dimensionResource(R.dimen.padding_medium)
){
    val durationText = when{
        isComplete -> "Task Completed"
        isDone -> "Timer Over"
        duration == 0 -> "Less than 1 minute remaining"
        else -> "${duration / 60}h ${duration % 60}m $title"
    }

    Text(
        text = durationText,
        modifier = Modifier
            .padding(paddingValue)
    )
}

@Preview(showBackground = true)
@Composable
fun DisplayTimerPreview(){
    TimeManagementAppTheme {
        DisplayTimer(
            duration = 0,
            isDone = false,
            title = "remaining",
            isComplete = false
        )
    }
}