package com.timelyproductivity.app.ui.editScheduled

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.TimelyBottomAppBar
import com.timelyproductivity.app.TimelySmallTopAppBar
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.categories.CategoryEditorUiState
import com.timelyproductivity.app.ui.components.ErrorMessageBox
import com.timelyproductivity.app.ui.components.PriorityDropdown
import com.timelyproductivity.app.ui.components.categories.CategoryDropdown
import com.timelyproductivity.app.ui.components.categories.CategoryEditorDialog
import com.timelyproductivity.app.ui.components.lists.GoalTemplateCard
import com.timelyproductivity.app.ui.createGoal.GoalDetails
import com.timelyproductivity.app.ui.createGoal.GoalUiState
import com.timelyproductivity.app.ui.createGoal.toGoal
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import kotlinx.coroutines.launch

object EditScheduledGoalDestination : NavigationDest {
    override val route = "edit_goal"
    override val titleRes = R.string.edit_one_goal
    const val scheduledGoalIdArg = "scheduledGoalId"
    val routeWithArgs = "$route/{$scheduledGoalIdArg}"
}

@Composable
fun EditScheduledGoalScreen(
    modifier: Modifier = Modifier,
    editScheduledGoalViewModel: EditScheduledGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToEditScheduledGoals: (Int) -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit, //TODO
    navigateToAnalytics: () -> Unit, //TODO
){
    val coroutineScope = rememberCoroutineScope()
    val categoryEditorUiState by editScheduledGoalViewModel.categoryEditorUiState.collectAsState()
    Scaffold(
        topBar = { TimelySmallTopAppBar(stringResource(R.string.edit_one_goal)) },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = navigateToCalendar,
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )}
    ) { innerPadding ->
        EditScheduledGoalBody(
            goalUiState = editScheduledGoalViewModel.goalUiState,
            onGoalValueChange = editScheduledGoalViewModel::updateUiState,
            onSaveGoalClick = {
                coroutineScope.launch {
                    editScheduledGoalViewModel.updateScheduledGoal { eventId ->
                        navigateToEditScheduledGoals(eventId)
                    }
                }
            },
            navigateBack = { editScheduledGoalViewModel.calendarEventId.value?.let(navigateToEditScheduledGoals) },
            onCategorySelected = {category ->
                editScheduledGoalViewModel.selectCategory(category?.categoryId)
            },
            categoryEditorUiState = categoryEditorUiState,
            onCreateCategory = editScheduledGoalViewModel::openAddCategoryEditor,
            onCategoryInputChanged = editScheduledGoalViewModel::updateCategoryName,
            onSaveCategory = editScheduledGoalViewModel::saveCategory,
            onDismissCategoryDialog = editScheduledGoalViewModel::closeCategoryDialog,
            onColorSelected = editScheduledGoalViewModel::updateCategoryColor,

            onPrioritySelected = editScheduledGoalViewModel::selectPriority,

            modifier = modifier.padding(innerPadding)
        )
    }

}

