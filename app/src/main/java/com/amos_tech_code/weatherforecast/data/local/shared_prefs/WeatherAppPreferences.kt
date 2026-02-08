package com.amos_tech_code.weatherforecast.data.local.shared_prefs

import android.content.Context
import androidx.core.content.edit
import com.amos_tech_code.weatherforecast.domain.model.City

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

    fun saveCityLocationData(city: City) {
        prefs.edit {
            putString(LAST_CITY_ID, city.id)
            putFloat(CITY_LOCATION_LAT, city.latitude.toFloat())
            putFloat(CITY_LOCATION_LONG, city.longitude.toFloat())
            putString(CITY_NAME, city.name)
            putString(CITY_COUNTRY, city.country)
        }
    }

    fun getCityLocationData(): City? {
        // Check if the essential data exists first.
        if (!prefs.contains(LAST_CITY_ID)) {
            return null
        }

        val cityId = prefs.getString(LAST_CITY_ID, null)
        val latitude = prefs.getFloat(CITY_LOCATION_LAT, -1f).toDouble()
        val longitude = prefs.getFloat(CITY_LOCATION_LONG, -1f).toDouble()
        val cityName = prefs.getString(CITY_NAME, null)
        val cityCountry = prefs.getString(CITY_COUNTRY, null)

        // Ensure we have a valid city to return
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
