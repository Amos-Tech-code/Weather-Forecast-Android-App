package com.amos_tech_code.weatherforecast.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,

    val currentTemp: Int,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: String,
    val weatherCode: Int,

    val lastWeatherUpdate: Long,
    val addedAt: Long
)