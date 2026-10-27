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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.util.Locale

@Composable
fun CategoryAnalyticsBarChart(
    categoryStats: List<CategoryActivity>,
    modifier: Modifier = Modifier
){
    val maxScheduledMillis = categoryStats.maxOfOrNull { it.scheduledMillis } ?: 0L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Time by Category",
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
        } else {
            categoryStats.forEach { activity ->
                CategoryAnalyticsBar(
                    categoryName = activity.category?.name ?: "Uncategorized",
                    completedMillis = activity.completedMillis,
                    scheduledMillis = activity.scheduledMillis,
                    maxScheduledMillis = maxScheduledMillis,
                    categoryColor = activity.category?.color?.toColor()
                        ?: MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CategoryAnalyticsBar(
    categoryName: String,
    completedMillis: Long,
    scheduledMillis: Long,
    maxScheduledMillis: Long,
    categoryColor: Color,
    modifier: Modifier = Modifier
) {
    val completed = completedMillis.coerceIn(0L, scheduledMillis)
    val incomplete = (scheduledMillis - completed).coerceAtLeast(0L)

    val completedFraction =
        if (maxScheduledMillis > 0L) {
            completed.toFloat() / maxScheduledMillis
        } else 0f

    val incompleteFraction =
        if (maxScheduledMillis > 0L) {
            incomplete.toFloat() / maxScheduledMillis
        } else 0f

    val remainingFraction = (1f - completedFraction - incompleteFraction).coerceAtLeast(0f)

    val completionPercentage =
        if (scheduledMillis > 0f){
            completed.toFloat() / scheduledMillis * 100f
        } else {
            0f
        }

    val totalCompletedMinutes = completed / MILLIS_IN_MINUTE
    val completedHours = totalCompletedMinutes / 60
    val completedMinutes = totalCompletedMinutes % 60

    val totalScheduledMinutes = scheduledMillis.coerceIn(0L, scheduledMillis) / MILLIS_IN_MINUTE
    val scheduledHours = totalScheduledMinutes / 60
    val scheduledMinutes = totalScheduledMinutes % 60

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
                text = "${completedHours}h ${completedMinutes}m " +
                        "/ ${scheduledHours}h ${scheduledMinutes}m " +
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
            completedMillis = (60_000 * 60 * 4) + (MILLIS_IN_MINUTE * 5),
            scheduledMillis = 60_000 * 60 * 5,
            maxScheduledMillis = 60_000 * 60 * 5,
            categoryColor = CategoryColor.BLUE.toColor()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryAnalyticsBarChartPreview(){
    TimeManagementAppTheme {
        CategoryAnalyticsBarChart(
            categoryStats = testCategoryStatsSizeThree.sortedByDescending { it.scheduledMillis },
        )
    }
}