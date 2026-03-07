package com.amos_tech_code.weatherforecast.ui.feature.add_city

import com.amos_tech_code.weatherforecast.domain.model.City

// Events
sealed class AddCityScreenEvent {
    data class ShowErrorMessage(val message: String) : AddCityScreenEvent()
    data class CityAdded(val city: City) : AddCityScreenEvent()
    data class CityRemoved(val cityName: String) : AddCityScreenEvent()
}