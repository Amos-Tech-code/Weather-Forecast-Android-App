package com.amos_tech_code.weatherforecast.core.di

import androidx.room.Room
import com.amos_tech_code.weatherforecast.data.local.room.CityDao
import com.amos_tech_code.weatherforecast.data.local.room.WeatherDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single<WeatherDatabase> {
        Room.databaseBuilder(
            androidContext(),
            WeatherDatabase::class.java,
            "weather_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single<CityDao> {
        get<WeatherDatabase>().cityDao()
    }
}
