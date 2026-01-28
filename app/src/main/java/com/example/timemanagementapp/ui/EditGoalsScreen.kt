package com.example.timemanagementapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyScreen
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.TestData
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

/*Todo: Pass the information of existing goal for onEditGoal to the editonegoalscreen to auto-populate the fields, user can then edit as they like and submit, resulting
Gamestate will reflect it.
*/

@Composable
fun EditGoalsScreen(
    currentGoals: List<Goal>,
    onAddGoalButtonClicked: () -> Unit = {},
    onDeleteGoal: (Goal) -> Unit,
    onEditGoal: (Goal) -> Unit,
    remaining: Int
){

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoalList(
            goals = currentGoals,
            onDeleteGoal = onDeleteGoal,
            onEditGoal = onEditGoal,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium_large)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),

        )
        TimeRemaining(remaining = remaining)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            IconButton(
                onClick = onAddGoalButtonClicked,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(100.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_goal),
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = stringResource(R.string.top_app_bar_add_goal),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun EditGoalsScreenPreview(){
    TimeManagementAppTheme {
        EditGoalsScreen(
            currentGoals = TestData.goals,
            onDeleteGoal = {},
            onEditGoal = {},
            onAddGoalButtonClicked = {},
            remaining = 870
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditGoalsEmptyListScreenPreview(){
    val emptyGoals = emptyList<Goal>()
    TimeManagementAppTheme {
        EditGoalsScreen(
            currentGoals = emptyGoals,
            onDeleteGoal = {},
            onEditGoal = {},
            onAddGoalButtonClicked = {},
            remaining = 1440
        )
    }
}