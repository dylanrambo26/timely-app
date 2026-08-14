package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import com.example.timemanagementapp.ui.theme.completedGoal
import com.example.timemanagementapp.util.MIN_PERCENTAGE_FOR_LABEL
import kotlin.math.cos
import kotlin.math.sin

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
    val innerOutlineColor = MaterialTheme.colorScheme.onSurface

    val textMeasurer = rememberTextMeasurer()
    val percentageTextStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )

    Canvas(
        modifier = Modifier.size(250.dp)
    ){
        var startAngle = -90f

        val strokeWidth = 30.dp.toPx()
        val inset = strokeWidth / 2f
        
        val outerArcSize = Size(
            width = size.width - strokeWidth,
            height = size.height - strokeWidth
        )

        val center = Offset(size.width / 2f, size.height / 2f)
        val innerRadius = (size.minDimension - strokeWidth) / 2f - strokeWidth / 2f
        val outlineWidth = 2.dp.toPx()

        drawArc(
            startAngle = startAngle,
            sweepAngle = completedSweepAngle,
            color = completedColor,
            topLeft = Offset(inset, inset),
            size = outerArcSize,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        startAngle += completedSweepAngle

        drawArc(
            startAngle = startAngle,
            sweepAngle = partialSweepAngle,
            color = partialColor,
            topLeft = Offset(inset, inset),
            size = outerArcSize,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        startAngle += partialSweepAngle

        drawArc(
            startAngle = startAngle,
            sweepAngle = unfinishedSweepAngle,
            color = unfinishedColor,
            topLeft = Offset(inset, inset),
            size = outerArcSize,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        drawCircle(
            color = innerOutlineColor,
            radius = innerRadius,
            center = center,
            style = Stroke(width = outlineWidth)
        )

        startAngle = -90f

        drawDivider(
            angle = startAngle,
            center = center,
            radius = innerRadius,
            color = innerOutlineColor,
            strokeWidth = outlineWidth
        )

        startAngle += completedSweepAngle

        drawDivider(
            angle = startAngle,
            center = center,
            radius = innerRadius,
            color = innerOutlineColor,
            strokeWidth = outlineWidth
        )

        startAngle += partialSweepAngle

        drawDivider(
            angle = startAngle,
            center = center,
            radius = innerRadius,
            color = innerOutlineColor,
            strokeWidth = outlineWidth
        )

        startAngle = -90f

        val textRadius = innerRadius * 0.6f
        val completedTextPosition = percentagePosition(center, startAngle + completedSweepAngle / 2f, textRadius)
        val partialTextPosition = percentagePosition(center, startAngle + completedSweepAngle + partialSweepAngle / 2f, textRadius)
        val unfinishedTextPosition = percentagePosition(center, startAngle + completedSweepAngle + partialSweepAngle + unfinishedSweepAngle / 2f, textRadius)



        val completedTextLayout = textMeasurer.measure(
            text = "${String.format("%.1f", completedPercentage)}%",
            style = percentageTextStyle
        )

        val partialTextLayout = textMeasurer.measure(
            text = "${String.format("%.1f", partialPercentage)}%",
            style = percentageTextStyle
        )

        val unfinishedTextLayout = textMeasurer.measure(
            text = "${String.format("%.1f", unfinishedPercentage)}%",
            style = percentageTextStyle
        )

        if(completedPercentage >= MIN_PERCENTAGE_FOR_LABEL){
            drawText(
                textLayoutResult = completedTextLayout,
                topLeft = Offset(
                    completedTextPosition.x - completedTextLayout.size.width / 2f,
                    completedTextPosition.y - completedTextLayout.size.height / 2f,
                )
            )
        }

        if(partialPercentage >= MIN_PERCENTAGE_FOR_LABEL){
            drawText(
                textLayoutResult = partialTextLayout,
                topLeft = Offset(
                    partialTextPosition.x - partialTextLayout.size.width / 2f,
                    partialTextPosition.y - partialTextLayout.size.height / 2f,
                )
            )
        }

        if(unfinishedPercentage >= MIN_PERCENTAGE_FOR_LABEL){
            drawText(
                textLayoutResult = unfinishedTextLayout,
                topLeft = Offset(
                    unfinishedTextPosition.x - unfinishedTextLayout.size.width / 2f,
                    unfinishedTextPosition.y - unfinishedTextLayout.size.height / 2f,
                )
            )
        }
    }
}

fun DrawScope.drawDivider(
    angle: Float,
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float
){
    val radians = Math.toRadians(angle.toDouble())

    val end = Offset(
        x = center.x + cos(radians).toFloat() * radius,
        y = center.y + sin(radians).toFloat() * radius,
    )

    drawLine(
        color = color,
        start = center,
        end = end,
        strokeWidth = strokeWidth
    )
}

fun percentagePosition(
    center: Offset,
    angle: Float,
    radius: Float
): Offset{
    val radians = Math.toRadians(angle.toDouble())

    return Offset(
        x = center.x + cos(radians).toFloat() * radius,
        y = center.y + sin(radians).toFloat() * radius
    )
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