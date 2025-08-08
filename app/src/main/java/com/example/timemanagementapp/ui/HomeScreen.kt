package com.example.timemanagementapp.ui


import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.TestData
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

/**
 * @param goalsText - The list of current goals from the uiState in a joined String
 * @param onAddButtonClicked - function that will navigate to Add Log screen when clicked
 * @param modifier
 */
@Composable
fun HomeScreen(
    goalsText: String,
    onAddButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
){
    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text= "Today's Goals:",
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(8.dp),
            )
            Spacer(modifier = Modifier.width(64.dp))
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text= goalsText
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 4.dp
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
            ){
                //Add Log Button
                IconButton (
                    onClick = onAddButtonClicked,
                        modifier = Modifier.size(100.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.add_log_button),
                                contentDescription = stringResource(R.string.add_log_button_desc),
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Manually Enter Log",
                            textAlign = TextAlign.Center
                        )
            }

        }
    }
}

//Preview the Home Screen
@Preview
@Composable
fun HomeScreenPreview(){
    TimeManagementAppTheme {
        HomeScreen(
            TestData.goalText,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}