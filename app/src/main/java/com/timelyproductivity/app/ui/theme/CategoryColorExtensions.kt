package com.timelyproductivity.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.timelyproductivity.app.data.goal.category.CategoryColor

@Composable
fun CategoryColor.toColor(): Color {
    val isSystemInDarkTheme = isSystemInDarkTheme()

    return when(this){
        CategoryColor.RED ->
            if(isSystemInDarkTheme){
                categoryRedDark
            } else {
                categoryRedLight
            }
        CategoryColor.ORANGE ->
            if(isSystemInDarkTheme){
                categoryOrangeDark
            } else {
                categoryOrangeLight
            }
        CategoryColor.YELLOW ->
            if(isSystemInDarkTheme){
                categoryYellowDark
            } else {
                categoryYellowLight
            }
        CategoryColor.GREEN ->
            if(isSystemInDarkTheme){
                categoryGreenDark
            } else {
                categoryGreenLight
            }
        CategoryColor.BLUE ->
            if(isSystemInDarkTheme){
                categoryBlueDark
            } else {
                categoryBlueLight
            }
        CategoryColor.VIOLET ->
            if(isSystemInDarkTheme){
                categoryVioletDark
            } else {
                categoryVioletLight
            }
    }
}