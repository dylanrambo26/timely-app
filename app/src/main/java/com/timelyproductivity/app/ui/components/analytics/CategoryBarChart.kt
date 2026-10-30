package com.timelyproductivity.app.ui.components.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.data.analytics.CategoryActivity
import com.timelyproductivity.app.data.goal.category.CategoryColor
import com.timelyproductivity.app.data.testCategoryStatsSizeThree
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.toColor
import com.timelyproductivity.app.util.MILLIS_IN_MINUTE
import java.time.LocalDate
import java.util.Locale

@Composable
fun CategoryAnalyticsBarChart(
    categoryStats: List<CategoryActivity>,
    startDate: LocalDate,
    endDate: LocalDate,
    modifier: Modifier = Modifier
){
    val maxNumScheduledGoals = categoryStats.maxOfOrNull { it.numScheduledGoals } ?: 0
    var isExpanded by rememberSaveable(
        startDate, endDate
    ) {
        mutableStateOf(false)
    }

    val visibleCategories = if(isExpanded){
        categoryStats
    } else {
        categoryStats.take(5)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Completed Goals by Category",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Multicolored completed indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(CategoryColor.BLUE.toColor().copy(
                                alpha = 1.0f
                            ))
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(CategoryColor.GREEN.toColor().copy(
                                alpha = 1.0f
                            ))
                    )
                }

                Text(
                    text = "Completed (category color)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Incomplete indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(3.dp)
                        )
                )

                Text(
                    text = "Incomplete",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        HorizontalDivider()

        if (categoryStats.isEmpty()) {
            Text(
                text = "No category activity for this period.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else{
            visibleCategories.forEach { activity ->
                CategoryAnalyticsBar(
                    categoryName = activity.category?.name ?: "Uncategorized",
                    numCompletedGoals = activity.numCompleteGoals,
                    numScheduledGoals = activity.numScheduledGoals,
                    maxNumScheduledGoals = maxNumScheduledGoals,
                    categoryColor = activity.category?.color?.toColor()
                        ?: MaterialTheme.colorScheme.primary
                )
            }

            if (categoryStats.size > 5){
                TextButton(
                    onClick = {
                        isExpanded = !isExpanded
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        if(isExpanded){
                            "Show less"
                        } else {
                            "Show all (${categoryStats.size}"
                        }
                    )

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = if(isExpanded){
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = if(isExpanded){
                            "Show less categories"
                        } else {
                            "Show more categories"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryAnalyticsBar(
    categoryName: String,
    numCompletedGoals: Int,
    numScheduledGoals: Int,
    maxNumScheduledGoals: Int,
    categoryColor: Color,
    modifier: Modifier = Modifier
) {
    //val completed = numCompletedGoals.coerceIn(0L, scheduledMillis)
    //val incomplete = (scheduledMillis - completed).coerceAtLeast(0L)

    val completedFraction =
        if (maxNumScheduledGoals > 0) {
            numCompletedGoals.toFloat() / maxNumScheduledGoals
        } else {
            0f
        }

    val incompleteFraction =
        if (maxNumScheduledGoals > 0) {
            (numScheduledGoals - numCompletedGoals).toFloat() /
                    maxNumScheduledGoals
        } else {
            0f
        }

    val remainingFraction =
        (1f - completedFraction - incompleteFraction)
            .coerceAtLeast(0f)

    val completionPercentage =
        if (numScheduledGoals > 0f){
            numCompletedGoals.toFloat() / numScheduledGoals * 100f
        } else {
            0f
        }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "$numCompletedGoals / $numScheduledGoals" +
                        "· ${String.format(Locale.ROOT, "%.1f", completionPercentage)}%",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (completedFraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(completedFraction)
                        .background(categoryColor.copy(
                            alpha = 1.0f
                        ))
                )
            }

            if (incompleteFraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(incompleteFraction)
                        .background(MaterialTheme.colorScheme.errorContainer)
                )
            }

            if(remainingFraction > 0f){
                Spacer(
                    modifier = Modifier
                        .weight(remainingFraction)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryAnalyticsBarPreview(){
    TimeManagementAppTheme {
        CategoryAnalyticsBar(
            categoryName = "School",
            numCompletedGoals = 8,
            numScheduledGoals = 10,
            maxNumScheduledGoals = 10,
            categoryColor = CategoryColor.BLUE.toColor()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryAnalyticsBarChartPreview(){
    TimeManagementAppTheme {
        CategoryAnalyticsBarChart(
            categoryStats = testCategoryStatsSizeThree.sortedByDescending { it.numScheduledGoals },
            startDate = LocalDate.of(2026, 10, 1),
            endDate = LocalDate.of(2026, 10, 31)
        )
    }
}