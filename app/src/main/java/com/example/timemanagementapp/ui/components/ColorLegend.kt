package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.room.util.TableInfo
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.completedGoal

@Composable
fun ColorLegend(
    items: List<LegendItem>,
    isCircleShape: Boolean = false,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items.forEach {item ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = item.color,
                            shape = if(isCircleShape) CircleShape else RectangleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item.label)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }

}

data class LegendItem(
    val label: String,
    val color: Color
)

@Preview(showBackground = true)
@Composable
fun ColorLegendPreview(){
    TimeManagementAppTheme {
        ColorLegend(
            items = listOf(
                LegendItem(
                    label = "= Test1",
                    color = MaterialTheme.colorScheme.completedGoal
                ),
                LegendItem(
                    label = "= Test2",
                    color = MaterialTheme.colorScheme.primary
                )
            )
        )
    }
}