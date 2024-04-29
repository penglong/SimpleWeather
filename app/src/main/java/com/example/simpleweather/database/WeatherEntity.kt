package com.example.simpleweather.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherEntity(
    @PrimaryKey
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val temperature: Double,
    val description: String,
    val iconUrl: String,
    val timestamp: Long
)
