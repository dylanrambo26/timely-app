package com.timelyproductivity.app.ui.viewgoals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.scheduledgoal.ScheduledGoal
import com.timelyproductivity.app.data.testScheduledGoalsWithCategoriesSizeFour
import com.timelyproductivity.app.ui.components.buttons.TwoActionButtonRow
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

@Composable
fun ManualTimeLogDialog(
    scheduledGoal: ScheduledGoal,
    uiState: ManualTimeLogUiState,
    onHoursChanged: (String) -> Unit,
    onMinutesChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        ManualTimeLogDialogContent(
            scheduledGoal = scheduledGoal,
            uiState = uiState,
            onDismiss = onDismiss,
            onSave = onSave,
            onHoursChanged = onHoursChanged,
            onMinutesChanged = onMinutesChanged
        )
    }
}

@Composable
fun ManualTimeLogDialogContent(
    scheduledGoal: ScheduledGoal,
    uiState: ManualTimeLogUiState,
    onHoursChanged: (String) -> Unit,
    onMinutesChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add Manual Time Log",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = scheduledGoal.scheduledGoalTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Scheduled: " +
                        "${scheduledGoal.scheduledHours}h " +
                        "${scheduledGoal.scheduledMinutes}m",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.hours,
                    onValueChange = onHoursChanged,
                    label = { Text("Hours") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.minutes,
                    onValueChange = onMinutesChanged,
                    label = { Text("Minutes") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = stringResource(error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TwoActionButtonRow(
                onOutlinedButtonClicked = onDismiss,
                onButtonClicked = onSave,
                outlinedButtonText = "Cancel",
                buttonText = if (uiState.isSaving) {
                    "Saving..."
                } else {
                    "Save Log"
                },
                buttonEnabled = !uiState.isSaving
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ManualTimeLogDialogPreview(){
    TimeManagementAppTheme {
        ManualTimeLogDialogContent(
            scheduledGoal = testScheduledGoalsWithCategoriesSizeFour[0],
            onSave = {},
            uiState = ManualTimeLogUiState(
                hours = "3",
                minutes = "30",
                errorMessage = R.string.invalid_hours_0_23
            ),
            onHoursChanged = {},
            onMinutesChanged = {},
            onDismiss = {}
        )
    }
}