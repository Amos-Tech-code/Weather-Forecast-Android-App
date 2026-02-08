package com.amos_tech_code.weatherforecast.domain.model

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