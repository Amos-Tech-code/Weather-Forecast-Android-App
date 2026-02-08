package com.amos_tech_code.weatherforecast.ui.navigation

import kotlinx.serialization.Serializable

interface NavRoutes

@Serializable
object HomeRoute: NavRoutes

@Serializable
object AddCityRoute: NavRoutes

@Serializable
object SavedCitiesRoute: NavRoutes