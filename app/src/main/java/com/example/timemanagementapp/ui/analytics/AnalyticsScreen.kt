package com.example.timemanagementapp.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.TimelyScaffold
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.ui.AppViewModelProvider

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
        modifier = modifier.fillMaxSize()
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
            text = stringResource(R.string.completed_goals_in_date_range) + " ${analyticsUiState.completedGoalCount}",
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        Text(
            text = stringResource(R.string.total_completed_time_in_date_range) + " ${analyticsUiState.totalCompletedMillis}",
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
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

@Preview(showBackground = true)
@Composable
fun AnalyticsBodyPreview(){
    TimeManagementAppTheme {
        AnalyticsBody(
            analyticsUiState = AnalyticsUiState(
                selectedPeriod = AnalyticsTimePeriod.MONTHLY,
            ),
            updateTimePeriod = {}
        )
    }
}