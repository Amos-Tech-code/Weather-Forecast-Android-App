package com.amos_tech_code.weatherforecast.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.weatherforecast.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel()
{

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    private val _event = Channel<HomeEvent>()
    val event = _event.receiveAsFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        // Simulate API call delay
        viewModelScope.launch {
            delay(1000) // Simulate network delay

            // Create dummy data matching the design
            val dummyData = createDummyWeatherData()
            _state.value = HomeState.Success(dummyData)
        }
    }

    private fun createDummyWeatherData(): WeatherData {
        return WeatherData(
            location = "Montreal",
            currentTemp = 19,
            condition = "Mostly Clear",
            highTemp = 24,
            lowTemp = 18,
            hourlyForecast = listOf(
                HourlyForecast("12AM", 19, R.drawable.ic_moon_cloud_mid_rain, null),
                HourlyForecast("Now", 19, R.drawable.ic_moon_cloud_mid_rain, 30),
                HourlyForecast("2AM", 18, R.drawable.ic_moon_cloud_mid_rain, 30),
                HourlyForecast("3AM", 19, R.drawable.ic_moon_cloud_mid_rain, 30),
                HourlyForecast("4AM", 19, R.drawable.ic_moon_cloud_mid_rain, 30),
                HourlyForecast("5AM", 18, R.drawable.ic_moon_cloud_mid_rain, 25),
                HourlyForecast("6AM", 17, R.drawable.ic_moon_cloud_mid_rain, 20),
                HourlyForecast("7AM", 18, R.drawable.ic_moon_cloud_mid_rain, 15),
            ),
            weeklyForecast = listOf(
                DailyForecast("Today", 24, 18, R.drawable.ic_moon_cloud_mid_rain, 30),
                DailyForecast("Tue", 26, 19, R.drawable.ic_moon_cloud_mid_rain, 10),
                DailyForecast("Wed", 22, 17, R.drawable.ic_moon_cloud_mid_rain, 80),
                DailyForecast("Thu", 20, 16, R.drawable.ic_moon_cloud_mid_rain, 90),
                DailyForecast("Fri", 23, 17, R.drawable.ic_moon_cloud_mid_rain, 40),
                DailyForecast("Sat", 25, 19, R.drawable.ic_moon_cloud_mid_rain, 5),
                DailyForecast("Sun", 27, 20, R.drawable.ic_moon_cloud_mid_rain, 0),
            ),
            weatherDetails = WeatherDetails(
                airQuality = AirQuality(3, "Low Health Risk"),
                uvIndex = UVIndex(4, "Moderate"),
                sunrise = "5:28 AM",
                sunset = "7:25 PM",
                wind = Wind("N", "9.7 km/h"),
                rainfall = Rainfall("1.8 mm", "1.2 mm"),
                feelsLike = 19,
                humidity = 90,
                visibility = "8 km",
                pressure = "0 hPa" // From design
            )
        )
    }
}

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val data: WeatherData) : HomeState()
    data class Error(val message: String) : HomeState()
}

sealed class HomeEvent {
    data class ShowErrorMessage(val message: String) : HomeEvent()
}

// Domain models for Home Screen
data class WeatherData(
    val location: String,
    val currentTemp: Int,
    val condition: String,
    val highTemp: Int,
    val lowTemp: Int,
    val hourlyForecast: List<HourlyForecast>,
    val weeklyForecast: List<DailyForecast>,
    val weatherDetails: WeatherDetails
)

data class HourlyForecast(
    val time: String,
    val temp: Int,
    val iconRes: Int,
    val precipitation: Int? // percentage
)

data class DailyForecast(
    val day: String,
    val highTemp: Int,
    val lowTemp: Int,
    val iconRes: Int,
    val precipitation: Int? // percentage
)

data class WeatherDetails(
    val airQuality: AirQuality,
    val uvIndex: UVIndex,
    val sunrise: String,
    val sunset: String,
    val wind: Wind,
    val rainfall: Rainfall,
    val feelsLike: Int,
    val humidity: Int,
    val visibility: String,
    val pressure: String
)

data class AirQuality(val value: Int, val riskLevel: String)
data class UVIndex(val value: Int, val level: String)
data class Wind(val direction: String, val speed: String)
data class Rainfall(val current: String, val next24h: String)