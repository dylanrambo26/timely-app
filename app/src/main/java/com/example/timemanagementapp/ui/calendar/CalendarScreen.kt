package com.example.timemanagementapp.ui.calendar

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.TimelyBottomAppBar
import com.example.timemanagementapp.TimelySmallTopAppBar
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme
import java.time.LocalDate
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.PreviewLightDark
import java.time.YearMonth

object CalendarDestination : NavigationDest {
    override val route = "calendar"
    override val titleRes = R.string.calendar
}

@Composable
fun CalendarScreen(
    navigateToHome: () -> Unit,
    navigateToAnalytics: () -> Unit, //TODO
    viewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
            displayedMonth = viewModel.displayedMonth,
            calendarCells = viewModel.calendarCells,
            selectedDate = viewModel.selectedDate,
            onPrevMonth = viewModel::previousMonth,
            onNextMonth = viewModel::nextMonth,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CalendarBody(
    calendarCells: List<LocalDate?> = emptyList(),
    selectedDate: LocalDate = LocalDate.now(),
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    displayedMonth: YearMonth = YearMonth.now(),
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeader(
            displayedMonth = displayedMonth,
            modifier = modifier
        )
        WeekdayHeader(
            modifier = modifier
        )
        CalendarGrid(
            calendarCells = calendarCells,
            selectedDate = selectedDate,
            onDateSelected = {}
        )
    }
}

@Composable
fun CalendarCell(
    date: LocalDate?,
    selectedDate: LocalDate?,
    onClick: (LocalDate) -> Unit,
){
    val cellColor = if (date == null) {
        MaterialTheme.colorScheme.secondaryContainer
    } else if (date == selectedDate) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            .background(cellColor)
            .clickable(enabled = date != null) {
                date?.let(onClick)
            },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(date.dayOfMonth.toString())
        }
    }
}

@Composable
fun CalendarGrid(
    calendarCells: List<LocalDate?>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(7)
    ) {
        items(calendarCells){ date ->
            CalendarCell(
                date = date,
                selectedDate = selectedDate,
                onClick = onDateSelected
            )
        }
    }
}

@Composable
fun WeekdayHeader(
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
    ){
        listOf("Su","Mo","Tu","We","Th","Fr","Sa").forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ){
                Text(day)
            }
        }
    }
}

@Composable
fun CalendarHeader(
    displayedMonth: YearMonth,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = {/*TODO Previous Month Button*/}
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_month)
            )
        }

        Text(
            text = displayedMonth.month.toString().lowercase().replaceFirstChar {it.uppercase()} + " " + displayedMonth.year.toString(),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = {/*TODO Next Month Button*/}
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.next_month)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarBodyPreview(){
    TimeManagementAppTheme {
        val viewModel = CalendarViewModel()
        CalendarBody(
            calendarCells = viewModel.generateCalendarCells(YearMonth.now()),
            selectedDate = LocalDate.now(),
            onPrevMonth = {},
            onNextMonth = {}
        )
    }
}