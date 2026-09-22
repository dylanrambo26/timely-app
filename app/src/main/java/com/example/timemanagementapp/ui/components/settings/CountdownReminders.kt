package com.example.timemanagementapp.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.ui.settings.ReminderEditorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderEditorDialog(
    editorUiState: ReminderEditorUiState,
    onInputChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if(editorUiState.originalMinutes == null){
                    "Add reminder"
                } else {
                    "Edit reminder"
                }
            )
        },
        text = {
            OutlinedTextField(
                value = editorUiState.input,
                onValueChange = onInputChanged,
                label = {
                    Text("Minutes before completion")
                },
                singleLine = true,
                isError = editorUiState.errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                supportingText = {
                    editorUiState.errorMessage?.let {
                        Text(it)
                    }
                }
            )
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReminderTimes(
    selectedMinutes: Set<Int>,
    onEditReminder: (Int) -> Unit,
    onAddReminder: () -> Unit,
    onDeleteReminder: (Int) -> Unit,
    maxSizeReached: Boolean
){
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        selectedMinutes
            .sortedDescending()
            .forEach { minutes ->
                ListItem(
                    headlineContent = {
                        Text(
                            if(minutes == 1){
                                "1 minute before"
                            } else {
                                "$minutes minutes before"
                            }
                        )
                    },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = {
                                    onEditReminder(minutes)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit $minutes-minute reminder"
                                )
                            }

                            IconButton(
                                onClick = {
                                    onDeleteReminder(minutes)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription =
                                        "Delete $minutes-minute reminder"
                                )
                            }
                        }
                    }
                )
            }
        TextButton(
            onClick = onAddReminder,
            enabled = !maxSizeReached
        ) {
            if(!maxSizeReached){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

            }

            Text(
                text = if (maxSizeReached){
                    "Max number of countdown reminders reached. Delete one to add another."
                } else {
                    "Add reminder"
                }
            )
        }
    }
}