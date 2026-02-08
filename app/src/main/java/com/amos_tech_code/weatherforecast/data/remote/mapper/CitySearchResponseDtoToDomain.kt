package com.amos_tech_code.weatherforecast.data.remote.mapper

import com.amos_tech_code.weatherforecast.data.remote.dto.CitySearchResponseDto
import com.amos_tech_code.weatherforecast.domain.model.CitySearchResult

// Extension to convert dto to domain model
fun CitySearchResponseDto.toDomain(): CitySearchResult {
    return CitySearchResult(
        id = id.toString(),
        name = name,
        country = country ?: "",
        latitude = latitude,
        longitude = longitude,
        admin1 = admin1
    )
}