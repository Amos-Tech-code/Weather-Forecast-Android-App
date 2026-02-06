package com.amos_tech_code.weatherforecast.ui.feature.add_city

sealed class AddCityScreenState {
    data object Loading : AddCityScreenState()
    data object Success : AddCityScreenState()
    data class Error(val message: String) : AddCityScreenState()
}