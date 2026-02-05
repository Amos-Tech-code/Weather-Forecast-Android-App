package com.amos_tech_code.weatherforecast.core.di

import com.amos_tech_code.weatherforecast.core.network.ConnectivityObserver
import org.koin.dsl.module

val appModule = module {

    single { ConnectivityObserver(get()) }

}