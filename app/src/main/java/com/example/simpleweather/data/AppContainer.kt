package com.example.simpleweather.data

import android.content.Context
import androidx.room.Room
import com.example.simpleweather.database.WeatherDao
import com.example.simpleweather.database.WeatherDatabase
import com.example.simpleweather.network.WeatherService
import com.example.simpleweather.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// AppContainer is a simple, manual way of managing dependencies. DI frameworks like Hilt can be
// used to automate this process.
interface AppContainer {
    val weatherService: WeatherService
    val weatherDao: WeatherDao
    val locationProvider: FusedLocationProviderClient
}

class DefaultAppContainer(
    applicationContext: Context
) : AppContainer {

    override val weatherService: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    override val weatherDao: WeatherDao by lazy {
        val db = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
        db.weatherDao()
    }

    override val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }
}