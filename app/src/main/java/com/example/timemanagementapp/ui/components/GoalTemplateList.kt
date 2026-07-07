package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.goal.Goal
import com.example.timemanagementapp.data.testGoalsSizeThree
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

@Composable
fun GoalTemplateList(
    goals: List<Goal>,
    onGoalClick: (Goal) -> Unit,

    modifier: Modifier = Modifier,
    selectedGoalId: Int? = null,
){
    val listState = rememberLazyListState()
    val previousSize = rememberPreviousLazyColumn(goals.size)

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            goals
        ) { goal ->
            val isSelected = goal.goalID == selectedGoalId
            GoalTemplateCard(
                goal = goal,
                isSelected = isSelected,
                onGoalClick = onGoalClick,
            )
        }
    }
}

@Composable
fun GoalTemplateCard(
    goal: Goal,
    isSelected: Boolean = false,
    onGoalClick: ((Goal) -> Unit)? = null,
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if(onGoalClick != null){
                    Modifier.clickable { onGoalClick(goal)}
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = if(isSelected){
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Text(text = goal.goalID.toString()) //TODO delete later
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = goal.goalTitle)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "${goal.hours}h ${goal.minutes}m")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalTemplateListPreview(){
    TimeManagementAppTheme {
        GoalTemplateList(
            goals = testGoalsSizeThree,
            onGoalClick = {}
        )
    }
}