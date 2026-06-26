package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

@Composable
fun DisplayTimer(duration: Int, isDone: Boolean, title: String){
    val durationText = when{
        isDone -> "$title Timer Over"
        duration == 0 -> "$title Less than 1 minute"
        else -> "$title ${duration / 60}h ${duration % 60}m"
    }

    Text(
        text = durationText,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
    )
}

@Preview(showBackground = true)
@Composable
fun DisplayTimerPreview(){
    TimeManagementAppTheme {
        DisplayTimer(
            duration = 3,
            isDone = false,
            title = "Task: "
        )
    }
}