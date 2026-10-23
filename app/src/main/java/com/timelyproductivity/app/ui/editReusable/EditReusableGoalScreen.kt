package com.timelyproductivity.app.ui.editReusable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.goal.category.GoalCategory
import com.timelyproductivity.app.data.goal.priority.GoalPriority
import com.timelyproductivity.app.data.testGoalCategoriesSizeThreeWithColor
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.TimelyScaffold
import com.timelyproductivity.app.ui.categories.CategoryEditorUiState
import com.timelyproductivity.app.ui.components.ErrorMessageBox
import com.timelyproductivity.app.ui.components.PriorityDropdown
import com.timelyproductivity.app.ui.components.RecurrenceOptions
import com.timelyproductivity.app.ui.components.RecurringGoalBody
import com.timelyproductivity.app.ui.components.categories.CategoryDropdown
import com.timelyproductivity.app.ui.components.categories.CategoryEditorDialog
import com.timelyproductivity.app.ui.components.lists.GoalTemplateCard
import com.timelyproductivity.app.ui.createGoal.GoalDetails
import com.timelyproductivity.app.ui.createGoal.GoalUiState
import com.timelyproductivity.app.ui.createGoal.toGoal
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

object EditReusableGoalDestination : NavigationDest {
    override val route = "edit_reusable_goal"
    override val titleRes = R.string.edit_one_goal
    const val goalIdArg = "goalId"
    val routeWithArgs = "$route/{$goalIdArg}"
}

