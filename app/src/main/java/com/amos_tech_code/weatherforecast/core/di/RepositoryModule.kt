package com.amos_tech_code.weatherforecast.core.di

import com.amos_tech_code.weatherforecast.data.repository.CityRepositoryImpl
import com.amos_tech_code.weatherforecast.data.repository.WeatherRepositoryImpl
import com.amos_tech_code.weatherforecast.domain.repository.CityRepository
import com.amos_tech_code.weatherforecast.domain.repository.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<WeatherRepository> {
        WeatherRepositoryImpl(get(), get())
    }

    single<CityRepository> {
        CityRepositoryImpl(get(), get())
    }

}
