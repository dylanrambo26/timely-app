package com.timelyproductivity.app.ui.components.categories

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<GoalCategory>,
    selectedCategory: GoalCategory?,
    onCategorySelected: (GoalCategory?) -> Unit,
    onCreateCategory: () -> Unit,
){
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "None",
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Category (Optional)")
            },
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
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("None")
                },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )

            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(category.name)
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }

            HorizontalDivider()

            DropdownMenuItem(
                text = {
                    Text("Create new category")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onCreateCategory()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryDropdownPreview(){
    TimeManagementAppTheme {
        CategoryDropdown(
            categories = testGoalCategoriesSizeThreeWithColor,
            selectedCategory = testGoalCategoriesSizeThreeWithColor[0],
            onCategorySelected = {},
            onCreateCategory = {}
        )
    }
}