package com.timelyproductivity.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.timelyproductivity.app.R
import androidx.compose.ui.res.stringResource

@Composable
fun PermissionsDialog(
    onDismiss: () -> Unit,
    onContinue: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Allow task alerts?"
            )
        },
        text = {
            Text(
                text = stringResource(R.string.permissions_dialog_body)
            )
        },
        confirmButton = {
            Button(
                onClick = onContinue
            ) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Not now")
            }
        }
    )
}