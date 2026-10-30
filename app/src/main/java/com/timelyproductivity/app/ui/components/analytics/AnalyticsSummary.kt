package com.timelyproductivity.app.ui.components.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.TimeUtils.formatDuration
import com.timelyproductivity.app.data.testAnalyticsUiState
import com.timelyproductivity.app.ui.analytics.AnalyticsUiState
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.util.formatMillisToDurationString
import java.util.Locale

@Composable
fun AnalyticsSummary(
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalyticsStatCard(
                title = "Goals completed",
                value = uiState.completedGoalCount.toString(),
                modifier = Modifier.weight(1f)
            )

            AnalyticsStatCard(
                title = "Total tracked time",
                value = uiState.totalCompletedMillis.formatMillisToDurationString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalyticsStatCard(
                title = "Average time per goal",
                value = uiState.averageCompletedMillis.formatMillisToDurationString(),
                modifier = Modifier.weight(1f)
            )

            AnalyticsStatCard(
                title = "Scheduled time utilized",
                value = String.format(
                    Locale.ROOT,
                    "%.1f%%",
                    uiState.scheduledTimeUtilization
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun AnalyticsSummaryPreview(){
    TimeManagementAppTheme {
        AnalyticsSummary(
            uiState = testAnalyticsUiState
        )
    }
}