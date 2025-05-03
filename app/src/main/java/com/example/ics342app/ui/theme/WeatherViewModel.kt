package com.example.ics342app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class WeatherViewModel(private val service: WeatherService = RetrofitInstance.api) : ViewModel() {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = service.getWeather(city, "imperial", API_KEY)
                _weather.value = response
            } catch (e: Exception) {
                println("❌ ERROR: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        const val API_KEY = "3d4e715a4658b370c46c5dfb1b11ce7e"
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