package com.amos_tech_code.weatherforecast.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.time.Clock

@Parcelize
data class City(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long = Clock.System.now().toEpochMilliseconds()
) : Parcelable

data class CityWeather(
    val city: City,
    val temp: Int,
    val high: Int,
    val low: Int,
    val condition: String,
    val iconRes: Int
)

data class CitySearchResult(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val admin1: String? = null // Region/state
)