package com.amos_tech_code.weatherforecast.ui.feature.add_city

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.weatherforecast.core.network.ApiResult
import com.amos_tech_code.weatherforecast.core.network.extractApiErrorMessage
import com.amos_tech_code.weatherforecast.domain.model.City
import com.amos_tech_code.weatherforecast.domain.model.CitySearchResult
import com.amos_tech_code.weatherforecast.domain.model.CityWeather
import com.amos_tech_code.weatherforecast.domain.model.CityWithWeather
import com.amos_tech_code.weatherforecast.domain.repository.CityRepository
import com.amos_tech_code.weatherforecast.domain.repository.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AddCityScreenViewModel(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel()
{

    // Saved cities state - first we show just cities, then load weather
    private val _savedCities = MutableStateFlow<List<City>>(emptyList())
    val savedCities: StateFlow<List<City>> = _savedCities.asStateFlow()

    // Separate flow for cities with weather (loaded asynchronously)
    private val _citiesWithWeather = MutableStateFlow<Map<String, CityWithWeather>>(emptyMap())
    val citiesWithWeather: StateFlow<Map<String, CityWithWeather>> = _citiesWithWeather.asStateFlow()

    // Loading states
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isLoadingCities = MutableStateFlow(true)
    val isLoadingCities: StateFlow<Boolean> = _isLoadingCities.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    // 1. Make searchResults a mutable state flow that we control directly.
    private val _searchResults = MutableStateFlow<List<CitySearchResult>>(emptyList())
    val searchResults: StateFlow<List<CitySearchResult>> = _searchResults.asStateFlow()

    // Events
    private val _event = Channel<AddCityScreenEvent>()
    val event = _event.receiveAsFlow()

    // Track weather loading per city
    private val _weatherLoadingStates = MutableStateFlow<Set<String>>(emptySet())
    val weatherLoadingStates: StateFlow<Set<String>> = _weatherLoadingStates.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSavedCitiesWithWeather()
    }


    private fun loadSavedCitiesWithWeather() {
        viewModelScope.launch {
            cityRepository.getAllCities()
                .catch {
                    _isLoadingCities.value = false
                    _event.send(AddCityScreenEvent.ShowErrorMessage("Failed to load saved cities"))
                }
                .collect { cities ->
                    // Show cities immediately
                    _savedCities.value = cities
                    _isLoadingCities.value = false

                    // Load weather for each city
                    val missingWeatherCities = cities.filter {
                        _citiesWithWeather.value[it.id] == null
                    }

                    if (missingWeatherCities.isNotEmpty()) {
                        loadWeatherForCities(missingWeatherCities)
                    }
                }
        }
    }

    private fun loadWeatherForCities(cities: List<City>) {
        viewModelScope.launch {
            _weatherLoadingStates.value = cities.map { it.id }.toSet()

            coroutineScope {
                cities.map { city ->
                    async { loadWeatherForCity(city) }
                }.awaitAll()
            }
        }
    }

    private suspend fun loadWeatherForCity(city: City) {
        try {
            val weatherResult = weatherRepository.getCityBasicWeather(
                city.latitude,
                city.longitude
            )

            val cityWithWeather = when (weatherResult) {
                is ApiResult.Success -> {
                    // The repository returns CityWeather, but we need to match it with our City
                    CityWithWeather(
                        city = city,
                        weather = CityWeather(
                            city = city,
                            temp = weatherResult.data.temp,
                            high = weatherResult.data.high,
                            low = weatherResult.data.low,
                            condition = weatherResult.data.condition,
                            iconRes = weatherResult.data.iconRes
                        ),
                        error = null
                    )
                }
                is ApiResult.Failure -> {
                    CityWithWeather(
                        city = city,
                        weather = null,
                        error = weatherResult.error.extractApiErrorMessage()
                    )
                }
            }

            // Update the specific city's weather
            _citiesWithWeather.update { it + (city.id to cityWithWeather) }

        } catch (_: Exception) {
            val cityWithWeather = CityWithWeather(
                city = city,
                weather = null,
                error = "Failed to fetch weather"
            )

            _citiesWithWeather.update { it + (city.id to cityWithWeather) }

        } finally {
            // Remove from loading set
            _weatherLoadingStates.update { it - city.id }
        }
    }

    // In AddCityScreenViewModel.kt
    // In AddCityScreenViewModel.kt
    fun onSearchQueryUpdated(query: String) {
        _searchQuery.value = query

        if (query.length < 2) {
            searchJob?.cancel()
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        // --- START OF THE VIEWMODEL FIX ---

        // 1. Create a local, immutable copy of the query at this exact moment.
        val queryToSearch = query

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            _isSearching.value = true
            try {
                // 2. Use the local copy. This value will not change, even if _searchQuery is cleared later.
                val result = cityRepository.searchCities(queryToSearch)

                when (result) {
                    is ApiResult.Success -> {
                        _searchResults.value = result.data
                    }

                    is ApiResult.Failure -> {
                        val message = result.error.extractApiErrorMessage()
                        _event.send(AddCityScreenEvent.ShowErrorMessage(message))
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _event.send(AddCityScreenEvent.ShowErrorMessage("Failed to search cities"))
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
        // --- END OF THE VIEWMODEL FIX ---
    }


    fun onClearSearch() {
        searchJob?.cancel()
        searchJob = null
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    fun onAddToSavedState(suggestion: CitySearchResult) {
        onClearSearch()

        val city = City(
            id = suggestion.id,
            name = suggestion.name,
            country = suggestion.country,
            latitude = suggestion.latitude,
            longitude = suggestion.longitude
        )
        viewModelScope.launch {
            _event.send(AddCityScreenEvent.CityAdded(city))
        }
    }


}