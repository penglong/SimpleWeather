package com.example.simpleweather.data;

import com.example.simpleweather.database.WeatherDao;
import com.example.simpleweather.network.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;

// AppContainer is a simple, manual way of managing dependencies. DI frameworks like Hilt can be
// used to automate this process.
public interface AppContainer {
    WeatherService getWeatherService();

    WeatherDao getWeatherDao();

    FusedLocationProviderClient getLocationProvider();
}
