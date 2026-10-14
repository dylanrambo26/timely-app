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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
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
    val categories by manageCategoriesViewModel.categories.collectAsState()
    val categoryEditorUiState by manageCategoriesViewModel.categoryEditorUiState.collectAsState()
    TimelyScaffold(
        topBarTitle = stringResource(R.string.manage_categories),
        onHomeClick = navigateToHome,
        onCalendarClick = navigateToCalendar,
        onAnalyticsClick = navigateToAnalytics
    ) { innerPadding ->
        ManageCategoriesBody(
            categories = categories,
            categoryEditorUiState = categoryEditorUiState,

            onAddCategory = manageCategoriesViewModel::openAddCategoryEditor,
            onEditCategory = manageCategoriesViewModel::openEditCategoryEditor,
            onDeleteCategory = manageCategoriesViewModel::deleteCategory,
            navigateBack = navigateBack,

            onCategoryInputChanged = manageCategoriesViewModel::updateCategoryName,
            onColorSelected = manageCategoriesViewModel::updateCategoryColor,
            onSaveCategory = manageCategoriesViewModel::saveCategory,
            onDismissCategoryDialog = manageCategoriesViewModel::closeCategoryDialog,

            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ManageCategoriesBody(
    categories: List<GoalCategory>,
    categoryEditorUiState: CategoryEditorUiState,

    onAddCategory: () -> Unit,
    onEditCategory: (GoalCategory) -> Unit,
    onDeleteCategory: (GoalCategory) -> Unit,

    onCategoryInputChanged: (String) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissCategoryDialog: () -> Unit,
    onColorSelected: (CategoryColor) -> Unit,

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
        CategoryList(
            categories = categories,
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
    categories: List<GoalCategory>,
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
            items = categories,
            key = { it.categoryId }
        ){category ->
            CategoryCard(
                category = category,
                onEditCategory = onEditCategory,
                onDeleteCategory = onDeleteCategory
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: GoalCategory,
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

            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

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

@Preview(showBackground = true)
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
}

@Preview(showBackground = true)
@Composable
fun ManageCategoriesBodyPreview(){
    TimeManagementAppTheme {
        ManageCategoriesBody(
            categories = testGoalCategoriesSizeThreeWithColor,
            onAddCategory = {},
            onEditCategory = {},
            onDeleteCategory = {},
            navigateBack = {},
            onDismissCategoryDialog = {},
            onSaveCategory = {},
            onCategoryInputChanged = {},
            onColorSelected = {},
            categoryEditorUiState = CategoryEditorUiState()
        )
    }
}