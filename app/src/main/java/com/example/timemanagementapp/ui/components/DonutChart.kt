package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.completedGoal

@Composable
fun DonutChart(
    completedPercentage: Float,
    partialPercentage: Float,
    unfinishedPercentage: Float
){
    val completedSweepAngle = completedPercentage / 100f * 360f
    val partialSweepAngle = partialPercentage / 100f * 360f
    val unfinishedSweepAngle = unfinishedPercentage / 100f * 360f

    val completedColor = MaterialTheme.colorScheme.completedGoal //green
    val partialColor = MaterialTheme.colorScheme.primary  //blue
    val unfinishedColor = MaterialTheme.colorScheme.error //red

    Canvas(
        modifier = Modifier.size(250.dp)
    ){
        var startAngle = -90f

        val strokeWidth = 30.dp.toPx()
        val inset = strokeWidth / 2f
        
        val arcSize = Size(
            width = size.width - strokeWidth,
            height = size.height - strokeWidth
        )

        drawArc(
            startAngle = startAngle,
            sweepAngle = completedSweepAngle,
            color = completedColor,
            topLeft = Offset(inset, inset),
            size = arcSize,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        startAngle += completedSweepAngle

        drawArc(
            startAngle = startAngle,
            sweepAngle = partialSweepAngle,
            color = partialColor,
            topLeft = Offset(inset, inset),
            size = arcSize,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        startAngle += partialSweepAngle

        drawArc(
            startAngle = startAngle,
            sweepAngle = unfinishedSweepAngle,
            color = unfinishedColor,
            topLeft = Offset(inset, inset),
            size = arcSize,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview(){
    TimeManagementAppTheme {
        DonutChart(
            completedPercentage = 30.0f,
            partialPercentage = 35.0f,
            unfinishedPercentage = 35.0f
        )
    }
}