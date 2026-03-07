package com.amos_tech_code.weatherforecast.data.local.shared_prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.amos_tech_code.weatherforecast.domain.model.City
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

class WeatherAppPreferences(context: Context) {

    private val prefs =
        context.getSharedPreferences("weather_app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val LAST_CITY_ID = "last_city_id"
        const val CITY_LOCATION_LAT = "last_location_lat"
        const val CITY_LOCATION_LONG = "last_location_long"
        const val CITY_NAME = "city_name"
        const val CITY_COUNTRY = "city_country"

    }

    fun isLocationSet(): Boolean =
        prefs.contains(CITY_LOCATION_LAT) && prefs.contains(CITY_LOCATION_LONG)

    /**
     * Store the last accessed city
     */
    fun saveCityLocationData(city: City) {
        prefs.edit {
            putString(LAST_CITY_ID, city.id)
            putFloat(CITY_LOCATION_LAT, city.latitude.toFloat())
            putFloat(CITY_LOCATION_LONG, city.longitude.toFloat())
            putString(CITY_NAME, city.name)
            putString(CITY_COUNTRY, city.country)
        }
    }

    /**
     * Observes the saved city data and emits a new City object whenever it changes.
     * Emits null if no valid city is saved.
     */
    fun getCityLocationData(): Flow<City?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == LAST_CITY_ID || key == CITY_NAME) { // Listen for relevant key changes
                trySend(readCityData())
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

        // Emit the initial value
        trySend(readCityData())

        // Unregister the listener when the flow is cancelled
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.conflate() // Use conflate to only deliver the latest value to slow collectors

    /**
     * Helper function to read the raw data from SharedPreferences.
     */
    private fun readCityData(): City? {
        if (!prefs.contains(LAST_CITY_ID)) {
            return null
        }

        val cityId = prefs.getString(LAST_CITY_ID, null)
        val latitude = prefs.getFloat(CITY_LOCATION_LAT, -1f).toDouble()
        val longitude = prefs.getFloat(CITY_LOCATION_LONG, -1f).toDouble()
        val cityName = prefs.getString(CITY_NAME, null)
        val cityCountry = prefs.getString(CITY_COUNTRY, null)

        return if (cityId != null && cityName != null && cityCountry != null) {
            City(
                id = cityId,
                latitude = latitude,
                longitude = longitude,
                name = cityName,
                country = cityCountry
            )
        } else {
            null
        }
    }


}
