package com.timelyproductivity.app.util

fun String.enumNameToFirstLetterCapitalized(): String{
    return this.lowercase().replaceFirstChar { it.uppercase() }.replace("_", "-")
}

fun Long.formatMillisToDurationString(): String{
    val minutes = millisToMinutes(this)

    return "${minutes / 60}h ${minutes % 60}m"
}