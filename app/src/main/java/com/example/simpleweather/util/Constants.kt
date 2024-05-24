package com.example.simpleweather.util

object Constants {
    const val WEATHER_BASE_URL: String = "https://api.openweathermap.org/"

    // TODO: move API key from code to a secure location, such as local.properties, or refer to
    // https://developer.android.com/studio/build/build-variants#build-types
    const val WEATHER_API_KEY: String = "3fbeb0e282a847aca38761a52ccb2ed6"
    const val WEATHER_ICON_BASE_URL: String = "https://openweathermap.org/img/wn/"

    const val DATABASE_NAME: String = "weather-database"
}