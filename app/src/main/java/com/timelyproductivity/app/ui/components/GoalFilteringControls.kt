package com.timelyproductivity.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.ui.filters.GoalFilterState

@Composable
fun GoalFilteringControls(
    filters: GoalFilterState,
    categories: List<GoalCategory>,
    onFiltersChanged: (GoalFilterState) -> Unit,
    modifier: Modifier = Modifier,
    showCompletionFilter: Boolean = true,
    showRecurrenceFilter: Boolean = false,
){
    var showFilterSheet by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ){
        GoalFilterBar(
            searchQuery = filters.searchQuery,
            onSearchQueryChanged = {query ->
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

        ActiveGoalFilters(
            filters = filters,
            categories = categories,
            onFiltersChanged = onFiltersChanged
        )
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