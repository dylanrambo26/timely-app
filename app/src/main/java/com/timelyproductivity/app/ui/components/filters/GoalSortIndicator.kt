package com.timelyproductivity.app.ui.components.filters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.ui.components.sorting.GoalSortOption
import com.timelyproductivity.app.ui.filters.GoalFilterState
import com.timelyproductivity.app.ui.sorting.sortDisplayLabel

@Composable
fun GoalSortIndicator(
    filters: GoalFilterState,
    onSortAscendingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (filters.sortOption == GoalSortOption.DEFAULT) return

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClickLabel = "Reverse sort direction"
            ){
                onSortAscendingChanged(!filters.sortAscending)
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (filters.sortAscending) {
                Icons.Default.ArrowUpward
            } else {
                Icons.Default.ArrowDownward
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = filters.sortDisplayLabel(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}