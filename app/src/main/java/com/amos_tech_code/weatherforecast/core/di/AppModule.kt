package com.amos_tech_code.weatherforecast.core.di

import com.amos_tech_code.weatherforecast.core.network.ConnectivityObserver
import com.amos_tech_code.weatherforecast.ui.feature.add_city.AddCityScreenViewModel
import com.amos_tech_code.weatherforecast.ui.feature.home.HomeViewModel
import com.amos_tech_code.weatherforecast.ui.feature.weather_list.WeatherListViewModel
import org.koin.dsl.module

val appModule = module {

    // ConnectivityObserver
    single { ConnectivityObserver(get()) }

    // Repositories

    // ViewModels
    single { HomeViewModel() }

    single { AddCityScreenViewModel() }

    single { WeatherListViewModel() }

}