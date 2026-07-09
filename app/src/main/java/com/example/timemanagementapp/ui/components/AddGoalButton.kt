package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R

@Composable
fun AddGoalButton(
    modifier: Modifier = Modifier,
    onAddGoal: () -> Unit,
    text: String = ""
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(
            onClick = onAddGoal,
            modifier = Modifier
                .size(100.dp),
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = text,
                modifier = Modifier
                    .size(100.dp)
            )
        }
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}