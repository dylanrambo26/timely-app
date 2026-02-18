package com.example.timemanagementapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.timemanagementapp.ui.navigation.TimelyNavHost

@Composable
fun TimelyApp(navController: NavHostController = rememberNavController()){
    TimelyNavHost(navController = navController)
}

//TODO Add Scaffolds to each screen to make bottom bar and top bar persist
@Composable
fun TimelyBottomAppBar(
    modifier: Modifier = Modifier
){
    BottomAppBar(
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                //Calendar Button
                IconButton(
                    onClick = ({/*TODO*/ })) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Calendar Button",
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(64.dp))

                //Home Button
                IconButton(
                    onClick = {/*navController.navigate(TimelyScreen.Home.name)*/}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Home Page Button",
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(64.dp))

                //Analytics Page Button
                IconButton(
                    onClick = ({ /*TODO*/ })
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.graph_image),
                        contentDescription = "Analytics Button",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    )
}

//Custom Top App Bar that mimics a SmallTopAppBar, displays the param title
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelySmallTopAppBar(
    title: String
){
    TopAppBar(
        modifier = Modifier.height(dimensionResource(R.dimen.top_app_bar_small_height)),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
}