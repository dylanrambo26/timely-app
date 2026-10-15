package com.timelyproductivity.app.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.data.testGoalCountMap
import com.timelyproductivity.app.data.testGoalCounts
import com.timelyproductivity.app.data.testScheduledGoalCountMap
import com.timelyproductivity.app.data.toSortedByColorAsc
import com.timelyproductivity.app.data.toSortedByColorDesc
import com.timelyproductivity.app.data.toSortedByGoalCountAsc
import com.timelyproductivity.app.data.toSortedByGoalCountDesc
import com.timelyproductivity.app.data.toSortedByNameAsc
import com.timelyproductivity.app.data.toSortedByNameDesc
import com.timelyproductivity.app.data.toSortedByScheduledGoalCountAsc
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.TimelyScaffold
import com.timelyproductivity.app.ui.components.categories.CategoryEditorDialog
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.toColor

object ManageCategoriesDestination : NavigationDest {
    override val route = "manage_categories_screen"
    override val titleRes = R.string.manage_categories
}

@Composable
fun ManageCategoriesScreen(
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit,
    navigateToHome: () -> Unit,
    navigateBack: () -> Unit,

    manageCategoriesViewModel: ManageCategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val manageCategoriesUiState by manageCategoriesViewModel.manageCategoriesUiState.collectAsState()
    val categoryEditorUiState by manageCategoriesViewModel.categoryEditorUiState.collectAsState()
    val categoryPendingDeletion by manageCategoriesViewModel.categoryPendingDeletion.collectAsState()
    TimelyScaffold(
        topBarTitle = stringResource(R.string.manage_categories),
        onHomeClick = navigateToHome,
        onCalendarClick = navigateToCalendar,
        onAnalyticsClick = navigateToAnalytics
    ) { innerPadding ->
        ManageCategoriesBody(
            manageCategoriesUiState = manageCategoriesUiState,
            categoryEditorUiState = categoryEditorUiState,

            onAddCategory = manageCategoriesViewModel::openAddCategoryEditor,
            onEditCategory = manageCategoriesViewModel::openEditCategoryEditor,
            onDeleteCategory = manageCategoriesViewModel::requestDeleteCategory,
            navigateBack = navigateBack,

            onCategoryInputChanged = manageCategoriesViewModel::updateCategoryName,
            onColorSelected = manageCategoriesViewModel::updateCategoryColor,
            onSaveCategory = manageCategoriesViewModel::saveCategory,
            onDismissCategoryDialog = manageCategoriesViewModel::closeCategoryDialog,

            updateSortOption = manageCategoriesViewModel::updateSortOption,
            updateSortDescending = manageCategoriesViewModel::updateSortDescending,

            modifier = Modifier.padding(innerPadding)
        )
    }

    categoryPendingDeletion?.let { category ->
        DeleteCategoryDialog(
            category = category,
            onConfirm = manageCategoriesViewModel::confirmDeleteCategory,
            onDismiss = manageCategoriesViewModel::cancelDeleteCategory
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesBody(
    manageCategoriesUiState: ManageCategoriesUiState,
    categoryEditorUiState: CategoryEditorUiState,

    onAddCategory: () -> Unit,
    onEditCategory: (GoalCategory) -> Unit,
    onDeleteCategory: (GoalCategory) -> Unit,

    onCategoryInputChanged: (String) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissCategoryDialog: () -> Unit,
    onColorSelected: (CategoryColor) -> Unit,

    updateSortOption: (CategorySortOption) -> Unit,
    updateSortDescending: (Boolean) -> Unit,

    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    if(categoryEditorUiState.isVisible){
        CategoryEditorDialog(
            editorUiState = categoryEditorUiState,
            onInputChanged = onCategoryInputChanged,
            onColorSelected = onColorSelected,
            onSave = onSaveCategory,
            onDismiss = onDismissCategoryDialog
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {

        CategorySortDropdown(
            sortOption = manageCategoriesUiState.sortOption,
            sortDescending = manageCategoriesUiState.sortDescending,
            onSortOptionChanged = updateSortOption,
            onSortDescendingChanged = updateSortDescending,
        )

        CategoryList(
            manageCategoriesUiState = manageCategoriesUiState,
            onEditCategory = onEditCategory,
            onDeleteCategory = onDeleteCategory,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onAddCategory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text("Add Category")
        }

        OutlinedButton(
            onClick = navigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text("Back to Settings")
        }
    }
}


@Composable
fun CategoryList(
    manageCategoriesUiState: ManageCategoriesUiState,
    onEditCategory: (GoalCategory) -> Unit,
    onDeleteCategory: (GoalCategory) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
    ){
        items(
            items = manageCategoriesUiState.categories,
            key = { it.categoryId }
        ){category ->
            CategoryCard(
                category = category,
                goalCount = manageCategoriesUiState.goalCounts[category.categoryId] ?: 0,
                scheduledGoalCount = manageCategoriesUiState.scheduledGoalCounts[category.categoryId] ?: 0,
                sortOption = manageCategoriesUiState.sortOption,
                onEditCategory = onEditCategory,
                onDeleteCategory = onDeleteCategory
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: GoalCategory,
    goalCount: Int,
    scheduledGoalCount: Int,
    sortOption: CategorySortOption,
    onEditCategory: (GoalCategory) -> Unit,
    onDeleteCategory: (GoalCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(category.color.toColor().copy(
                        alpha = 1.0f
                    ))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val count = when (sortOption) {
                    CategorySortOption.GOAL_COUNT -> goalCount
                    CategorySortOption.SCHEDULED_GOAL_COUNT -> scheduledGoalCount
                    else -> null
                }

                count?.let {
                    val label = when (sortOption) {
                        CategorySortOption.GOAL_COUNT -> "reusable goal"
                        CategorySortOption.SCHEDULED_GOAL_COUNT -> "scheduled goal"
                        else -> ""
                    }

                    Text(
                        text = "$it $label${if (it == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { onEditCategory(category) }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${category.name}"
                )
            }

            IconButton(
                onClick = { onDeleteCategory(category) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete ${category.name}"
                )
            }
        }
    }
}

@Composable
fun DeleteCategoryDialog(
    category: GoalCategory,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Category?")
        },
        text = {
            Text(
                "Are you sure you want to delete the \"${category.name}\" category? " +
                        "This category will be removed from all goals that use it. " +
                        "Your goals will not be deleted."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySortDropdown(
    sortOption: CategorySortOption,
    sortDescending: Boolean,
    onSortOptionChanged: (CategorySortOption) -> Unit,
    onSortDescendingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
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
                CategorySortOption.entries.forEach { option ->
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
                onSortDescendingChanged(!sortDescending)
            }
        ) {
            Icon(
                imageVector = if(sortDescending){
                    Icons.Default.KeyboardArrowDown
                } else {
                    Icons.Default.KeyboardArrowUp
                },
                contentDescription = if (sortDescending) {
                    "Sort ascending"
                } else {
                    "Sort descending"
                }
            )
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun CategoryCardPreview(){
    TimeManagementAppTheme {
        CategoryCard(
            category = testGoalCategoriesSizeThreeWithColor[0],
            onEditCategory = {},
            onDeleteCategory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryListPreview(){
    TimeManagementAppTheme {
        CategoryList(
            categories = testGoalCategoriesSizeThreeWithColor,
            onEditCategory = {},
            onDeleteCategory = {}
        )
    }
}*/

@Preview(showBackground = true)
@Composable
fun ManageCategoriesBodyPreview(){
    TimeManagementAppTheme {
        ManageCategoriesBody(
            onAddCategory = {},
            onEditCategory = {},
            onDeleteCategory = {},
            navigateBack = {},
            onDismissCategoryDialog = {},
            onSaveCategory = {},
            onCategoryInputChanged = {},
            onColorSelected = {},
            categoryEditorUiState = CategoryEditorUiState(),
            manageCategoriesUiState = ManageCategoriesUiState(
                categories = testGoalCategoriesSizeThreeWithColor.toSortedByScheduledGoalCountAsc(),
                sortOption = CategorySortOption.SCHEDULED_GOAL_COUNT,
                goalCounts = testGoalCountMap,
                scheduledGoalCounts = testScheduledGoalCountMap,
                sortDescending = true,
            ),
            updateSortDescending = {},
            updateSortOption = {}
        )
    }
}