package com.amos_tech_code.weatherforecast.core.di

import com.amos_tech_code.weatherforecast.core.network.ConnectivityObserver
import com.amos_tech_code.weatherforecast.data.local.shared_prefs.WeatherAppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    includes(viewModelModule, repositoryModule, networkModule, databaseModule)

    // ConnectivityObserver
    single { ConnectivityObserver(androidContext()) }

    // Weather Preferences
    single { WeatherAppPreferences(androidContext()) }
}