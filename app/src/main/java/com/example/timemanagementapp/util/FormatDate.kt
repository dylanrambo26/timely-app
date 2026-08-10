package com.example.timemanagementapp.util

import androidx.compose.ui.text.TextStyle
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shorthandDateFormatter = DateTimeFormatter.ofPattern("M/d")

private val calendarDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

/***
 * @param date LocalDate to be formatted for use on UI
 * @return String formatted as the mm/dd shorthand,
 * ex. (Local Date of 2026-07-15 -> String of "7/15")
 */
fun formatLocalDateToShorthandDate(date: LocalDate): String {
    if (date == LocalDate.now()){
        return "Today's"
    }

    return date.format(shorthandDateFormatter)
}

/***
 * @param date LocalDate to be formatted for use on UI
 * @return String formatted as Month Day, Year.
 * ex. (Local Date of 2026-07-15 -> String of "July 15, 2026")
 */
fun formatLocalDateToCalendarDate(date: LocalDate): String {
    return date.format(calendarDateFormatter)
}


/***
 * @param yearMonth YearMonth to be formatted for use on UI
 * @return String formatted as Month, Year
 * ex. (Year Month of 2026-07 -> String of "July 2026")
 */
fun formatYearMonthToMonthYearString(yearMonth: YearMonth): String {
    return yearMonth.format(monthYearFormatter)
}

fun formatLocalDateToAnalyticsRange(startDate: LocalDate, endDate: LocalDate): String {
    val startDateFormatter = DateTimeFormatter.ofPattern("MMM d")
    val endDateFormatter = DateTimeFormatter.ofPattern("MMM d yyyy")

    return "${startDate.format(startDateFormatter)} - ${endDate.format(endDateFormatter)}"
}