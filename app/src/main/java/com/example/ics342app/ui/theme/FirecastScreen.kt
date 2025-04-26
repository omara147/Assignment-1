package com.example.ics342app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(zip: String) {
    val viewModel: ForecastViewModel = viewModel()
    val forecastList by viewModel.forecast.collectAsState()

    LaunchedEffect(zip) {
        viewModel.fetchForecast(zip)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("16-Day Forecast") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("ZIP Code: $zip", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (forecastList.isEmpty()) {
                Text("Loading forecast...")
            } else {
                LazyColumn {
                    items(forecastList) { day ->
                        ForecastItem(day)
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(item: ForecastDay) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Date: ${item.date}", style = MaterialTheme.typography.bodyLarge)
        Text("Temp: ${item.temp}°F", style = MaterialTheme.typography.bodyMedium)
        Text("Condition: ${item.condition}", style = MaterialTheme.typography.bodyMedium)
    }
}