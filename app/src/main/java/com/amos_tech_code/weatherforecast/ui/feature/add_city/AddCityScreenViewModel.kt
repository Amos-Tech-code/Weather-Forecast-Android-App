package com.amos_tech_code.weatherforecast.ui.feature.add_city

import androidx.lifecycle.ViewModel
import com.amos_tech_code.weatherforecast.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class AddCityScreenViewModel : ViewModel() {

    private val _state = MutableStateFlow<List<CityWeather>>(emptyList())
    val state: StateFlow<List<CityWeather>> = _state.asStateFlow()

    private val _event = Channel<AddCityScreenEvent>()
    val event = _event.receiveAsFlow()

    init {
        fetchData()
    }

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryUpdated(query: String) {
        _searchQuery.update { query }
    }

    fun fetchData() {
        _state.value = listOf(
            CityWeather(1, 19, 24, 18, "Montreal", "Canada", "Mid Rain", R.drawable.ic_moon_cloud_mid_rain),
            CityWeather(2, 20, 21, 19, "Toronto", "Canada", "Fast Wind", R.drawable.ic_moon_cloud_mid_rain),
            CityWeather(3, 13, 16, 8, "Tokyo", "Japan", "Showers", R.drawable.ic_moon_cloud_mid_rain),
            CityWeather(4, 10, 15, 7, "Tennessee", "United States", "Tornado", R.drawable.ic_moon_cloud_mid_rain)
        )
    }

}
data class CityWeather(
    val id: Int,
    val temp: Int,
    val high: Int,
    val low: Int,
    val city: String,
    val country: String,
    val condition: String,
    val iconRes: Int // Pass your 3D weather icons here
)
