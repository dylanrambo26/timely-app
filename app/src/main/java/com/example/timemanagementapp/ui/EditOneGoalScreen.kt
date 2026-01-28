package com.example.timemanagementapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.TimeDuration
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

@Composable
fun EditOneGoalScreen(
    goalMinutes: String,
    goalHours: String,
    goalTitle: String,
    onGoalHourChanged: (String) -> Unit,
    onGoalMinutesChanged: (String) -> Unit,
    onGoalTitleChanged: (String) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            //Hours Text Field
            OutlinedTextField(
                value = goalHours,
                onValueChange = onGoalHourChanged,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(),
                label = {
                    Text("Hours")
                }
            )
            // Minutes Text Field
            OutlinedTextField(
                value = goalMinutes,
                onValueChange = onGoalMinutesChanged,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(),
                label = {
                    Text("Minutes")
                }
            )
        }

        //Goal Title Text Field
        OutlinedTextField(
            value = goalTitle,
            onValueChange = onGoalTitleChanged,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            colors = OutlinedTextFieldDefaults.colors(),
            label = {
                Text("Goal Title")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditOneGoalScreenPreview(){
    TimeManagementAppTheme {
        EditOneGoalScreen(
            goalMinutes = "30",
            goalHours = "1",
            goalTitle = "GoalTest",
            onGoalHourChanged = {},
            onGoalMinutesChanged = {},
            onGoalTitleChanged = {}
        )
    }
}