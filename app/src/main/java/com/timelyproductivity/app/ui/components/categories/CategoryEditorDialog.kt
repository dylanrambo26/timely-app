package com.timelyproductivity.app.ui.components.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.ui.categories.CategoryEditorUiState
import com.timelyproductivity.app.ui.theme.toColor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryEditorDialog(
    editorUiState: CategoryEditorUiState,
    onInputChanged: (String) -> Unit,
    onColorSelected: (CategoryColor) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if(editorUiState.originalCategoryId == null){
                    "Add category"
                } else {
                    "Edit category"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = editorUiState.input,
                    onValueChange = onInputChanged,
                    label = {
                        Text("Category name")
                    },
                    singleLine = true,
                    isError = editorUiState.errorMessage != null,
                    supportingText = {
                        editorUiState.errorMessage?.let {
                            Text(it)
                        }
                    }
                )

                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelLarge
                )

                CategoryColorSelector(
                    selectedColor = editorUiState.selectedColor,
                    onColorSelected = onColorSelected
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave){
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
fun CategoryColorSelector(
    selectedColor: CategoryColor,
    onColorSelected: (CategoryColor) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CategoryColor.entries.forEach { categoryColor ->
            val color = categoryColor.toColor()

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable{
                        onColorSelected(categoryColor)
                    }
                    .then(
                        if (categoryColor == selectedColor){
                            Modifier.border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                        } else {
                                Modifier
                            }
                    )
            )
        }
    }
}