@Composable
fun EditReusableGoalScreen(
    modifier: Modifier = Modifier,
    viewModel: EditReusableGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),

    navigateToCreateGoalWithGoalId: (Int) -> Unit,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit,
){
    val coroutineScope = rememberCoroutineScope()
    val categoryEditorUiState by viewModel.categoryEditorUiState.collectAsState()
    TimelyScaffold(
        topBarTitle = stringResource(R.string.edit_one_goal_top_bar_text),
        onHomeClick = navigateToHome,
        onCalendarClick = navigateToCalendar,
        onAnalyticsClick = navigateToAnalytics
    ) { innerPadding ->
        EditReusableGoalBody(
            goalUiState = viewModel.goalUiState,
            onGoalValueChange = viewModel::updateUiState,
            onSaveAndUpdateScheduledGoalsClicked = {
                coroutineScope.launch {
                    viewModel.updateReusableGoal(
                        onNavigate = navigateBack,
                        updateFutureScheduledGoals = true
                    )
                }
            },
            onSaveGoal = {
                coroutineScope.launch {
                    viewModel.updateReusableGoal(
                        onNavigate = navigateBack,
                        updateFutureScheduledGoals = false
                    )
                }
            },
            navigateBack = navigateBack,

            onDailyChange = viewModel::updateAllRecurringDays,
            onRecurringDayChange = viewModel::onRecurringDayChange,
            onEndDateEnabledChanged = viewModel::updateHasRecurrenceEndDate,
            updateRecurrenceStartDate = viewModel::updateRecurrenceStartDate,
            updateRecurrenceEndDate = viewModel::updateRecurrenceEndDate,
            onRecurringChange = viewModel::updateIsGoalRecurring,
            onCreateNonRecurringCopyClicked = {
                navigateToCreateGoalWithGoalId(
                    viewModel.goalUiState.goalDetails.id
                )
            },
            onCategorySelected = {category ->
                viewModel.selectCategory(category?.categoryId)
            },
            categoryEditorUiState = categoryEditorUiState,
            onCreateCategory = viewModel::openAddCategoryEditor,
            onCategoryInputChanged = viewModel::updateCategoryName,
            onSaveCategory = viewModel::saveCategory,
            onDismissCategoryDialog = viewModel::closeCategoryDialog,
            onColorSelected = viewModel::updateCategoryColor,

            onPrioritySelected = viewModel::selectPriority,

            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EditReusableGoalBody(
    goalUiState: GoalUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onSaveAndUpdateScheduledGoalsClicked: () -> Unit,
    onCreateNonRecurringCopyClicked: () -> Unit,
    onSaveGoal: () -> Unit,
    navigateBack: () -> Unit,

    onDailyChange: (Boolean) -> Unit,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,
    updateRecurrenceStartDate: (LocalDate) -> Unit,
    updateRecurrenceEndDate: (LocalDate?) -> Unit,
    onRecurringChange: (Boolean) -> Unit,

    onCategorySelected: (GoalCategory?) -> Unit,
    categoryEditorUiState: CategoryEditorUiState,
    onCreateCategory: () -> Unit,
    onCategoryInputChanged: (String) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissCategoryDialog: () -> Unit,
    onColorSelected: (CategoryColor) -> Unit,

    onPrioritySelected: (GoalPriority?) -> Unit,

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

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val oldGoalDetails = remember(goalUiState.goalDetails.id){
            goalUiState.goalDetails
        }

        Text(stringResource(R.string.existing_goal))
        GoalTemplateCard(
            goal = oldGoalDetails.toGoal(
                categoryId = goalUiState.originalCategoryId,
                priority = goalUiState.originalPriority
            ),
            categories = goalUiState.goalCategories,
            addCategoryColors = goalUiState.useCategoryColorsEnabled,
            recurringDays = goalUiState.originalRecurringDays.takeIf { goalUiState.wasOriginallyRecurring }
        )

        Text(stringResource(R.string.updated_goal))
        GoalTemplateCard(
            goal = goalUiState.goalDetails.toGoal(
                categoryId = goalUiState.selectedCategoryId,
                priority = goalUiState.priority
            ),
            categories = goalUiState.goalCategories,
            addCategoryColors = goalUiState.useCategoryColorsEnabled,
            recurringDays = goalUiState.recurringDays.takeIf { goalUiState.isGoalRecurring }
        )

        EditGoalInputForm(
            goalDetails = goalUiState.goalDetails,
            onValueChange = onGoalValueChange,
            modifier = Modifier.fillMaxWidth()
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

        EditRecurrenceRuleBody(
            goalUiState = goalUiState,
            onDailyChange = onDailyChange,
            onRecurringDayChange = onRecurringDayChange,
            onEndDateEnabledChanged = onEndDateEnabledChanged,
            updateRecurrenceEndDate = updateRecurrenceEndDate,
            updateRecurrenceStartDate = updateRecurrenceStartDate,
            onRecurringChange = onRecurringChange,
        )

        ErrorMessageBox(
            errorMessageValue = goalUiState.errorMessage
        )

        Column(
            modifier = Modifier.width(280.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Save Goal Button
            Button(
                onClick = onSaveGoal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = "Save Changes to Template Only",
                    fontSize = 16.sp,
                )
            }

            //Save Goal and Ripple Edit future scheduled goals
            OutlinedButton(
                onClick = onSaveAndUpdateScheduledGoalsClicked,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Apply changes to future goals",
                    fontSize = 16.sp,
                )
            }

            if(goalUiState.wasOriginallyRecurring){
                OutlinedButton(
                    onClick = onCreateNonRecurringCopyClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create Non-recurring copy",
                        fontSize = 16.sp
                    )
                }
            }
            //Cancel Edit Button
            TextButton(
                onClick = navigateBack,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.cancel_edit_one_goal),
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
fun EditRecurrenceRuleBody(
    goalUiState: GoalUiState,
    onDailyChange: (Boolean) -> Unit,
    onRecurringDayChange: (DayOfWeek, Boolean) -> Unit,
    onEndDateEnabledChanged: (Boolean) -> Unit,
    updateRecurrenceStartDate: (LocalDate) -> Unit,
    updateRecurrenceEndDate: (LocalDate?) -> Unit,
    onRecurringChange: (Boolean) -> Unit,
){
    if(goalUiState.wasOriginallyRecurring){
        Column {
            RecurrenceOptions(
                recurrenceEndDate = goalUiState.recurrenceEndDate,
                recurrenceStartDate = goalUiState.recurrenceStartDate,
                hasRecurrenceEndDate = goalUiState.hasRecurrenceEndDate,
                recurringDays = goalUiState.recurringDays,
                onRecurringDayChange = onRecurringDayChange,
                onEndDateEnabledChanged = onEndDateEnabledChanged,
                updateRecurrenceEndDate = updateRecurrenceEndDate,
                updateRecurrenceStartDate = updateRecurrenceStartDate,
                onDailyChange = onDailyChange
            )
        }
    } else {
        RecurringGoalBody(
            recurringDays = goalUiState.recurringDays,
            recurrenceStartDate = goalUiState.recurrenceStartDate,
            recurrenceEndDate = goalUiState.recurrenceEndDate,
            hasRecurrenceEndDate = goalUiState.hasRecurrenceEndDate,
            isGoalRecurring = goalUiState.isGoalRecurring,
            onRecurringChange = onRecurringChange,
            onDailyChange = onDailyChange,
            onRecurringDayChange = onRecurringDayChange,
            onEndDateEnabledChanged = onEndDateEnabledChanged,
            updateRecurrenceStartDate = updateRecurrenceStartDate,
            updateRecurrenceEndDate = updateRecurrenceEndDate
        )
    }
}

@Composable
fun EditGoalInputForm(
    goalDetails: GoalDetails,
    modifier: Modifier = Modifier,
    onValueChange: (GoalDetails) -> Unit = {}
){
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
            }
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
            }
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

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun EditReusableGoalBodyPreview(){
    TimeManagementAppTheme {
        EditReusableGoalBody(
            goalUiState = GoalUiState(
                GoalDetails(
                    title = "Title", hours = "1", minutes = "30"
                ),
                isGoalRecurring = true,
                wasOriginallyRecurring = true,
                recurringDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                originalRecurringDays = setOf(DayOfWeek.THURSDAY, DayOfWeek.TUESDAY),
                errorMessage = R.string.select_at_least_one_recurring_day,
                goalCategories = testGoalCategoriesSizeThreeWithColor,
                selectedCategoryId = 0,
                originalCategoryId = 1,
                useCategoryColorsEnabled = true,
                priority = GoalPriority.HIGH
            ),
            onGoalValueChange = {},
            onSaveAndUpdateScheduledGoalsClicked = {},
            onSaveGoal = {},
            navigateBack = {},
            updateRecurrenceEndDate = {},
            onDailyChange = {},
            onRecurringChange = {},
            onRecurringDayChange = {_,_->},
            onCreateNonRecurringCopyClicked = {},
            updateRecurrenceStartDate = {},
            onEndDateEnabledChanged = {},
            onSaveCategory = {},
            onColorSelected = {},
            onCreateCategory = {},
            onCategorySelected = {},
            onCategoryInputChanged = {},
            onDismissCategoryDialog = {},
            categoryEditorUiState = CategoryEditorUiState(),
            onPrioritySelected = {}
        )
    }
}