package com.amos_tech_code.weatherforecast.ui.feature.saved_cities

import com.amos_tech_code.weatherforecast.domain.model.CityWeather

data class SavedCitiesUiState(
    val cities: List<CityWeather> = emptyList(),
    val isLoading: Boolean = true
)
