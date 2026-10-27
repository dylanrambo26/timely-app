package com.timelyproductivity.app.ui.components.filters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.filters.getFilterCount

@Composable
fun GoalFilteringControls(
    filters: GoalFilterState,
    categories: List<GoalCategory>,
    onFiltersChanged: (GoalFilterState) -> Unit,
    resultCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
    showCompletionFilter: Boolean = true,
    showRecurrenceFilter: Boolean = false,
){
    var showFilterSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var filtersExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleSmall
                )
                val activeFilterCount = getFilterCount(filters)

                if (activeFilterCount > 0) {
                    Text(
                        text = "($activeFilterCount active)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (activeFilterCount > 0) {
                        "$resultCount of $totalCount goals"
                    } else {
                        "$totalCount ${if (totalCount == 1) "goal" else "goals"}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = {
                    filtersExpanded = !filtersExpanded
                }
            ) {
                Text(if (filtersExpanded) "Hide" else "Show")
            }
        }

        AnimatedVisibility(
            visible = filtersExpanded
        ) {
            Column{
                GoalFilterBar(
                    searchQuery = filters.searchQuery,
                    onSearchQueryChanged = { query ->
                        onFiltersChanged(
                            filters.copy(
                                searchQuery = query
                            )
                        )
                    },
                    onFilterClick = {
                        showFilterSheet = true
                    }
                )
                GoalSortIndicator(
                    filters = filters,
                    onSortAscendingChanged = {ascending ->
                        onFiltersChanged(
                            filters.copy(
                                sortAscending = ascending
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )

                ActiveGoalFilters(
                    filters = filters,
                    categories = categories,
                    onFiltersChanged = onFiltersChanged
                )
            }
        }
    }

    if (showFilterSheet) {
        GoalFilterSheet(
            filters = filters,
            categories = categories,
            showCompletionFilter = showCompletionFilter,
            showRecurrenceFilter = showRecurrenceFilter,
            onFiltersChanged = onFiltersChanged,
            onDismiss = {
                showFilterSheet = false
            }
        )
    }
}