@Composable
fun EditScheduledGoalBody(
    goalUiState: GoalUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onSaveGoalClick: () -> Unit,

    onCategorySelected: (GoalCategory?) -> Unit,
    categoryEditorUiState: CategoryEditorUiState,
    onCreateCategory: () -> Unit,
    onCategoryInputChanged: (String) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissCategoryDialog: () -> Unit,
    onColorSelected: (CategoryColor) -> Unit,

    onPrioritySelected: (GoalPriority?) -> Unit,

    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(dimensionResource(R.dimen.padding_medium))
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if(categoryEditorUiState.isVisible){
            CategoryEditorDialog(
                editorUiState = categoryEditorUiState,
                onInputChanged = onCategoryInputChanged,
                onColorSelected = onColorSelected,
                onSave = onSaveCategory,
                onDismiss = onDismissCategoryDialog
            )
        }

        val oldGoalDetails = remember(goalUiState.goalDetails.id){
            goalUiState.goalDetails
        }
        Text(stringResource(R.string.old_goal))
        GoalTemplateCard(
            goal = oldGoalDetails.toGoal(
                categoryId = goalUiState.originalCategoryId,
                priority = goalUiState.originalPriority
            ),
            categories = goalUiState.goalCategories,
            addCategoryColors = goalUiState.useCategoryColorsEnabled
        )
        Text(stringResource(R.string.new_goal))
        GoalTemplateCard(
            goal = goalUiState.goalDetails.toGoal(
                categoryId = goalUiState.selectedCategoryId,
                priority = goalUiState.priority
            ),
            categories = goalUiState.goalCategories,
            addCategoryColors = goalUiState.useCategoryColorsEnabled
        )


        EditGoalInputForm(
            goalDetails = goalUiState.goalDetails,
            onValueChange = onGoalValueChange,
            modifier = Modifier.fillMaxWidth(),
            isDurationEditable = goalUiState.isDurationEditable
        )

        val selectedCategory = goalUiState.goalCategories
            .firstOrNull {
                it.categoryId == goalUiState.selectedCategoryId
            }

        CategoryDropdown(
            categories = goalUiState.goalCategories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            onCreateCategory = onCreateCategory
        )

        PriorityDropdown(
            selectedPriority = goalUiState.priority,
            onPrioritySelected = onPrioritySelected
        )

        //Error Message Space
        ErrorMessageBox(
            errorMessageValue = goalUiState.errorMessage
        )

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            //Cancel Edit Button
            OutlinedButton(
                onClick = navigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel_edit_one_goal),
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }

            //Save Goal Button
            OutlinedButton(
                onClick = onSaveGoalClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.save_edit_one_goal),
                    fontSize = 16.sp,
                    color = Color.Green
                )
            }
        }
    }
}

@Composable
fun EditGoalInputForm(
    isDurationEditable: Boolean,
    goalDetails: GoalDetails,
    modifier: Modifier = Modifier,
    onValueChange: (GoalDetails) -> Unit = {}
){
    if(!isDurationEditable){
        Text(
            text = stringResource(R.string.duration_locked),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        //Hours Text Field
        OutlinedTextField(
            value = goalDetails.hours,
            onValueChange = {onValueChange(goalDetails.copy(hours = it))},
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(),
            label = {
                Text("Hours")
            },
            enabled = isDurationEditable
        )
        // Minutes Text Field
        OutlinedTextField(
            value = goalDetails.minutes,
            onValueChange = {onValueChange(goalDetails.copy(minutes = it))},
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(),
            label = {
                Text("Minutes")
            },
            enabled = isDurationEditable
        )
    }

    //Goal Title Text Field
    OutlinedTextField(
        value = goalDetails.title,
        onValueChange = {onValueChange(goalDetails.copy(title = it))},
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        colors = OutlinedTextFieldDefaults.colors(),
        label = {
            Text("Goal Title")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditScheduledGoalScreenPreview(){
    TimeManagementAppTheme {
        EditScheduledGoalBody(
           goalUiState = GoalUiState(
               GoalDetails(
                   title = "Title", hours = "1", minutes = "30"
               ),
               isDurationEditable = true,
               selectedCategoryId = 1,
               originalCategoryId = 0,
               errorMessage = R.string.selected_goal_exceeds_remaining_time,
               goalCategories = testGoalCategoriesSizeThreeWithColor,
               useCategoryColorsEnabled = true,
               priority = GoalPriority.HIGH
           ),
            onGoalValueChange = {},
            onSaveGoalClick = {},
            navigateBack = {},
            onCategorySelected = {},
            onSaveCategory = {},
            onColorSelected = {},
            onCategoryInputChanged = {},
            onDismissCategoryDialog = {},
            onCreateCategory = {},
            categoryEditorUiState = CategoryEditorUiState(),
            onPrioritySelected = {}
        )
    }
}