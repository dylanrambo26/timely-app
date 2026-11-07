package com.timelyproductivity.app.ui.components.buttons

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.timelyproductivity.app.R

@Composable
fun TwoActionButtonRow(
    onOutlinedButtonClicked: () -> Unit,
    onButtonClicked: () -> Unit,
    outlinedButtonText: String,
    outlinedButtonFontSize: TextUnit = TextUnit.Unspecified,
    buttonText: String,
    buttonEnabled: Boolean = true,
    buttonIcon: ImageVector? = null,
    buttonDescription: String? = null,
    buttonFontSize: TextUnit = TextUnit.Unspecified,

){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onOutlinedButtonClicked,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = outlinedButtonText,
                fontSize = outlinedButtonFontSize,
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onButtonClicked,
            enabled = buttonEnabled,
            modifier = Modifier.weight(1f)
        ) {
            if(buttonIcon != null){
                Icon(buttonIcon, contentDescription = buttonDescription)
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = buttonText,
                fontSize = buttonFontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}