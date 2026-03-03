package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal

@Composable
fun GoalList(
    modifier: Modifier = Modifier,
    goals: List<Goal>,
    onDeleteGoal: ((Goal) -> Unit)? = null,
    onEditGoal: ((Goal) -> Unit)? = null,
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
            items(goals) { goal ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = goal.goalID.toString()) //TODO delete later
                        Text(text = goal.goalTitle)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "${goal.hours}h ${goal.minutes}m")
                    }
                    if (onDeleteGoal != null || onEditGoal != null){
                        Row {
                            if (onDeleteGoal != null) {
                                IconButton(onClick = {onDeleteGoal(goal)})
                                {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                            if (onEditGoal != null){
                                IconButton(onClick = {onEditGoal(goal) })
                                {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                        }
                    }
                }
            }
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