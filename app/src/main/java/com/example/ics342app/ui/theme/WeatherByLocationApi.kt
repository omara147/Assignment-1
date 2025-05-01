package com.example.ics342app

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.ics342app.MainInfo

interface WeatherByLocationApi {
    @GET("data/2.5/weather")
    fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<WeatherByLocationResponse>
}

data class WeatherByLocationResponse(
    val name: String,
    val main: MainInfo
)

