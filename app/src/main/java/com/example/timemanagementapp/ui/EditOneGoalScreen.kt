package com.example.timemanagementapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.TimeDuration
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

@Composable
fun EditOneGoalScreen(
    goalID: Long,
    goalMinutes: String,
    goalHours: String,
    goalTitle: String,
    onGoalHourChanged: (String) -> Unit,
    onGoalMinutesChanged: (String) -> Unit,
    onGoalTitleChanged: (String) -> Unit,
    editGoal: () -> Boolean,
    onSaveButtonPressed: () -> Unit,
    onCancelButtonPressed: () -> Unit,
    remaining: Int
){
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center
    ) {
        TimeRemaining(remaining = remaining)
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

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            //Cancel Edit Button
            OutlinedButton(
                onClick = onCancelButtonPressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel_edit_one_goal),
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }

            //Save Goal Button
            OutlinedButton(
                onClick = {
                    val success = editGoal()
                    errorMessage = if(!success){
                        "Day already filled with goals. Delete or edit goals to free up time."
                    }else{
                        null
                    }
                    if(success){
                        onSaveButtonPressed()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.save_edit_one_goal),
                    fontSize = 16.sp,
                    color = Color.Green
                )
            }
        }

        if (errorMessage != null){
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onError,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditOneGoalScreenPreview(){
    TimeManagementAppTheme {
        EditOneGoalScreen(
            goalID = 1,
            goalMinutes = "30",
            goalHours = "1",
            goalTitle = "GoalTest",
            onGoalHourChanged = {},
            onGoalMinutesChanged = {},
            onGoalTitleChanged = {},
            editGoal = { -> false},
            onSaveButtonPressed = {},
            onCancelButtonPressed = {},
            remaining = 870
        )
    }
}