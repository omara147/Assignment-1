package com.example.ics342app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ics342app.ui.theme.ICS342AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICS342AppTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            ZipEntryScreen(navController)
        }
        composable("forecast/{zip}") { backStackEntry ->
            val zip = backStackEntry.arguments?.getString("zip") ?: ""
            ForecastScreen(zip = zip)
        }
        composable("weather") {
            WeatherScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZipEntryScreen(navController: NavHostController) {
    var zipCode by remember { mutableStateOf(TextFieldValue("")) }
    var isValid by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SkyCast") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = zipCode,
                onValueChange = {
                    zipCode = it
                    isValid = it.text.length == 5 && it.text.all { char -> char.isDigit() }
                },
                label = { Text("Enter ZIP Code") },
                isError = !isValid,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (zipCode.text.length == 5 && zipCode.text.all { it.isDigit() }) {
                        navController.navigate("forecast/${zipCode.text}")
                    } else {
                        isValid = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Forecast")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("weather") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("See Current Weather")
            }
        }
    }
}