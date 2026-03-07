package com.amos_tech_code.weatherforecast.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoErrorResponseDto(
    val error: Boolean,
    val reason: String
)
