package com.example.ics342app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ics342app.ui.theme.ICS342AppTheme
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICS342AppTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("SkyCast") }
                        )
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
    val viewModel: WeatherViewModel = viewModel()
    val weatherState by viewModel.weather.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeather("Saint Paul")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start // 🔄 Left align content
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

            Text("Condition: ${weather.weather.firstOrNull()?.main}", style = MaterialTheme.typography.bodyLarge)
            Text("Humidity: ${weather.main.humidity}%", style = MaterialTheme.typography.bodyLarge)
            Text("Pressure: ${weather.main.pressure} hPa", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("Loading weather...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


class WeatherViewModel : ViewModel() {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()

    private val service = RetrofitInstance.api

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = service.getWeather(city, "imperial", API_KEY)
                _weather.value = response
            } catch (e: Exception) {
                println("ERROR: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        const val API_KEY = "3d4e715a4658b370c46c5dfb1b11ce7e" // Replace with your real API key
    }
}

object RetrofitInstance {
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    val api: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(WeatherService::class.java)
    }
}

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}

@Serializable
data class WeatherResponse(
    val name: String,
    val weather: List<WeatherCondition>,
    val main: MainInfo
)

@Serializable
data class WeatherCondition(
    val main: String,
    val description: String
)

@Serializable
data class MainInfo(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val pressure: Int,
    val humidity: Int
)

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreen() {
    ICS342AppTheme {
        WeatherScreen()
    }
}