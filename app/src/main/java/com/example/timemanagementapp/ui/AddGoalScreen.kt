package com.example.timemanagementapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.TestData
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
fun AddGoalScreen(
    onUserHourChanged: (String) -> Unit,
    onUserMinutesChanged: (String) -> Unit,
    onUserGoalTitleChanged: (String) -> Unit,
    onClearButtonPressed: () -> Unit,
    onAddGoalButtonPressed: (String, String, String) -> Boolean,
    onReturnToHomeButtonPressed: () -> Unit,
    userHours: String,
    userMinutes: String,
    userGoalTitle: String,

    currentGoals: List<Goal>,
    remaining: Int,

    modifier: Modifier = Modifier
){
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GoalList(
            goals = currentGoals,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        TimeRemaining(remaining = remaining)
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
                onClick = {
                    val success = onAddGoalButtonPressed(userGoalTitle, userHours, userMinutes)
                    errorMessage = if(!success){
                        "Day already filled with goals. Delete or edit goals to free up time."
                    }else{
                        null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.add_goal),
                    fontSize = 16.sp,
                    color = Color.White
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
        Spacer(modifier = Modifier.padding(24.dp))
        Button(
            onClick = onReturnToHomeButtonPressed,
        ){
            Image(
                painter = painterResource(R.drawable.return_icon),
                contentDescription = "Return to Home"
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Return To Home"
            )
        }
    }
}

//Preview the AddLogScreen
@Preview(showBackground = true)
@Composable
fun AddGoalScreenPreview(){
    TimeManagementAppTheme {
        AddGoalScreen(
            onUserHourChanged = {},
            onUserMinutesChanged = {},
            onUserGoalTitleChanged = {},
            onClearButtonPressed = {},
            onAddGoalButtonPressed = {_,_,_ ->false}, //Dummy lambda for preview
            userHours = "",
            userMinutes = "",
            userGoalTitle = "",
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
            onReturnToHomeButtonPressed = {},
            currentGoals = TestData.goals,
            remaining = 870
        )
    }
}