package com.amos_tech_code.weatherforecast.core.di

import com.amos_tech_code.weatherforecast.core.util.GEOCODING_BASE_URL
import com.amos_tech_code.weatherforecast.core.util.WEATHER_BASE_URL
import com.amos_tech_code.weatherforecast.data.remote.api.GeocodingApiService
import com.amos_tech_code.weatherforecast.data.remote.api.WeatherApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    val weatherApiBaseUrl = WEATHER_BASE_URL
    val geocodingApiBaseUrl = GEOCODING_BASE_URL

    // Gson
    single {
        GsonBuilder()
            .serializeNulls()
            .create()
    }

    // Logging Interceptor
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    // Weather Retrofit
    single(named("weatherRetrofit")) {
        Retrofit.Builder()
            .baseUrl(weatherApiBaseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    // Geocoding Retrofit
    single(named("geocodingRetrofit")) {
        Retrofit.Builder()
            .baseUrl(geocodingApiBaseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }


    // API Services
    single<WeatherApiService> {
        get<Retrofit>(named("weatherRetrofit"))
            .create(WeatherApiService::class.java)
    }

    single<GeocodingApiService> {
        get<Retrofit>(named("geocodingRetrofit"))
            .create(GeocodingApiService::class.java)
    }



}
