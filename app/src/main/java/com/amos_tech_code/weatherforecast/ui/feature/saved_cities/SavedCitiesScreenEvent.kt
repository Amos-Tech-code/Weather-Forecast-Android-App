package com.amos_tech_code.weatherforecast.ui.feature.saved_cities

import com.amos_tech_code.weatherforecast.domain.model.City

sealed class SavedCitiesScreenEvent {

    data class ShowErrorMessage(val message: String) : SavedCitiesScreenEvent()

    object CityRemoved : SavedCitiesScreenEvent()

    data class AddToSavedState(val city: City) : SavedCitiesScreenEvent()
    object AddNewCity : SavedCitiesScreenEvent()
}