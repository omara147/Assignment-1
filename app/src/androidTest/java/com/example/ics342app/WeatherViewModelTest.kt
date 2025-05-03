package com.example.ics342app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel

    private val fakeService = object : WeatherService {
        override suspend fun getWeather(city: String, units: String, apiKey: String): WeatherResponse {
            return WeatherResponse(
                name = "Fake City",
                weather = listOf(WeatherCondition("Clear", "clear sky")),
                main = MainInfo(temp = 72.0, feelsLike = 70.0, pressure = 1010, humidity = 50)
            )
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = WeatherViewModel(fakeService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchWeather_updatesWeatherFlow() = runTest {
        viewModel.fetchWeather("Fake City")

        val result = withTimeoutOrNull(1000) {
            while (viewModel.weather.value == null) {
                // Wait until data is loaded
            }
            viewModel.weather.value
        }

        assertNotNull(result)
        assertEquals("Fake City", result?.name)
        assertEquals(72.0, result?.main?.temp!!, 0.01)    }
}