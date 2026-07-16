package com.example.timemanagementapp.util

import java.time.LocalDate
import java.time.YearMonth

/***
 * @param date LocalDate to be formatted for use on UI
 * @return String formatted as the mm/dd shorthand,
 * ex. (Local Date of 2026-07-15 -> String of "7/15")
 */
fun formatLocalDateToShorthandDate(date: LocalDate?): String {
    if(date == LocalDate.now()){
        return "Today's"
    }

    return date?.monthValue.toString() +
            "/" + date?.dayOfMonth.toString()
}

/***
 * @param date LocalDate to be formatted for use on UI
 * @return String formatted as Month Day, Year.
 * ex. (Local Date of 2026-07-15 -> String of "July 15, 2026")
 */
fun formatLocalDateToCalendarDate(date: LocalDate?): String {
    return date?.month.toString().lowercase().replaceFirstChar {it.uppercase()} + " " + date?.dayOfMonth.toString() + ", " + date?.year.toString()
}


/***
 * @param yearMonth YearMonth to be formatted for use on UI
 * @return String formatted as Month, Year
 * ex. (Year Month of 2026-07 -> String of "July 2026")
 */
fun formatYearMonthToMonthYearString(yearMonth: YearMonth?): String {
    return yearMonth?.month.toString().lowercase().replaceFirstChar {it.uppercase()} + " " + yearMonth?.year.toString()
}