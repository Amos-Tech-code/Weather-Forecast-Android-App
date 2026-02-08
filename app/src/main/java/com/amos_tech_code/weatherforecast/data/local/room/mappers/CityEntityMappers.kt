package com.amos_tech_code.weatherforecast.data.local.room.mappers

import com.amos_tech_code.weatherforecast.data.local.room.CityEntity
import com.amos_tech_code.weatherforecast.data.remote.dto.WeatherResponseDto
import com.amos_tech_code.weatherforecast.data.remote.mapper.getWeatherCondition
import com.amos_tech_code.weatherforecast.domain.model.City


fun WeatherResponseDto.toCityEntity(
    city: City,
    currentTime: Long = System.currentTimeMillis()
): CityEntity {
    return CityEntity(
        id = city.id,
        name = city.name,
        country = city.country,
        latitude = city.latitude,
        longitude = city.longitude,
        currentTemp = current.temperature_2m.toInt(),
        highTemp = daily.temperature_2m_max.firstOrNull()?.toInt() ?: 0,
        lowTemp = daily.temperature_2m_min.firstOrNull()?.toInt() ?: 0,
        condition = getWeatherCondition(current.weather_code, current.is_day),
        weatherCode = current.weather_code,
        lastWeatherUpdate = currentTime,
        addedAt = System.currentTimeMillis()
    )
}

// Extension to update existing entity with new weather data
fun WeatherResponseDto.updateCityEntity(
    entity: CityEntity,
    currentTime: Long = System.currentTimeMillis()
): CityEntity {
    return entity.copy(
        currentTemp = current.temperature_2m.toInt(),
        highTemp = daily.temperature_2m_max.firstOrNull()?.toInt() ?: 0,
        lowTemp = daily.temperature_2m_min.firstOrNull()?.toInt() ?: 0,
        condition = getWeatherCondition(current.weather_code, current.is_day),
        weatherCode = current.weather_code,
        lastWeatherUpdate = currentTime
    )
}

