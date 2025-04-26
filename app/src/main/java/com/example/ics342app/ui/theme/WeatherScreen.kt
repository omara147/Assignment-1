package com.example.ics342app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen() {
    val viewModel: WeatherViewModel = viewModel()
    val weatherState by viewModel.weather.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeather("Saint Paul")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Current Weather") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (weatherState != null) {
                val weather = weatherState!!

                Text(
                    text = "Location: ${weather.name}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${weather.main.temp}°F",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = "Feels like: ${weather.main.feelsLike}°F",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.sunny),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Condition: ${weather.weather.firstOrNull()?.main}")
                Text("Humidity: ${weather.main.humidity}%")
                Text("Pressure: ${weather.main.pressure} hPa")
            } else {
                Text("Loading weather...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}