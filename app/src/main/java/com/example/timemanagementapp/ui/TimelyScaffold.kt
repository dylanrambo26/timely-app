package com.example.timemanagementapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar

@Composable
fun TimelyScaffold(
    topBarTitle: String,
    onHomeClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(topBarTitle)
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = onCalendarClick,
                onHomeClick = onHomeClick,
                onAnalyticsClick = onAnalyticsClick
            )
        },
        content = content
    )
}