package com.amos_tech_code.weatherforecast.ui.feature.weather_list

import androidx.lifecycle.ViewModel
import com.amos_tech_code.weatherforecast.R
import com.amos_tech_code.weatherforecast.ui.feature.add_city.CityWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherListViewModel : ViewModel() {
    private val _savedCities = MutableStateFlow<List<CityWeather>>(emptyList())
    val savedCities: StateFlow<List<CityWeather>> = _savedCities.asStateFlow()

    init {
        loadSavedCities()
    }

    private fun loadSavedCities() {
        // Data based on your "Add.jpg" and "Weather Details.jpg" examples
        _savedCities.value = listOf(
            CityWeather(1, 19, 24, 18, "Montreal", "Canada", "Mid Rain", R.drawable.ic_moon_cloud_mid_rain),
            CityWeather(2, 20, 21, 19, "Toronto", "Canada", "Fast Wind", R.drawable.ic_moon_cloud_mid_rain),
            CityWeather(3, 13, 16, 8, "Tokyo", "Japan", "Showers", R.drawable.ic_moon_cloud_mid_rain),
            CityWeather(4, 10, 15, 7, "Tennessee", "United States", "Tornado", R.drawable.ic_moon_cloud_mid_rain)
        )
    }

    fun removeCity(city: CityWeather) {
        _savedCities.value = _savedCities.value.filter { it.city != city.city }
    }
}