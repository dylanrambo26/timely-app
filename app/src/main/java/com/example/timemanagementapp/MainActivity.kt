package com.example.timemanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeManagementAppTheme {
                TimelyApp()
            }
        }
    }
}

@Composable
fun TimelyAppTextAndImages(
    modifier: Modifier = Modifier
) {
    ScaffoldUI(
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldUI(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Welcome, User",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(
                        onClick = ({ /*TODO*/ })) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings Button"
                        )
                    }
                }

            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        IconButton(
                            onClick = ({ /*TODO*/ })) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Settings Button",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(64.dp))
                        IconButton(
                            onClick = ({ /*TODO*/ })) {
                            Icon(
                                imageVector = Icons.Outlined.Home,
                                contentDescription = "Settings Button",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(64.dp))
                        IconButton(
                            onClick = ({ /*TODO*/ })) {
                            Image(
                                painter = painterResource(id = R.drawable.graph_image),
                                contentDescription = "Settings Button",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }
            )
        }
    ) {
        innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                    text= "\nGoal1\nGoal2\nGoal3\netc.\n"
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
                    IconButton (
                        onClick = {/*TODO*/},
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
}

@Preview(showBackground = true)
@Composable
fun TimelyApp() {
    TimeManagementAppTheme {
        TimelyAppTextAndImages(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
        )
    }
}