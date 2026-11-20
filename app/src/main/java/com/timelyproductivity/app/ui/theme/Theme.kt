package com.timelyproductivity.app.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB4C5FF),
    onPrimary = Color(0xFF172E60),

    primaryContainer = Color(0xFF28509C),
    onPrimaryContainer = Color(0xFFDCE4FF),

    secondary = Color(0xFFBEC7DC),
    onSecondary = Color(0xFF283143),

    secondaryContainer = Color(0xFF404A60),
    onSecondaryContainer = Color(0xFFD9E2FF),

    tertiary = Color(0xFFD6BDE5),

    background = Color(0xFF111115),
    onBackground = Color(0xFFE3E2E9),

    surface = Color(0xFF1C1D22),
    onSurface = Color(0xFFE3E2E9),

    surfaceVariant = Color(0xFF44464F),
    onSurfaceVariant = Color(0xFFC4C6D0),

    outline = Color(0xFF8E909A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF28509C),
    onPrimary = Color(0xFFFFFFFF),

    primaryContainer = Color(0xFFD9E3FF),
    onPrimaryContainer = Color(0xFF102C63),

    secondary = Color(0xFF526181),
    onSecondary = Color(0xFFFFFFFF),

    secondaryContainer = Color(0xFFDCE3F5),
    onSecondaryContainer = Color(0xFF35435F),

    tertiary = Color(0xFF705581),
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFFFAFAFF),
    onBackground = Color(0xFF191B22),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191B22),

    surfaceVariant = Color(0xFFE1E4EF),
    onSurfaceVariant = Color(0xFF444750),

    outline = Color(0xFF747783)
)

@Composable
fun TimeManagementAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ColorPreview(){
    TimeManagementAppTheme(
        darkTheme = false
    ) {
        Column{
            ColorItem("Primary", MaterialTheme.colorScheme.primary)
            ColorItem("Secondary", MaterialTheme.colorScheme.secondary)
            ColorItem("Surface", MaterialTheme.colorScheme.surface)
            ColorItem("Background", MaterialTheme.colorScheme.background)
            ColorItem("Primary Container", MaterialTheme.colorScheme.primaryContainer)
            ColorItem("Secondary Container", MaterialTheme.colorScheme.secondaryContainer)
        }
    }
}

@Composable
fun ColorItem(name: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}