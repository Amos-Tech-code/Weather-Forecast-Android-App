package com.amos_tech_code.weatherforecast

import android.app.Application
import com.amos_tech_code.weatherforecast.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherForecastApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WeatherForecastApp)

            // Provide the property value for isDebug
            properties(mapOf("isDebug" to BuildConfig.DEBUG))

            modules(listOf(appModule))
        }

    }
}