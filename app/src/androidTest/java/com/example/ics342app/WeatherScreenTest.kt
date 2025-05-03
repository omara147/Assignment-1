package com.example.ics342app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    onZipChange: (String) -> Unit = {},
    onForecastClick: () -> Unit = {},
    onWeatherClick: () -> Unit = {},
    onLocationClick: () -> Unit = {}
) {
    var zipCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = zipCode,
            onValueChange = {
                zipCode = it
                onZipChange(it)
            },
            label = { Text("Enter ZIP Code") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ZipInput")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onForecastClick() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("GetForecastButton")
        ) {
            Text("Get Forecast")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onWeatherClick() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("GetWeatherButton")
        ) {
            Text("See Current Weather")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onLocationClick() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("UseLocationButton")
        ) {
            Text("Use My Location")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherScreen()
}