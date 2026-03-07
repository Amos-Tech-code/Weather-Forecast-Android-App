package com.amos_tech_code.weatherforecast.domain.repository

import com.amos_tech_code.weatherforecast.core.network.ApiResult
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CitySearchResult
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import kotlinx.coroutines.flow.Flow

interface CityRepository {

    suspend fun searchCities(query: String): ApiResult<List<CitySearchResult>>

    fun getAllCities(): Flow<List<City>>

    fun getAllCitiesWithWeather(): Flow<List<CityWeather>>

    suspend fun deleteCity(cityId: String)

}