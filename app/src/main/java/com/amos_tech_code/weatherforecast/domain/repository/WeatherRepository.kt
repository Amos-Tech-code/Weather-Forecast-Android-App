package com.amos_tech_code.weatherforecast.domain.repository

import com.amos_tech_code.weatherforecast.core.network.ApiResult
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import com.amos_tech_code.weatherforecast.domain.model.WeatherData

interface WeatherRepository {

    suspend fun getWeather(
        city: City
    ): ApiResult<WeatherData>

    suspend fun getCityBasicWeather(
        latitude: Double,
        longitude: Double
    ): ApiResult<CityWeather>
}