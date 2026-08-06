package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.goal.GoalStatus
import com.example.timemanagementapp.data.scheduledgoal.ScheduledGoal
import com.example.timemanagementapp.data.testScheduledGoalsSizeThree
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.checkbox
import com.example.timemanagementapp.ui.theme.completedGoal

@Composable
fun ScheduledGoalList(
    modifier: Modifier = Modifier,
    goals: List<ScheduledGoal>,
    selectedGoalId: Int? = null,
    onDeleteGoal: ((ScheduledGoal) -> Unit)? = null,
    onEditGoal: ((ScheduledGoal) -> Unit)? = null,
    onGoalClick: ((ScheduledGoal) -> Unit)? = null,
    addColors: Boolean = false,
    addCheckboxes: Boolean = false,
    onCompleteChange: ((ScheduledGoal, Boolean) -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val previousSize = rememberPreviousLazyColumn(goals.size)

    //Only scroll to recently added goal, do not scroll when deleting
    LaunchedEffect(goals.size) {
        if (previousSize != null && goals.size > previousSize){
            listState.animateScrollToItem(goals.lastIndex)
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (goals.isEmpty()){
            item{
                Text(
                    text = "No current goals.",
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
        }
        else{
            items(
                goals,
                key = {it.scheduledGoalId}
            ) { scheduledGoal ->
                val isSelected = scheduledGoal.scheduledGoalId == selectedGoalId
                GoalCard(
                    scheduledGoal = scheduledGoal,
                    isSelected = isSelected,
                    onDeleteGoal = onDeleteGoal,
                    onEditGoal = onEditGoal,
                    onGoalClick = onGoalClick,
                    addColors = addColors,
                    addCheckboxes = addCheckboxes && scheduledGoal.status != GoalStatus.RUNNING,
                    onCompleteChange = onCompleteChange
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    scheduledGoal: ScheduledGoal,
    isSelected: Boolean = false,
    addColors: Boolean = false,
    addCheckboxes: Boolean = false,
    onDeleteGoal: ((ScheduledGoal) -> Unit)? = null,
    onEditGoal: ((ScheduledGoal) -> Unit)? = null,
    onGoalClick: ((ScheduledGoal) -> Unit)? = null,
    onCompleteChange: ((ScheduledGoal, Boolean) -> Unit)? = null
){
    val goalStatus = scheduledGoal.status
    val scheduledDurationMillis = (scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60000L
    val completedDuration = scheduledGoal.completedMillis >= scheduledDurationMillis

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if(onGoalClick != null){
                    Modifier.clickable { onGoalClick(scheduledGoal)}
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = if(isSelected){
            MaterialTheme.colorScheme.primaryContainer
        } else if(addColors && goalStatus == GoalStatus.COMPLETED){
            MaterialTheme.colorScheme.completedGoal
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = scheduledGoal.scheduledGoalTitle,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Goal: ${scheduledGoal.scheduledHours}h ${scheduledGoal.scheduledMinutes}m",
                    style = MaterialTheme.typography.bodyMedium
                )

                GoalStatusText(scheduledGoal = scheduledGoal)

            }
            if (onDeleteGoal != null || onEditGoal != null){
                Row {
                    if (onDeleteGoal != null) {
                        IconButton(onClick = {onDeleteGoal(scheduledGoal)})
                        {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    if (onEditGoal != null){
                        IconButton(onClick = {onEditGoal(scheduledGoal) })
                        {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            }

            if (addCheckboxes){

                Checkbox(
                    checked = goalStatus == GoalStatus.COMPLETED,
                    enabled = !completedDuration,
                    onCheckedChange = {isChecked ->
                        onCompleteChange?.invoke(scheduledGoal, isChecked)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.checkbox
                    )
                )
            }
        }
    }
}

@Composable
fun GoalStatusText(
    scheduledGoal: ScheduledGoal
){
    when (scheduledGoal.status){
        GoalStatus.COMPLETED -> {
            val totalMinutes = scheduledGoal.completedMillis / 60_000L
            val completedHours = totalMinutes / 60
            val completedMinutes = totalMinutes % 60

            Text(
                text = "Completed: ${completedHours}h ${completedMinutes}m",
                style = MaterialTheme.typography.bodySmall
            )
        }

        GoalStatus.NOT_STARTED ->{
            Text(
                text = "NOT STARTED",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic
            )
        }

        GoalStatus.PAUSED ->{
            Text(
                text = "PAUSED",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic
            )
        }

        GoalStatus.RUNNING ->{
            Text(
                text = "RUNNING",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

//Helper function to remember size of the goal list before addition or deletion
@Composable
fun rememberPreviousLazyColumn(value: Int): Int? {
    val previous = remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(value) {
        previous.value = value
    }
    return previous.value
}

@Preview(showBackground = true)
@Composable
fun ScheduledGoalListPreview(){
    TimeManagementAppTheme {
        val previewGoals = testScheduledGoalsSizeThree.toMutableList().apply {
            this[0] = this[0].copy(
                scheduledHours = 0,
                scheduledMinutes = 1,
                completedMillis = 60_000L,
                status = GoalStatus.COMPLETED
            )
        }
        ScheduledGoalList(
            goals = previewGoals,
            addColors = true,
            addCheckboxes = true,
            /*onDeleteGoal = {},
            onEditGoal = {}*/
            )
    }
}