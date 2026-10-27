package com.timelyproductivity.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme
import com.timelyproductivity.app.ui.theme.completedGoal

@Composable
fun ColorLegendColumn(
    items: List<LegendItem>,
    isCircleShape: Boolean = false,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items.forEach {item ->
            ColorLegendItem(
                item = item,
                isCircleShape = isCircleShape
            )
        }
    }

}

@Composable
fun ColorLegendRow(
    items: List<LegendItem>,
    isCircleShape: Boolean = false,
    modifier: Modifier = Modifier
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items.forEach {item ->
            ColorLegendItem(
                item = item,
                isCircleShape = isCircleShape
            )
        }
    }
}

@Composable
fun ColorLegendItem(
    item: LegendItem,
    isCircleShape: Boolean = false
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = item.color,
                    shape = if(isCircleShape) CircleShape else RectangleShape
                )
                .border(
                    width = 3.dp,
                    color = item.borderColor
                )
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

data class LegendItem(
    val label: String,
    val color: Color,
    val borderColor: Color = Color.Transparent
)

@Preview(showBackground = true)
@Composable
fun ColorLegendPreview(){
    TimeManagementAppTheme {
        ColorLegendRow(
            items = listOf(
                LegendItem(
                    label = "= Test1",
                    color = MaterialTheme.colorScheme.completedGoal,
                    borderColor = MaterialTheme.colorScheme.primary
                ),
                LegendItem(
                    label = "= Test2",
                    color = MaterialTheme.colorScheme.primary,
                )
            )
        )
    }
}