package com.amos_tech_code.weatherforecast.ui.feature.add_city

sealed class AddCityScreenEvent {
    data class ShowErrorMessage(val message: String) : AddCityScreenEvent()
}