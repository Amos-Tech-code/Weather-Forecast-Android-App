package com.amos_tech_code.weatherforecast.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BasicWeatherResponseDto(
    val current: BasicCurrentData,
    val daily: BasicDailyData,
    val timezone: String
)

@Serializable
data class BasicCurrentData(
    val temperature_2m: Double,
    val weather_code: Int,
    val time: String
)

@Serializable
data class BasicDailyData(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val time: List<String>
)