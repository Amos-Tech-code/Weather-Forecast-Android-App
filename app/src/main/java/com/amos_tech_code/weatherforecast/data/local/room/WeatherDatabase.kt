package com.amos_tech_code.weatherforecast.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

}
