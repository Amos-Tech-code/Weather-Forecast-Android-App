package com.amos_tech_code.weatherforecast.ui.feature.home

import com.amos_tech_code.weatherforecast.domain.model.WeatherData

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val data: WeatherData) : HomeState()
    data class Error(val message: String) : HomeState()
}