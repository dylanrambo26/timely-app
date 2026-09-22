package com.timelyproductivity.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.timelyproductivity.app.TimelyBottomAppBar
import com.timelyproductivity.app.TimelySmallTopAppBar

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