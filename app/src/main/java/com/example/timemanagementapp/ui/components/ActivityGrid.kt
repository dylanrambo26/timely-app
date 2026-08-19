package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.generateTestDailyActivity
import com.example.timemanagementapp.ui.analytics.AnalyticsTimePeriod
import com.example.timemanagementapp.ui.analytics.DailyActivity
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.activityGridYellow
import com.example.timemanagementapp.ui.theme.completedGoal
import java.time.LocalDate

@Composable
fun ActivityGridWeeklyMonthly(
    dailyActivity: List<DailyActivity>,
    displayStartDate: LocalDate,
    displayEndDate: LocalDate,
    offset: Int,
){
    val activityByDate = dailyActivity.associateBy { it.date }
    val dates: List<LocalDate> =
        generateSequence(displayStartDate) { it.plusDays(1)}
            .takeWhile { !it.isAfter(displayEndDate) }
            .toList()

    val gridItems = List<LocalDate?>(offset) {null} + dates

    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeekdayLabels()

        gridItems.chunked(7).forEach { week ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                week.forEach { date ->
                    if (date != null){
                        val activity = activityByDate[date]
                        ActivityCell(
                            date = date,
                            completionPercentage = activity?.completionPercentage
                        )
                    } else {
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WeekdayLabels(
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        listOf("Su","M","Tu","W","Th","F","Sa").forEach { day ->
            Box(
                modifier = Modifier.width(20.dp),
                contentAlignment = Alignment.Center
            ){
                Text(day)
            }
        }
    }
}

@Composable
fun ActivityCell(
    date: LocalDate,
    completionPercentage: Float?,
    modifier: Modifier = Modifier
){
    val today = LocalDate.now()

    val borderColor = if(date == today){
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val cellColor = when{
        date.isAfter(today) ->
            MaterialTheme.colorScheme.surfaceVariant
        completionPercentage == null ->
            MaterialTheme.colorScheme.surfaceVariant

        completionPercentage < 39.9 -> MaterialTheme.colorScheme.error
        completionPercentage < 70.0 -> MaterialTheme.colorScheme.activityGridYellow
        else -> MaterialTheme.colorScheme.completedGoal
    }
    Box(
        modifier = modifier
            .size(20.dp)
            .background(
                color = cellColor,
                shape = RoundedCornerShape(3.dp)
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(3.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun ActivityGridWeeklyMonthlyPreview(){
    TimeManagementAppTheme {
        val today = LocalDate.now()
        val monthlyOffset = today.withDayOfMonth(1).dayOfWeek.value % 7
        ActivityGridWeeklyMonthly(
            dailyActivity = generateTestDailyActivity(
                timePeriod = AnalyticsTimePeriod.MONTHLY
            ),
            offset = monthlyOffset,
            displayStartDate =  today.withDayOfMonth(1),
            displayEndDate = today.withDayOfMonth(today.lengthOfMonth())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityCellPreview(){
    TimeManagementAppTheme {
        ActivityCell(
            date = LocalDate.of(2026, 8, 19),
            completionPercentage = 55.0f,
        )
    }
}