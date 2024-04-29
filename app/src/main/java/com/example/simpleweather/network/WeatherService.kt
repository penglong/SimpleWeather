package com.example.simpleweather.network

import com.example.simpleweather.data.Location
import com.example.simpleweather.data.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String? = "imperial",
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("/data/2.5/weather")
    suspend fun getWeatherByLatLon(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String? = "imperial",
        @Query("appid") apiKey: String,
    ): Response<WeatherResponse>

    @GET("/geo/1.0/direct")
    suspend fun getLocation(
        @Query("q") q: String,
        @Query("limit") limit: String? = "10",
        @Query("appid") apiKey: String,
    ): Response<Array<Location>>

}