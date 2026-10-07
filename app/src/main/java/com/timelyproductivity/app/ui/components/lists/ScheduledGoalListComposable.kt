package com.timelyproductivity.app.ui.components.lists

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testScheduledGoalsSizeThree
import com.timelyproductivity.app.data.testScheduledGoalsWithCategoriesSizeThree
import com.timelyproductivity.app.ui.components.settings.ReminderTimes
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.checkbox
import com.timelyproductivity.app.ui.theme.completedGoal
import com.timelyproductivity.app.ui.theme.toColor
import com.timelyproductivity.app.util.MAX_NUMBER_OF_COUNTDOWN_REMINDERS
import com.timelyproductivity.app.util.incompleteGoals

@Composable
fun ScheduledGoalList(
    modifier: Modifier = Modifier,
    goals: List<ScheduledGoal>,
    categories: List<GoalCategory> = emptyList(),
    selectedGoalId: Int? = null,
    onDeleteGoal: ((ScheduledGoal) -> Unit)? = null,
    onEditGoal: ((ScheduledGoal) -> Unit)? = null,
    onGoalClick: ((ScheduledGoal) -> Unit)? = null,
    addCompletionColor: Boolean = false,
    addCategoryColors: Boolean = false,
    addCheckboxes: Boolean = false,
    onCompleteChange: ((ScheduledGoal, Boolean) -> Unit)? = null,
    showCountdownReminders: Boolean = false,

    countdownReminders: Set<Int> = emptySet(),
    onCustomizeReminders: (() -> Unit)? = null,
    onFinishCustomizeReminders: (() -> Unit)? = null,
    onAddReminder: (() -> Unit)? = null,
    onDeleteReminder: ((Int) -> Unit)? = null,
    onEditReminder: ((Int) -> Unit)? = null,
    isCustomizingReminders: Boolean = false,

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
                    categories = categories,
                    isSelected = isSelected,
                    onDeleteGoal = onDeleteGoal,
                    onEditGoal = onEditGoal,
                    onGoalClick = onGoalClick,
                    addCompletionColor = addCompletionColor,
                    addCategoryColors = addCategoryColors,
                    addCheckboxes = addCheckboxes && scheduledGoal.status != GoalStatus.RUNNING,
                    onCompleteChange = onCompleteChange,
                    showCountdownReminders = showCountdownReminders,
                    countdownReminders = countdownReminders,
                    onCustomizeReminders = onCustomizeReminders,
                    onFinishCustomizeReminders = onFinishCustomizeReminders,

                    onAddReminder = onAddReminder,
                    onDeleteReminder = onDeleteReminder,
                    onEditReminder = onEditReminder,
                    isCustomizingReminders = isCustomizingReminders,
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    scheduledGoal: ScheduledGoal,
    categories: List<GoalCategory> = emptyList(),

    isSelected: Boolean = false,
    addCompletionColor: Boolean = false,
    addCategoryColors: Boolean = false,
    addCheckboxes: Boolean = false,
    onDeleteGoal: ((ScheduledGoal) -> Unit)? = null,
    onEditGoal: ((ScheduledGoal) -> Unit)? = null,
    onGoalClick: ((ScheduledGoal) -> Unit)? = null,
    onCompleteChange: ((ScheduledGoal, Boolean) -> Unit)? = null,
    showCountdownReminders: Boolean = false,

    countdownReminders: Set<Int> = emptySet(),
    onAddReminder: (() -> Unit)? = null,
    onDeleteReminder: ((Int) -> Unit)? = null,
    onEditReminder: ((Int) -> Unit)? = null,
    isCustomizingReminders: Boolean = false,

    onCustomizeReminders: (() -> Unit)? = null,
    onFinishCustomizeReminders: (() -> Unit)? = null
){
    val goalStatus = scheduledGoal.status
    val scheduledDurationMillis = (scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * 60000L
    val completedDuration = scheduledGoal.completedMillis >= scheduledDurationMillis

    val category = categories.firstOrNull{
        it.categoryId == scheduledGoal.scheduledCategoryId
    }

    val cardCategoryColor = category?.color?.toColor() ?: MaterialTheme.colorScheme.secondaryContainer

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if(onGoalClick != null){
                    Modifier.clickable {
                        onGoalClick(scheduledGoal)
                    }
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
        color = if(isSelected && addCategoryColors){
            cardCategoryColor
        } else if(isSelected){
            MaterialTheme.colorScheme.primaryContainer
        }
        else if(addCompletionColor && goalStatus == GoalStatus.COMPLETED){
            MaterialTheme.colorScheme.completedGoal
        } else if (addCategoryColors) {
            cardCategoryColor
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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

                    val categoryName = category?.name ?: "None"
                    Text(
                        text = "Category: $categoryName",
                        style = MaterialTheme.typography.bodySmall
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
            AnimatedVisibility(
                visible = isSelected && showCountdownReminders
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(top= 8.dp)
                    )

                    if(isCustomizingReminders &&
                        onAddReminder != null &&
                        onEditReminder != null &&
                        onDeleteReminder != null
                    ){
                        ReminderTimes(
                            selectedMinutes = countdownReminders,
                            onEditReminder = onEditReminder,
                            onAddReminder = onAddReminder,
                            onDeleteReminder = onDeleteReminder,
                            maxSizeReached = countdownReminders.size >= MAX_NUMBER_OF_COUNTDOWN_REMINDERS
                        )

                        TextButton(
                            onClick = {onFinishCustomizeReminders?.invoke()}
                        ) {
                            Text(
                                "Finish"
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text =
                                    "Reminders: ${
                                        if(countdownReminders.isNotEmpty()){
                                            countdownReminders.joinToString(", ") { "$it min" }
                                        } else {
                                            "None"
                                        }
                                    }",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )

                            TextButton(
                                onClick = { onCustomizeReminders?.invoke() }
                            ) {
                                Text("Customize")
                            }
                        }
                    }
                }
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
        val previewGoalsWithProgress = testScheduledGoalsSizeThree.toMutableList().apply {
            this[0] = this[0].copy(
                scheduledHours = 0,
                scheduledMinutes = 1,
                completedMillis = 60_000L,
                status = GoalStatus.COMPLETED
            )
        }

        val currentTaskGoals = testScheduledGoalsWithCategoriesSizeThree

        ScheduledGoalList(
            goals = currentTaskGoals,
            categories = testGoalCategoriesSizeThreeWithColor,
            addCompletionColor = false,
            addCategoryColors = true,
            addCheckboxes = true,
            selectedGoalId = 0,
            onCompleteChange = {_,_->},
        )
    }
}