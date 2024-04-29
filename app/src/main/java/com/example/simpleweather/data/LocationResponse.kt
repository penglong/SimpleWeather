package com.example.simpleweather.data

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("state")
    val state: String?,
    @SerializedName("local_names")
    val localNames: Map<String, String>?
)
