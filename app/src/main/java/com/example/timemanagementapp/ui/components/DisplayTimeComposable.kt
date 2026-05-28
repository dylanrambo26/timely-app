package com.example.timemanagementapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.timemanagementapp.R

@Composable
fun DisplayTime(duration: Int, title: String){
    Text(
        text = "$title ${duration / 60}h ${duration % 60}m",
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
    )
}