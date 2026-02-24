package com.amos_tech_code.weatherforecast.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.weatherforecast.core.network.ApiResult
import com.amos_tech_code.weatherforecast.core.network.extractApiErrorMessage
import com.amos_tech_code.weatherforecast.data.local.shared_prefs.WeatherAppPreferences
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.repository.WeatherRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherAppPreferences: WeatherAppPreferences
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    private val _event = Channel<HomeEvent>()
    val event = _event.receiveAsFlow()

    private var currentLocation: City? = null


    init {
        weatherAppPreferences.getCityLocationData()
            .filterNotNull() // We only care about non-null city values
            .distinctUntilChanged() // Don't re-fetch if the city is the same
            .onEach { city ->
                currentLocation = city
                fetchWeatherData(city)
            }
            .launchIn(viewModelScope)
    }

    fun updateLocation(city: City) {

        // Update current location
        currentLocation = city

        // Save last weather location search
        weatherAppPreferences.saveCityLocationData(city)

        // Fetch weather data for the new location
        fetchWeatherData(city)
    }


    // Fetch weather data from the repository
    fun fetchWeatherData(
        city: City,
    ) {
        _state.value = HomeState.Loading
        viewModelScope.launch {
            try {
                val result = weatherRepository.getWeather(city)

                when (result) {
                    is ApiResult.Success -> {
                        _state.value = HomeState.Success(result.data)
                    }
                    is ApiResult.Failure -> {
                        val message = result.error.extractApiErrorMessage()
                        _state.value = HomeState.Error(message)
                        _event.send(HomeEvent.ShowErrorMessage(message))
                    }
                }
            } catch (_: Exception) {
                _state.value = HomeState.Error("An unexpected error occurred")
            }

        }
    }

    // Retry function
    fun retry() {
        currentLocation?.let { fetchWeatherData(it) }
    }

}