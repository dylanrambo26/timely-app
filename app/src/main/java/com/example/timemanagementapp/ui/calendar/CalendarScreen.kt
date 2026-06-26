package com.example.timemanagementapp.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.util.TableInfo
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

object CalendarDestination : NavigationDest {
    override val route = "calendar"
    override val titleRes = R.string.calendar
}

@Composable
fun CalendarScreen(
    navigateToHome: () -> Unit,
    navigateToAnalytics: () -> Unit, //TODO
){
    Scaffold(
        topBar = {
            TimelySmallTopAppBar(stringResource(R.string.calendar))
        },
        bottomBar = {
            TimelyBottomAppBar(
                onCalendarClick = {},
                onHomeClick = navigateToHome,
                onAnalyticsClick = navigateToAnalytics
            )
        }
    ) {
        innerPadding ->
        CalendarBody(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CalendarBody(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.calendar),
            textAlign = TextAlign.Center
        )
    }

}

@Preview(showBackground = true)
@Composable
fun CalendarBodyPreview(){
    TimeManagementAppTheme {
        CalendarBody()
    }
}