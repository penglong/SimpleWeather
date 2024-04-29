package com.example.simpleweather.data;

import android.content.Context;

import androidx.room.Room;

import com.example.simpleweather.database.WeatherDao;
import com.example.simpleweather.database.WeatherDatabase;
import com.example.simpleweather.network.WeatherService;
import com.example.simpleweather.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultAppContainer implements AppContainer {

    private final Context applicationContext;
    private WeatherService weatherService;
    private WeatherDao weatherDao;
    private FusedLocationProviderClient locationProvider;

    public DefaultAppContainer(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public WeatherService getWeatherService() {
        if (weatherService == null) {
            weatherService = new Retrofit.Builder()
                    .baseUrl(Constants.WEATHER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WeatherService.class);
        }
        return weatherService;
    }

    @Override
    public WeatherDao getWeatherDao() {
        if (weatherDao == null) {
            WeatherDatabase db = Room.databaseBuilder(
                    applicationContext,
                    WeatherDatabase.class,
                    Constants.DATABASE_NAME
            ).build();
            weatherDao = db.weatherDao();
        }
        return weatherDao;
    }

    @Override
    public FusedLocationProviderClient getLocationProvider() {
        if (locationProvider == null) {
            locationProvider = LocationServices.getFusedLocationProviderClient(applicationContext);
        }
        return locationProvider;
    }

}
