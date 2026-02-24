package com.amos_tech_code.weatherforecast.data.remote.mapper

import com.amos_tech_code.weatherforecast.R
import com.amos_tech_code.weatherforecast.data.remote.dto.BasicWeatherResponseDto
import com.amos_tech_code.weatherforecast.data.remote.dto.CurrentDataDto
import com.amos_tech_code.weatherforecast.data.remote.dto.DailyDataDto
import com.amos_tech_code.weatherforecast.data.remote.dto.HourlyDataDto
import com.amos_tech_code.weatherforecast.data.remote.dto.WeatherResponseDto
import com.amos_tech_code.weatherforecast.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

// Mapper for full weather data
fun WeatherResponseDto.toDomainModel(cityName: String): WeatherData {
    return WeatherData(
        location = cityName,
        currentTemp = current.temperature_2m.toInt(),
        condition = getWeatherCondition(current.weather_code, current.is_day),
        highTemp = daily.temperature_2m_max.firstOrNull()?.toInt() ?: 0,
        lowTemp = daily.temperature_2m_min.firstOrNull()?.toInt() ?: 0,
        hourlyForecast = mapHourlyForecast(hourly, current.time), // Pass current.time
        weeklyForecast = mapWeeklyForecast(daily),
        weatherDetails = mapWeatherDetails(current, daily, hourly)
    )
}

private fun mapHourlyForecast(hourly: HourlyDataDto, currentTime: String? = null): List<HourlyForecast> {
    return hourly.time.take(24).mapIndexed { index, time ->
        HourlyForecast(
            time = formatTime(time, currentTime), // Pass current time for comparison
            temp = hourly.temperature_2m.getOrNull(index)?.toInt() ?: 0,
            iconRes = getWeatherIcon(hourly.weather_code.getOrNull(index) ?: 0, 1),
            precipitation = hourly.precipitation_probability.getOrNull(index)
        )
    }
}

private fun mapWeeklyForecast(daily: DailyDataDto): List<DailyForecast> {
    return daily.time.mapIndexed { index, date ->
        DailyForecast(
            day = formatDay(date, index == 0),
            highTemp = daily.temperature_2m_max.getOrNull(index)?.toInt() ?: 0,
            lowTemp = daily.temperature_2m_min.getOrNull(index)?.toInt() ?: 0,
            iconRes = getWeatherIcon(daily.weather_code.getOrNull(index) ?: 0, 1),
            precipitation = daily.precipitation_probability_max.getOrNull(index)
        )
    }
}

private fun mapWeatherDetails(
    current: CurrentDataDto,
    daily: DailyDataDto,
    hourly: HourlyDataDto
): WeatherDetails {
    return WeatherDetails(
        airQuality = AirQuality(3, "Low Health Risk"), // Open-Meteo doesn't provide AQI
        uvIndex = UVIndex(4, "Moderate"), // Would need separate API
        sunrise = formatTimeTo12Hour(daily.sunrise.firstOrNull() ?: ""),
        sunset = formatTimeTo12Hour(daily.sunset.firstOrNull() ?: ""),
        wind = Wind(
            direction = getWindDirection(current.wind_direction_10m),
            speed = "${current.wind_speed_10m.toInt()} km/h"
        ),
        rainfall = Rainfall(
            current = "${current.precipitation} mm",
            next24h = "${hourly.precipitation.take(24).sum()} mm"
        ),
        feelsLike = current.apparent_temperature.toInt(),
        humidity = current.relative_humidity_2m,
        visibility = "${(hourly.visibility.firstOrNull() ?: 0.0).toInt()} km",
        pressure = "${current.pressure_msl.toInt()} hPa"
    )
}

private fun formatTime(isoTime: String, currentTime: String? = null): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

        // Parse the forecast time
        val forecastDate = inputFormat.parse(isoTime)

        // If currentTime is provided (from API), compare with it
        // Otherwise compare with system current time
        val compareDate = if (currentTime != null) {
            inputFormat.parse(currentTime)
        } else {
            Date() // Current system time
        }

        // Check if this is the current hour
        if (forecastDate != null && compareDate != null) {
            val forecastCalendar = Calendar.getInstance().apply { time = forecastDate }
            val currentCalendar = Calendar.getInstance().apply { time = compareDate }

            // Check if same year, month, day, and hour
            if (forecastCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                forecastCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                forecastCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH) &&
                forecastCalendar.get(Calendar.HOUR_OF_DAY) == currentCalendar.get(Calendar.HOUR_OF_DAY)) {
                return "Now"
            }
        }

        // Otherwise format normally
        val outputFormat = SimpleDateFormat("ha", Locale.getDefault())
        outputFormat.format(forecastDate ?: Date()).lowercase()
    } catch (_: Exception) {
        "N/A"
    }
}

private fun formatDay(date: String, isToday: Boolean): String {
    return if (isToday) "Today" else {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: Date())
        } catch (_: Exception) {
            "Day"
        }
    }
}

private fun formatTimeTo12Hour(time: String): String {
    return try {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        outputFormat.format(date ?: Date())
    } catch (_: Exception) {
        time
    }
}

private fun getWindDirection(degrees: Int): String {
    return when (degrees) {
        in 338..360, in 0..22 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        in 293..337 -> "NW"
        else -> "N/A"
    }
}

fun getWeatherCondition(code: Int, isDay: Int = 1): String {
    return when (code) {
        0 -> if (isDay == 1) "Clear sky" else "Clear"
        1, 2, 3 -> if (isDay == 1) "Partly cloudy" else "Partly clear"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with hail"
        else -> "Unknown"
    }
}

fun getWeatherIcon(code: Int, isDay: Int = 1): Int {
    return R.drawable.ic_sun_cloud_angled_rain
    /*/return when (code) {
        0 -> if (isDay == 1) R.drawable.ic_sun else R.drawable.ic_moon
        1, 2, 3 -> if (isDay == 1) R.drawable.ic_sun_cloud else R.drawable.ic_moon_cloud
        45, 48 -> R.drawable.ic_fog
        51, 53, 55 -> R.drawable.ic_light_rain
        56, 57 -> R.drawable.ic_freezing_rain
        61, 63, 65 -> R.drawable.ic_rain
        66, 67 -> R.drawable.ic_freezing_rain
        71, 73, 75 -> R.drawable.ic_snow
        77 -> R.drawable.ic_snow
        80, 81, 82 -> R.drawable.ic_rain
        85, 86 -> R.drawable.ic_snow
        95 -> R.drawable.ic_thunderstorm
        96, 99 -> R.drawable.ic_thunderstorm_rain
        else -> if (isDay == 1) R.drawable.ic_sun else R.drawable.ic_moon
    }

     */
}


// Mapper for basic weather
fun BasicWeatherResponseDto.toCityWeather(
    latitude: Double,
    longitude: Double
): CityWeather {

    return CityWeather(
        city = City(
            id = "temp_${latitude}_${longitude}",
            name = "",
            country = "",
            latitude = latitude,
            longitude = longitude
        ),
        temp = current.temperature_2m.toInt(),
        high = daily.temperature_2m_max.firstOrNull()?.toInt() ?: 0,
        low = daily.temperature_2m_min.firstOrNull()?.toInt() ?: 0,
        condition = getWeatherCondition(current.weather_code),
        iconRes = getWeatherIcon(current.weather_code)
    )
}