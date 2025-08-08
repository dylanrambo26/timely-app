package com.example.timemanagementapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.TestData
import com.example.timemanagementapp.data.TimeDuration
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

/**
 * @param onUserHourChanged - preserves the user's entry into the hour text field
 * @param onUserMinutesChanged - preserves the user's entry into the minute text field
 * @param onUserGoalTitleChanged - preserves the user's entry into the goal title text field
 * @param onClearButtonPressed - clears the contents in the text fields
 * @param onAddGoalButtonPressed - Adds the Goal to the uiState list of goals
 * @param userHours - User value typed in text field
 * @param userMinutes - User value typed in text field
 * @param userGoalTitle - User value typed in text field
 *
 * Screen for adding a goal to the list
 */
@Composable
fun AddLogScreen(
    onUserHourChanged: (String) -> Unit,
    onUserMinutesChanged: (String) -> Unit,
    onUserGoalTitleChanged: (String) -> Unit,
    onClearButtonPressed: () -> Unit,
    onAddGoalButtonPressed: (String, String, String) -> Unit,
    userHours: String,
    userMinutes: String,
    userGoalTitle: String,

    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            //Hours Text Field
            OutlinedTextField(
                value = userHours,
                onValueChange = onUserHourChanged,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
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
                value = userMinutes,
                onValueChange = onUserMinutesChanged,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
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
            value = userGoalTitle,
            onValueChange = onUserGoalTitleChanged,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
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
            //Clear Fields Button
            OutlinedButton(
                onClick = onClearButtonPressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    //.background(color = Color.White)
            ) {
                Text(
                    text = stringResource(R.string.clear_fields),
                    fontSize = 16.sp,
                )
            }

            //Add Goal Button
            OutlinedButton(
                onClick = { onAddGoalButtonPressed(userGoalTitle, userHours, userMinutes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.add_goal),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

//Preview the AddLogScreen
@Preview
@Composable
fun AddLogScreenPreview(){
    TimeManagementAppTheme {
        AddLogScreen(
            onUserHourChanged = {},
            onUserMinutesChanged = {},
            onUserGoalTitleChanged = {},
            onClearButtonPressed = {},
            onAddGoalButtonPressed = {_,_,_ ->}, //Dummy lambda for preview
            userHours = "",
            userMinutes = "",
            userGoalTitle = "",
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}