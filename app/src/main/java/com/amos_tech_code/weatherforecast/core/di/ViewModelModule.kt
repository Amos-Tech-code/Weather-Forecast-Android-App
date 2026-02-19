package com.amos_tech_code.weatherforecast.core.di

import com.amos_tech_code.weatherforecast.ui.feature.add_city.AddCityScreenViewModel
import com.amos_tech_code.weatherforecast.ui.feature.home.HomeViewModel
import com.amos_tech_code.weatherforecast.ui.feature.saved_cities.SavedCitiesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { HomeViewModel(get(), get()) }

    viewModel { AddCityScreenViewModel(get(), get()) }

    viewModel { SavedCitiesViewModel(get()) }

}