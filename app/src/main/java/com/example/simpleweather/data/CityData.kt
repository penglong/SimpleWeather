package com.example.simpleweather.data

data class CityData(
    val cityName: String,
    val state: String?,
    val country: String,
    val lat: Double,
    val lon: Double
)
