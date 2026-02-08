package com.amos_tech_code.weatherforecast.domain.model

data class CityWithWeather(
    val city: City,
    val weather: CityWeather?,
    val error: String? = null
)