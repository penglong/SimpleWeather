package com.example.simpleweather.util

import com.example.simpleweather.data.CityData
import com.example.simpleweather.data.Location
import com.example.simpleweather.data.WeatherData
import com.example.simpleweather.data.WeatherResponse
import com.example.simpleweather.database.WeatherEntity

fun Location.asCityData(): CityData {
    return CityData(
        this.name,
        this.state,
        this.country,
        this.lat.toDouble(),
        this.lon.toDouble()
    )
}

fun WeatherResponse.asWeatherEntity(): WeatherEntity {
    val iconUrl = Constants.WEATHER_ICON_BASE_URL + this.weather[0].icon + "@4x.png"
    return WeatherEntity(
        this.name,
        this.coord.lat,
        this.coord.lon,
        this.main.temp,
        this.weather[0].description,
        iconUrl,
        System.currentTimeMillis()
    )
}

fun WeatherEntity.asWeatherData(): WeatherData {
    return WeatherData(
        this.cityName,
        this.temperature,
        this.description,
        this.iconUrl
    )
}

