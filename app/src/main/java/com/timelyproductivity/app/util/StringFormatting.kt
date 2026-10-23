package com.timelyproductivity.app.util

fun String.enumNameToFirstLetterCapitalized(): String{
    return this.lowercase().replaceFirstChar { it.uppercase() }.replace("_", "-")
}