package com.amos_tech_code.weatherforecast.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_units: CurrentUnitsDto,
    val current: CurrentDataDto,
    val hourly_units: HourlyUnitsDto,
    val hourly: HourlyDataDto,
    val daily_units: DailyUnitsDto,
    val daily: DailyDataDto
)

@Serializable
data class CurrentUnitsDto(
    val time: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val apparent_temperature: String,
    val is_day: String,
    val precipitation: String,
    val rain: String,
    val showers: String,
    val snowfall: String,
    val weather_code: String,
    val pressure_msl: String,
    val surface_pressure: String,
    val wind_speed_10m: String,
    val wind_direction_10m: String,
    val wind_gusts_10m: String
)

@Serializable
data class CurrentDataDto(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val is_day: Int,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val weather_code: Int,
    val pressure_msl: Double,
    val surface_pressure: Double,
    val wind_speed_10m: Double,
    val wind_direction_10m: Int,
    val wind_gusts_10m: Double
)

@Serializable
data class HourlyUnitsDto(
    val time: String,
    val temperature_2m: String,
)

@Serializable
data class HourlyDataDto(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val relative_humidity_2m: List<Int>,
    val apparent_temperature: List<Double>,
    val precipitation_probability: List<Int>,
    val precipitation: List<Double>,
    val weather_code: List<Int>,
    val visibility: List<Double>,
    val wind_speed_10m: List<Double>,
    val wind_direction_10m: List<Int>,
    val wind_gusts_10m: List<Double>
)

@Serializable
data class DailyUnitsDto(
    val time: String,
    val weather_code: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val sunrise: String,
    val sunset: String,
    val precipitation_sum: String,
    val precipitation_hours: String,
    val precipitation_probability_max: String,
    val wind_speed_10m_max: String,
    val wind_gusts_10m_max: String,
    val wind_direction_10m_dominant: String
)

@Serializable
data class DailyDataDto(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val precipitation_sum: List<Double>,
    val precipitation_hours: List<Double>,
    val precipitation_probability_max: List<Int>,
    val wind_speed_10m_max: List<Double>,
    val wind_gusts_10m_max: List<Double>,
    val wind_direction_10m_dominant: List<Int>
)