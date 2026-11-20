package com.timelyproductivity.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.goal.GoalStatus
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.ui.components.lists.GoalStatusText
import com.timelyproductivity.app.ui.components.time.DisplayTime
import com.timelyproductivity.app.ui.components.time.RemainingTaskTime
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.checkbox
import com.timelyproductivity.app.ui.theme.completedGoal
import com.timelyproductivity.app.ui.theme.toColor
import com.timelyproductivity.app.util.MILLIS_IN_MINUTE

@Composable
fun CurrentTaskCard(
    scheduledGoal: ScheduledGoal,
    useCategoryColorsEnabled: Boolean,
    category: GoalCategory? = null,

    onMarkAsCompleteClicked: () -> Unit,
    onPauseButtonClicked: () -> Unit = {},
    onResumeButtonClicked: () -> Unit = {},

){
    val isCompleted = scheduledGoal.status == GoalStatus.COMPLETED

    val cardColor = when{
        isCompleted -> MaterialTheme.colorScheme.completedGoal

        useCategoryColorsEnabled && category != null -> {
            category.color.toColor()
        }
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(32.dp),
        color = if(isCompleted){
            MaterialTheme.colorScheme.completedGoal
        } else{
            cardColor
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            //Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current Task",
                )
                Text(
                    text = category?.name ?: "Uncategorized"
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = scheduledGoal.scheduledGoalTitle,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    val scheduledDurationMillis = (scheduledGoal.scheduledHours * 60L + scheduledGoal.scheduledMinutes) * MILLIS_IN_MINUTE
                    val completedDuration = scheduledGoal.completedMillis >= scheduledDurationMillis

                    Checkbox(
                        checked = scheduledGoal.status == GoalStatus.COMPLETED,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                onMarkAsCompleteClicked()
                            }
                        },
                        enabled = !completedDuration,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.checkbox,
                        )
                    )
                }

                RemainingTaskTime(
                    scheduledGoal = scheduledGoal,
                    paddingValue = dimensionResource(R.dimen.padding_small)
                )

                if(!isCompleted){
                    DisplayTime(
                        duration = scheduledGoal.scheduledHours * 60 + scheduledGoal.scheduledMinutes,
                        useParentheses = true
                    )
                }
                else{
                    GoalStatusText(
                        scheduledGoal = scheduledGoal,
                        showOriginalDuration = true,
                    )
                }


                val pauseButtonEnabled = (scheduledGoal.status == GoalStatus.RUNNING) ||
                        (scheduledGoal.status == GoalStatus.PAUSED)

                if(!isCompleted){
                    OutlinedButton(
                        onClick = {
                            if(scheduledGoal.status == GoalStatus.RUNNING){
                                onPauseButtonClicked()
                            } else if(scheduledGoal.status == GoalStatus.PAUSED){
                                onResumeButtonClicked()
                            }
                        },
                        enabled = pauseButtonEnabled,
                        border = BorderStroke(
                            1.dp,
                            LocalContentColor.current
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = LocalContentColor.current,
                            containerColor = Color.Transparent
                        ),
                    ) {
                        if(pauseButtonEnabled){
                            Icon(
                                imageVector = if(scheduledGoal.status == GoalStatus.RUNNING){
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                },
                                contentDescription = if(scheduledGoal.status == GoalStatus.RUNNING){
                                    "Pause Task"
                                } else {
                                    "Resume Task"
                                }
                            )
                        }


                        Spacer(Modifier.width(8.dp))

                        val pauseButtonText = when(scheduledGoal.status){
                            GoalStatus.PAUSED -> "Pause Task"
                            GoalStatus.RUNNING -> "Resume Task"
                            GoalStatus.NOT_STARTED -> "Not Started"
                            else -> ""
                        }

                        Text(
                            text = pauseButtonText,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CurrentTaskCardPreview(){
    TimeManagementAppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        CurrentTaskCard(
            scheduledGoal = ScheduledGoal(
                status = GoalStatus.PAUSED,
                scheduledHours = 1,
                scheduledMinutes = 30,
                completedMillis = MILLIS_IN_MINUTE,
                scheduledGoalTitle = "Study",
                eventId = 0,
                goalId = 0
            ),
            onMarkAsCompleteClicked = {},
            useCategoryColorsEnabled = true,
        )
    }
}