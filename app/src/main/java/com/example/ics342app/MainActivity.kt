package com.example.ics342app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ics342app.ui.theme.ICS342AppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICS342AppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("SkyCast") })
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WeatherScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "St. Paul, MN",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "72°",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "Feels like 78°",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Image(
                painter = painterResource(id = R.drawable.sunny),
                contentDescription = "Sunny icon",
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Low 65°", style = MaterialTheme.typography.bodyLarge)
        Text(text = "High 80°", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Humidity 100%", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Pressure 1023 hPa", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreen() {
    ICS342AppTheme {
        WeatherScreen()
    }
}