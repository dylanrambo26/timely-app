package com.timelyproductivity.app.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.TableInfo
import com.timelyproductivity.app.R
import com.timelyproductivity.app.data.generateTestDailyActivity
import com.timelyproductivity.app.data.testAnalyticsUiState
import com.timelyproductivity.app.data.testCategoryStatsSizeThree
import com.timelyproductivity.app.ui.AppViewModelProvider
import com.timelyproductivity.app.ui.TimelyScaffold
import com.timelyproductivity.app.ui.components.ColorLegendColumn
import com.timelyproductivity.app.ui.components.LegendItem
import com.timelyproductivity.app.ui.components.analytics.ActivityGridWeeklyMonthly
import com.timelyproductivity.app.ui.components.analytics.ActivityGridYearly
import com.timelyproductivity.app.ui.components.analytics.AnalyticsSummary
import com.timelyproductivity.app.ui.components.analytics.CategoryAnalyticsBarChart
import com.timelyproductivity.app.ui.components.analytics.DonutChart
import com.timelyproductivity.app.ui.components.time.DisplayTime
import com.timelyproductivity.app.ui.navigation.NavigationDest
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.activityGridYellow
import com.timelyproductivity.app.ui.theme.completedGoal
import com.timelyproductivity.app.util.formatLocalDateToAnalyticsRange
import com.timelyproductivity.app.util.millisToMinutes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object AnalyticsDestination : NavigationDest {
    override val route = "analytics"
    override val titleRes = R.string.analytics
}

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
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
        val options = AnalyticsTimePeriod.entries

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
        Text(
            text = formatLocalDateToAnalyticsRange(analyticsUiState.startDate, analyticsUiState.endDate),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnalyticsSummary(
                uiState = analyticsUiState
            )
            HorizontalDivider()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Time Utilization",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.titleMedium
                )
                DonutChart(
                    completedPercentage = analyticsUiState.completedPercentage,
                    partialPercentage = analyticsUiState.partialPercentage,
                    unfinishedPercentage = analyticsUiState.unfinishedPercentage
                )
                ColorLegendColumn(
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
                    ),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }

            HorizontalDivider()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Completed Goals by Category",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.titleMedium
                )
                CategoryAnalyticsBarChart(
                    categoryStats = analyticsUiState.categoryStats,
                    startDate = analyticsUiState.startDate,
                    endDate = analyticsUiState.endDate
                )
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daily Activity",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.titleMedium
                )

                if(analyticsUiState.selectedPeriod != AnalyticsTimePeriod.YEARLY){

                    ActivityGridWeeklyMonthly(
                        dailyActivity = analyticsUiState.dailyActivity,
                        offset = analyticsUiState.gridOffset,
                        displayStartDate = analyticsUiState.startDate,
                        displayEndDate = analyticsUiState.endDate
                    )
                }
                else{
                    ActivityGridYearly(
                        dailyActivity = analyticsUiState.dailyActivity,
                        year = analyticsUiState.startDate.year,
                    )
                }

                ColorLegendColumn(
                    items = listOf(
                        LegendItem(
                            label = "70%-100% Completed Time",
                            color = MaterialTheme.colorScheme.completedGoal
                        ),
                        LegendItem(
                            label = "40%-69% Completed Time",
                            color = MaterialTheme.colorScheme.activityGridYellow
                        ),
                        LegendItem(
                            label = "Under 40% Completed Time",
                            color = MaterialTheme.colorScheme.error
                        ),
                        LegendItem(
                            label = "Today",
                            color = Color.Transparent,
                            borderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 1400,
)
@Composable
fun AnalyticsBodyPreview(){
    TimeManagementAppTheme {
        AnalyticsBody(
            analyticsUiState = testAnalyticsUiState,
            updateTimePeriod = {}
        )
    }
}