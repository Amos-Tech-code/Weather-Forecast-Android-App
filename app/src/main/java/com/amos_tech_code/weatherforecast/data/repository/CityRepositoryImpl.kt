package com.amos_tech_code.weatherforecast.data.repository

import com.amos_tech_code.weatherforecast.core.network.ApiResult
import com.amos_tech_code.weatherforecast.core.network.safeApiCall
import com.amos_tech_code.weatherforecast.data.local.room.CityDao
import com.amos_tech_code.weatherforecast.data.remote.api.GeocodingApiService
import com.amos_tech_code.weatherforecast.data.remote.mapper.getWeatherIcon
import com.amos_tech_code.weatherforecast.data.remote.mapper.toDomain
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CitySearchResult
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import com.amos_tech_code.weatherforecast.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class CityRepositoryImpl(
    private val cityDao: CityDao,
    private val geocodingApiService: GeocodingApiService,
) : CityRepository {

    override suspend fun searchCities(query: String): ApiResult<List<CitySearchResult>> {
        val result = safeApiCall {
            geocodingApiService.searchCities(query)
        }
        if (result is ApiResult.Success) {
            val cities = result.data.results
                ?.map { it.toDomain() }
                ?: emptyList()

          return ApiResult.Success(cities)
        }
        return result as ApiResult.Failure

    }

    override fun getAllCities(): Flow<List<City>> = cityDao.getAllCities().map { cityEntities ->
        cityEntities.map {
            City(
                id = it.id,
                name = it.name,
                country = it.country,
                latitude = it.latitude,
                longitude = it.longitude,
                addedAt = it.addedAt
            )
        }
    }

    override fun getAllCitiesWithWeather(): Flow<List<CityWeather>> {
        return cityDao.getAllCities().map { cities ->
            cities.map { city ->
                CityWeather(
                    city = City(
                        id = city.id,
                        name = city.name,
                        country = city.country,
                        latitude = city.latitude,
                        longitude = city.longitude,
                        addedAt = city.addedAt
                    ),
                    temp = city.currentTemp,
                    high = city.highTemp,
                    low = city.lowTemp,
                    condition = city.condition,
                    iconRes = getWeatherIcon(city.weatherCode)
                )
            }
        }.distinctUntilChanged()

    }

    override suspend fun deleteCity(cityId: String) {
        cityDao.deleteCity(cityId)
    }

}