package com.timelyproductivity.app.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.R
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

@Composable
fun GoalFilterBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search goals") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_for_a_specific_goal)
                )
            },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onFilterClick) {
            Icon(
                Icons.Default.FilterAlt,
                contentDescription = "Filter Goals"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GoalFilterBarPreview(){
    TimeManagementAppTheme {
        GoalFilterBar(
            searchQuery = "Study",
            onSearchQueryChanged = {},
            onFilterClick = {},
        )
    }
}