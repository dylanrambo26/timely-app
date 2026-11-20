package com.timelyproductivity.app.ui.components.time

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.timelyproductivity.app.ui.theme.TimeManagementAppTheme

@Composable
fun DisplayTime(
    duration: Int,
    title: String = "",

    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    color: Color = LocalContentColor.current,
    useParentheses: Boolean = false
){
    Text(
        text = if (!useParentheses){
            "$title ${duration / 60}h ${duration % 60}m"
        } else{
            "(${duration / 60}h ${duration % 60}m)"
        },
        style = style,
        modifier = modifier,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
fun DisplayTimePreview(){
    TimeManagementAppTheme {
        DisplayTime(
            duration = 0,
            title = "Task: "
        )
    }
}