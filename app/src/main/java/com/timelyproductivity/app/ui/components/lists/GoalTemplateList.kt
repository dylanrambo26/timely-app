package com.timelyproductivity.app.ui.components.lists

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.data.goal.Goal
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.recurrence.GoalWithRecurrence
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testGoalsWithCategoriesSizeThree
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.toColor
import com.timelyproductivity.app.util.toShortLabel
import java.time.DayOfWeek

@Composable
fun GoalTemplateList(
    goals: List<GoalWithRecurrence>,
    categories: List<GoalCategory> = emptyList(),
    addCategoryColors: Boolean = false,
    onGoalClick: ((Goal) -> Unit)? = null,
    onEditGoal: ((Goal) -> Unit)? = null,
    onDeleteGoal: ((GoalWithRecurrence) -> Unit)? = null,
    modifier: Modifier = Modifier,
    selectedGoalId: Int? = null,
){
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
        items(
            goals
        ) { goalWithRecurrence ->
            val isSelected = goalWithRecurrence.goal.goalID == selectedGoalId
            GoalTemplateCard(
                goal = goalWithRecurrence.goal,
                categories = categories,
                addCategoryColors = addCategoryColors,
                recurringDays = goalWithRecurrence.recurrenceRule?.recurringDays,
                isSelected = isSelected,
                onGoalClick = onGoalClick,
                onEditGoal = onEditGoal,
                onDeleteGoal = onDeleteGoal?.let { deleteGoal ->
                    { deleteGoal(goalWithRecurrence) }
                }
            )
        }
    }
}

@Composable
fun GoalTemplateCard(
    goal: Goal,
    categories: List<GoalCategory> = emptyList(),
    recurringDays: Set<DayOfWeek>? = null,
    isSelected: Boolean = false,
    addCategoryColors: Boolean = false,
    onDeleteGoal: ((Goal) -> Unit)? = null,
    onEditGoal: ((Goal) -> Unit)? = null,
    onGoalClick: ((Goal) -> Unit)? = null,
){
    val category = categories.firstOrNull{
        it.categoryId == goal.categoryId
    }

    val cardCategoryColor = category?.color?.toColor() ?: MaterialTheme.colorScheme.secondaryContainer

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if (onGoalClick != null) {
                    Modifier.clickable { onGoalClick(goal) }
                } else {
                    Modifier
                }
            )
            .border(
                width = if(isSelected && addCategoryColors) 2.dp else 0.dp,
                color = if(isSelected){
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = if(isSelected && !addCategoryColors){
            MaterialTheme.colorScheme.primaryContainer
        } else if(isSelected){
            cardCategoryColor
        } else if(addCategoryColors){
            cardCategoryColor
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = goal.goalTitle,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Duration: ${goal.hours}h ${goal.minutes}m",
                    style = MaterialTheme.typography.bodyMedium
                )

                val categoryName = category?.name ?: "None"
                Text(
                    text = "Category: $categoryName",
                    style = MaterialTheme.typography.bodySmall
                )

                val isRecurring = recurringDays != null

                if(isRecurring){
                    Text(
                        text = "Recurring: " + recurringDays
                            .sortedBy { it.value % 7}
                            .joinToString(", ") {day ->
                                day.toShortLabel()
                            },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                else{
                    Text(
                        text = "",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

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

@Preview(showBackground = true)
@Composable
fun GoalTemplateListPreview(){
    TimeManagementAppTheme {
        GoalTemplateList(
            goals = testGoalsWithCategoriesSizeThree,
            onGoalClick = {},
            onDeleteGoal = {},
            onEditGoal = {},
            addCategoryColors = false,
            selectedGoalId = 1,
            categories = testGoalCategoriesSizeThreeWithColor
        )
    }
}