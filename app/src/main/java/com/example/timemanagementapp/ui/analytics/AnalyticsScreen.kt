package com.example.timemanagementapp.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.TimelyScaffold
import com.example.timemanagementapp.ui.components.ColorLegend
import com.example.timemanagementapp.ui.components.DisplayTime
import com.example.timemanagementapp.ui.components.DonutChart
import com.example.timemanagementapp.ui.components.LegendItem
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.completedGoal
import com.example.timemanagementapp.util.formatLocalDateToAnalyticsRange
import com.example.timemanagementapp.util.millisToMinutes
import java.time.LocalDate

object AnalyticsDestination : NavigationDest {
    override val route = "analytics"
    override val titleRes = R.string.analytics
}

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToSettings: () -> Unit,
    analyticsViewModel: AnalyticsViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val analyticsUiState by analyticsViewModel.analyticsUiState.collectAsState()

    TimelyScaffold(
        topBarTitle = stringResource(R.string.analytics),
        onHomeClick = navigateToHome,
        onCalendarClick = navigateToCalendar,
        onAnalyticsClick = {}
    ) { innerPadding->

        AnalyticsBody(
            analyticsUiState = analyticsUiState,
            updateTimePeriod = { period ->
                analyticsViewModel.updatePeriod(period)
            },
            modifier = modifier.padding(innerPadding)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsBody(
    analyticsUiState: AnalyticsUiState,
    updateTimePeriod: (AnalyticsTimePeriod) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
            horizontalArrangement = Arrangement.Center
        ) {
            val options = AnalyticsTimePeriod.entries

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                options.forEachIndexed {index, period ->
                    SegmentedButton(
                        selected = analyticsUiState.selectedPeriod == period,
                        onClick = {
                            updateTimePeriod(period)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        icon = {}
                    ) {
                        Text(
                            text = when(period){
                                AnalyticsTimePeriod.WEEKLY -> "Weekly"
                                AnalyticsTimePeriod.MONTHLY -> "Monthly"
                                AnalyticsTimePeriod.YEARLY -> "Yearly"
                            }
                        )
                    }
                }
            }
        }
        Text(
            text = formatLocalDateToAnalyticsRange(analyticsUiState.startDate, analyticsUiState.endDate),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.completed_goals_in_date_range) + " ${analyticsUiState.completedGoalCount}",
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        DisplayTime(millisToMinutes(analyticsUiState.totalCompletedMillis), stringResource(R.string.total_completed_time_in_date_range))
        DisplayTime(millisToMinutes(analyticsUiState.averageCompletedMillis), stringResource(R.string.average_completed_time_in_date_range))
        Text(
            text = stringResource(
                R.string.scheduled_time_utilized,
                analyticsUiState.scheduledTimeUtilization
            ),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        DonutChart(
            completedPercentage = analyticsUiState.completedPercentage,
            partialPercentage = analyticsUiState.partialPercentage,
            unfinishedPercentage = analyticsUiState.unfinishedPercentage
        )
        ColorLegend(
            items = listOf(
                LegendItem(
                    label = "Unfinished Time",
                    color = MaterialTheme.colorScheme.error
                ),
                LegendItem(
                    label = "Partially Completed Time",
                    color = MaterialTheme.colorScheme.primary
                ),
                LegendItem(
                    label = "Completed Time",
                    color = MaterialTheme.colorScheme.completedGoal
                )
            )
        )
    }
}

/*@Preview
@Composable
fun AnalyticsScreenPreview()
{
    TimeManagementAppTheme {
        AnalyticsScreen(
            navigateToHome = {},
            navigateToCalendar = {},
            navigateToSettings = {}
        )
    }
}*/

@Preview(
    showBackground = true,
    heightDp = 1000
)
@Composable
fun AnalyticsBodyPreview(){
    TimeManagementAppTheme {
        val completedMillis = 10 * 60 * 60_000L
        val completedGoals = 10
        val scheduledMillis = 11 * 60 * 60_000L
        AnalyticsBody(
            analyticsUiState = AnalyticsUiState(
                selectedPeriod = AnalyticsTimePeriod.YEARLY,
                startDate = LocalDate.now().withDayOfYear(1),
                endDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()),
                completedGoalCount = completedGoals,
                totalCompletedMillis = completedMillis,
                averageCompletedMillis = completedMillis / completedGoals,
                scheduledTimeUtilization = completedMillis.toDouble() / scheduledMillis * 100,
                completedPercentage = 30.0f,
                partialPercentage = 35.0f,
                unfinishedPercentage = 35.0f
            ),
            updateTimePeriod = {}
        )
    }
}