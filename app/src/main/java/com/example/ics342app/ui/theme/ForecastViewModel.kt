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
import java.text.SimpleDateFormat
import java.util.*

class ForecastViewModel(
    private val service: ForecastApi = Retrofit.Builder()
        .baseUrl("https://pro.openweathermap.org/")
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ForecastApi::class.java)
) : ViewModel() {

    private val _forecast = MutableStateFlow<List<ForecastDay>>(emptyList())
    val forecast: StateFlow<List<ForecastDay>> = _forecast.asStateFlow()

    fun fetchForecast(zip: String) {
        viewModelScope.launch {
            try {
                val response = service.getForecast(zip, "imperial", API_KEY)
                val daily = response.list.map {
                    ForecastDay(
                        date = convertUnixToDate(it.timestamp),
                        temp = it.temp.day,
                        condition = it.weather.firstOrNull()?.main ?: ""
                    )
                }
                _forecast.value = daily
            } catch (e: Exception) {
                println("❌ Forecast error: ${e.localizedMessage}")
            }
        }
    }

    private fun convertUnixToDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }

    companion object {
        const val API_KEY = "3d4e715a4658b370c46c5dfb1b11ce7e" // replace if needed
    }
}

interface ForecastApi {
    @GET("data/2.5/forecast/daily?cnt=16")
    suspend fun getForecast(
        @Query("zip") zip: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): ForecastResponse
}

@Serializable
data class ForecastResponse(
    val list: List<ForecastDayApi>
)

@Serializable
data class ForecastDayApi(
    @SerialName("dt") val timestamp: Long,
    val temp: TempInfo,
    val weather: List<WeatherInfo>
)

@Serializable
data class TempInfo(
    val day: Double
)

@Serializable
data class WeatherInfo(
    val main: String
)

data class ForecastDay(
    val date: String,
    val temp: Double,
    val condition: String
)