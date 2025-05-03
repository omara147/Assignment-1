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
class ForecastViewModelTest {

    private lateinit var viewModel: ForecastViewModel

    private val fakeApi = object : ForecastApi {
        override suspend fun getForecast(zip: String, units: String, apiKey: String): ForecastResponse {
            return ForecastResponse(
                list = listOf(
                    ForecastDayApi(
                        timestamp = 1700000000L,
                        temp = TempInfo(day = 65.5),
                        weather = listOf(WeatherInfo("Cloudy"))
                    ),
                    ForecastDayApi(
                        timestamp = 1700086400L,
                        temp = TempInfo(day = 70.2),
                        weather = listOf(WeatherInfo("Sunny"))
                    )
                )
            )
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ForecastViewModel(fakeApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchForecast_updatesForecastFlow() = runTest {
        viewModel.fetchForecast("55404")

        val result = withTimeoutOrNull(1000) {
            while (viewModel.forecast.value.isEmpty()) {
                // wait for flow update
            }
            viewModel.forecast.value
        }

        assertNotNull(result)
        assertEquals(2, result!!.size)
        assertEquals(65.5, result[0].temp, 0.01)
        assertEquals("Cloudy", result[0].condition)
    }
}