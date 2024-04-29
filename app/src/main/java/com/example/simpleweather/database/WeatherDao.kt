package com.example.simpleweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM WeatherEntity ORDER BY timestamp DESC")
    fun getAll(): Flow<List<WeatherEntity>>
}