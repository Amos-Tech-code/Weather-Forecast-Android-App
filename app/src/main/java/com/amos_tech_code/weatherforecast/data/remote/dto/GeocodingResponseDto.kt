package com.amos_tech_code.weatherforecast.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    val results: List<CitySearchResponseDto>? = null,
    val generationtime_ms: Double? = null
)

@Serializable
data class CitySearchResponseDto(
    val id: Int,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val admin1: String? = null,
    val timezone: String? = null
)