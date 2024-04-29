package com.example.simpleweather

import android.app.Application
import com.example.simpleweather.data.AppContainer
import com.example.simpleweather.data.DefaultAppContainer


class WeatherApplication : Application() {

    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = DefaultAppContainer(this)
    }

    companion object {
        lateinit var instance: WeatherApplication
            private set
    }
}