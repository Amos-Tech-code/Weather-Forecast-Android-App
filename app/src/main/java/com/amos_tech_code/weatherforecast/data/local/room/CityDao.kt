package com.amos_tech_code.weatherforecast.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM city WHERE id = :cityId")
    suspend fun getCityById(cityId: String): CityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)

    @Query("SELECT * FROM city ORDER BY addedAt DESC")
    fun getAllCities(): Flow<List<CityEntity>>

    @Query("DELETE FROM city WHERE id = :cityId")
    suspend fun deleteCity(cityId: String)

}