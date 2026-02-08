package com.amos_tech_code.weatherforecast.ui.feature.saved_cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import com.amos_tech_code.weatherforecast.domain.model.CityWithWeather
import com.amos_tech_code.weatherforecast.domain.repository.CityRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SavedCitiesViewModel(
    private val cityRepository: CityRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SavedCitiesUiState())
    val uiState = _uiState.asStateFlow()

    // Event channel
    private val _event = Channel<SavedCitiesScreenEvent>()
    val event = _event.receiveAsFlow()

    init {
        loadSavedCitiesWithWeather()
    }

    private fun loadSavedCitiesWithWeather() {
        viewModelScope.launch {
            cityRepository.getAllCitiesWithWeather()
                .catch {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _event.send(SavedCitiesScreenEvent.ShowErrorMessage("Failed to load saved cities"))
                }
                .collect { cities ->
                    _uiState.value = _uiState.value.copy(cities = cities, isLoading = false)
                }

        }
    }

    fun removeCity(cityId: String) {
        viewModelScope.launch {
            cityRepository.deleteCity(cityId)
            _event.send(SavedCitiesScreenEvent.CityRemoved)
        }
    }

    fun onAddCityClick() {
        _event.trySend(SavedCitiesScreenEvent.AddNewCity)
    }

    fun onAddToSavedState(cityWeather: CityWeather) {
        viewModelScope.launch {
            val city = City(
                id = cityWeather.city.id,
                name = cityWeather.city.name,
                country = cityWeather.city.country,
                latitude = cityWeather.city.latitude,
                longitude = cityWeather.city.longitude
            )
            _event.send(SavedCitiesScreenEvent.AddToSavedState(city))

        }
    }

}