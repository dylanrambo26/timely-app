package com.example.timemanagementapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val ColorScheme.completedGoal: Color
    get() = if(surface.luminance() > 0.5f) CompletedGoalLight else CompletedGoalDark

val ColorScheme.selectedDate: Color
    get() = if(surface.luminance() > 0.5f) selectedDateLight else selectedDateDark

val ColorScheme.checkbox: Color
    get() = if(surface.luminance() > 0.5f) CheckboxLight else CheckboxDark

val ColorScheme.activityGridYellow: Color
    get() = if(surface.luminance() > 0.5f)activityGridYellowLight else activityGridYellowDark