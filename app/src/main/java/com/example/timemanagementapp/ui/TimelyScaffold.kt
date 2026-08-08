package com.example.timemanagementapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.ui.calendar.CalendarBody

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