package com.timelyproductivity.app.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.ui.components.sorting.GoalSortOption
import com.timelyproductivity.app.ui.filters.CompletionFilter
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.filters.RecurrenceFilter
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.util.enumNameToFirstLetterCapitalized

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFilterSheet(
    filters: GoalFilterState,
    categories: List<GoalCategory>,
    showCompletionFilter: Boolean = true,
    showRecurrenceFilter: Boolean = false,
    onFiltersChanged: (GoalFilterState) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        GoalFilterSheetContent(
            filters = filters,
            categories = categories,
            showCompletionFilter = showCompletionFilter,
            showRecurrenceFilter = showRecurrenceFilter,
            onFiltersChanged = onFiltersChanged,
            onDismiss = onDismiss
        )
    }
}
@Composable
fun GoalFilterSheetContent(
    filters: GoalFilterState,
    categories: List<GoalCategory>,
    showCompletionFilter: Boolean = true,
    showRecurrenceFilter: Boolean = false,
    onFiltersChanged: (GoalFilterState) -> Unit,
    onDismiss: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Goals",
                style = MaterialTheme.typography.titleLarge
            )

            TextButton(
                onClick = {
                    onFiltersChanged(GoalFilterState())
                }
            ) {
                Text("Clear all")
            }
        }

        HorizontalDivider()

        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium
        )

        categories.forEach { category ->
            FilterCheckboxRow(
                label = category.name,
                checked = category.categoryId in filters.categoryIds,
                onCheckedChange = { checked ->
                    val updatedCategories =
                        if (checked) {
                            filters.categoryIds + category.categoryId
                        } else {
                            filters.categoryIds - category.categoryId
                        }

                    onFiltersChanged(
                        filters.copy(categoryIds = updatedCategories)
                    )
                }
            )
        }

        HorizontalDivider()

        Text(
            text = "Priority",
            style = MaterialTheme.typography.titleMedium
        )

        val priorityOptions: List<GoalPriority?> =
            GoalPriority.entries + listOf(null)

        priorityOptions.forEach { priority ->
            FilterCheckboxRow(
                label = priority?.name ?: "None",
                checked = priority in filters.priorities,
                onCheckedChange = { checked ->
                    val updatedPriorities =
                        if (checked) {
                            filters.priorities + priority
                        } else {
                            filters.priorities - priority
                        }

                    onFiltersChanged(
                        filters.copy(priorities = updatedPriorities)
                    )
                }
            )
        }

        if (showCompletionFilter) {
            HorizontalDivider()

            Text(
                text = "Completion",
                style = MaterialTheme.typography.titleMedium
            )

            CompletionFilter.entries.forEach { completion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = filters.completionFilter == completion,
                            onClick = {
                                onFiltersChanged(
                                    filters.copy(
                                        completionFilter = completion
                                    )
                                )
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = filters.completionFilter == completion,
                        onClick = null
                    )

                    Text(completion.name.enumNameToFirstLetterCapitalized())
                }
            }
        }

        if(showRecurrenceFilter){
            HorizontalDivider()

            Text(
                text = "Recurrence",
                style = MaterialTheme.typography.titleMedium
            )

            RecurrenceFilter.entries.forEach { recurrence ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = filters.recurrenceFilter == recurrence,
                            onClick = {
                                onFiltersChanged(
                                    filters.copy(
                                        recurrenceFilter = recurrence
                                    )
                                )
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = filters.recurrenceFilter == recurrence,
                        onClick = null
                    )

                    Text(recurrence.name.enumNameToFirstLetterCapitalized())
                }
            }
        }

        HorizontalDivider()

        Text(
            text = "Sort Goals",
            style = MaterialTheme.typography.titleMedium
        )

        GoalSortDropdown(
            sortOption = filters.sortOption,
            sortAscending = filters.sortAscending,
            onSortOptionChanged = {option ->
                onFiltersChanged(
                    filters.copy(
                        sortOption = option
                    )
                )
            },
            onSortAscendingChanged = {ascending ->
                onFiltersChanged(
                    filters.copy(
                        sortAscending = ascending
                    )
                )
            },

        )

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalSortDropdown(
    sortOption: GoalSortOption,
    sortAscending: Boolean,
    onSortOptionChanged: (GoalSortOption) -> Unit,
    onSortAscendingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = sortOption.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sort by") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                GoalSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onSortOptionChanged(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        IconButton(
            onClick = {
                onSortAscendingChanged(!sortAscending)
            },
            enabled = sortOption != GoalSortOption.DEFAULT
        ) {
            Icon(
                imageVector = if(sortAscending){
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = if (sortAscending) {
                    "Sort descending"
                } else {
                    "Sort ascending"
                }
            )
        }
    }
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Text(
            text = label.enumNameToFirstLetterCapitalized(),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun GoalFilterSheetPreview(){
    TimeManagementAppTheme {
        GoalFilterSheetContent(
            categories = testGoalCategoriesSizeThreeWithColor,
            filters = GoalFilterState(
                categoryIds = testGoalCategoriesSizeThreeWithColor
                    .map { it.categoryId }
                    .toSet(),
                priorities = setOf(
                    GoalPriority.LOW,
                    GoalPriority.MEDIUM,
                    GoalPriority.HIGH
                ),
                searchQuery = "",
                completionFilter = CompletionFilter.ALL
            ),
            onFiltersChanged = {},
            onDismiss = {},
            showRecurrenceFilter = true
        )
    }
}