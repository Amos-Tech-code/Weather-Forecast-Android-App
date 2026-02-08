package com.amos_tech_code.weatherforecast.data.repository

import com.amos_tech_code.weatherforecast.core.network.ApiResult
import com.amos_tech_code.weatherforecast.core.network.safeApiCall
import com.amos_tech_code.weatherforecast.data.local.room.CityDao
import com.amos_tech_code.weatherforecast.data.local.room.mappers.toCityEntity
import com.amos_tech_code.weatherforecast.data.local.room.mappers.updateCityEntity
import com.amos_tech_code.weatherforecast.data.remote.api.WeatherApiService
import com.amos_tech_code.weatherforecast.data.remote.mapper.toCityWeather
import com.amos_tech_code.weatherforecast.data.remote.mapper.toDomainModel
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import com.amos_tech_code.weatherforecast.domain.model.WeatherData
import com.amos_tech_code.weatherforecast.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService,
    private val cityDao: CityDao,
) : WeatherRepository {

    override suspend fun getWeather(
        city: City
    ): ApiResult<WeatherData> {

        val result = safeApiCall {
            weatherApiService.getWeatherForecast(city.latitude, city.longitude)
        }

       if (result is ApiResult.Success) {
           val weatherResponse = result.data
           val weatherData = weatherResponse.toDomainModel()

           // Check if city already exists in the database
           withContext(Dispatchers.IO) {
               val existingCity = cityDao.getCityById(city.id)
               if (existingCity != null) {
                   // Update existing city with new weather data
                   cityDao.insertCity(weatherResponse.updateCityEntity(existingCity))
               } else {
                   // Insert new city into the database
                   cityDao.insertCity(weatherResponse.toCityEntity(city))
               }
           }

           return ApiResult.Success(weatherData)

       }
       return result as ApiResult.Failure


    }

    override suspend fun getCityBasicWeather(
        latitude: Double,
        longitude: Double
    ): ApiResult<CityWeather> {
        val result = safeApiCall {
            weatherApiService.getCityBasicWeather(latitude, longitude)
        }

        return when (result) {
            is ApiResult.Success ->
                ApiResult.Success(
                    result.data.toCityWeather(latitude, longitude)
                )

            is ApiResult.Failure -> result
        }
    }

}