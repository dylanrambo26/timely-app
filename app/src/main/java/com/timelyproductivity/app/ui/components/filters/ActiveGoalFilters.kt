package com.timelyproductivity.app.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveGoalFilters(
    filters: GoalFilterState,
    categories: List<GoalCategory>,
    onFiltersChanged: (GoalFilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters =
        filters.categoryIds.isNotEmpty() ||
                filters.priorities.isNotEmpty() ||
                filters.completionFilter != CompletionFilter.ALL ||
                filters.recurrenceFilter != RecurrenceFilter.ALL

    if (!hasActiveFilters) return
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(filters.categoryIds.toList()) { categoryId ->
                val category = categories.firstOrNull {
                    it.categoryId == categoryId
                }

                if (category != null) {
                    FilterChipItem(
                        label = "Category: ${category.name}",
                        onRemove = {
                            onFiltersChanged(
                                filters.copy(
                                    categoryIds = filters.categoryIds - categoryId
                                )
                            )
                        }
                    )
                }
            }

            items(filters.priorities.toList()) { priority ->
                FilterChipItem(
                    label = "Priority: ${priority?.name?.enumNameToFirstLetterCapitalized() ?: "None"}",
                    onRemove = {
                        onFiltersChanged(
                            filters.copy(
                                priorities = filters.priorities - priority
                            )
                        )
                    }
                )
            }

            if (filters.completionFilter != CompletionFilter.ALL) {
                item {
                    FilterChipItem(
                        label = filters.completionFilter.name.enumNameToFirstLetterCapitalized(),
                        onRemove = {
                            onFiltersChanged(
                                filters.copy(
                                    completionFilter = CompletionFilter.ALL
                                )
                            )
                        }
                    )
                }
            }
            if (filters.recurrenceFilter != RecurrenceFilter.ALL) {
                item {
                    FilterChipItem(
                        label = filters.recurrenceFilter.name.enumNameToFirstLetterCapitalized(),
                        onRemove = {
                            onFiltersChanged(
                                filters.copy(
                                    recurrenceFilter = RecurrenceFilter.ALL
                                )
                            )
                        }
                    )
                }
            }
        }
        TextButton(
            onClick = {
                onFiltersChanged(
                    GoalFilterState(
                        searchQuery = filters.searchQuery
                    )
                )
            }
        ) {
            Text("Clear all")
        }
    }

}

@Composable
private fun FilterChipItem(
    label: String,
    onRemove: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = onRemove,
        label = {
            Text(label)
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $label",
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Preview(showBackground = true, widthDp = 500)
@Composable
fun ActiveGoalFiltersPreview(){
    TimeManagementAppTheme {
        ActiveGoalFilters(
            filters = GoalFilterState(
                //categoryIds = setOf(testGoalCategoriesSizeThreeWithColor[0].categoryId, testGoalCategoriesSizeThreeWithColor[1].categoryId),
                priorities = setOf(GoalPriority.LOW),
                sortOption = GoalSortOption.CATEGORY
            ),
            onFiltersChanged = {},
            categories = testGoalCategoriesSizeThreeWithColor
        )
    }
}