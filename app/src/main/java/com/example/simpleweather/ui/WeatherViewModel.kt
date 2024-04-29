package com.example.simpleweather.ui

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simpleweather.R
import com.example.simpleweather.WeatherApplication
import com.example.simpleweather.data.CityData
import com.example.simpleweather.database.WeatherDao
import com.example.simpleweather.network.WeatherService
import com.example.simpleweather.util.Constants.WEATHER_API_KEY
import com.example.simpleweather.util.DataMapper
import com.example.simpleweather.util.NetworkUtils
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherDao: WeatherDao,
    private val weatherService: WeatherService,
    private val locationProvider: FusedLocationProviderClient
) : ViewModel() {

    private var _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    private val _searchResults = MutableStateFlow(listOf<CityData>())
    val searchResults = _searchResults.asStateFlow()

    val currentPage = MutableStateFlow(0)

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val isInternetAvailable: Boolean
        get() = NetworkUtils.isInternetAvailable(WeatherApplication.instance)

    val weatherData = weatherDao.getAll()
        .map { list ->
            if (list.isNotEmpty()) {
                list.map(DataMapper::toWeatherData)
            } else {
                emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            emptyList()
        )

    init {
        // For demo purposes, the weather data for the first city in the list is fetched/refreshed
        // when the application starts. A minor modification can enable fetching of weather data
        // for all cities in the list.
        viewModelScope.launch {
            val weatherEntityList = weatherDao.getAll().first()
            if (weatherEntityList.isNotEmpty() && isInternetAvailable) {
                fetchAndStoreWeatherData(weatherEntityList[0].lat, weatherEntityList[0].lon)
            }
        }
    }

    private var searchJob: Job? = null
    fun updateSearchQuery(query: String) {
        _searchString.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(200) // wait a bit for user to finish typing
            if (!isInternetAvailable) {
                Toast.makeText(
                    WeatherApplication.instance,
                    WeatherApplication.instance.getString(R.string.no_internet_available),
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }
            if (query.length < 3) {
                _searchResults.value = emptyList()
                return@launch
            }
            try {
                val response =
                    weatherService.getLocation(
                        q = query,
                        apiKey = WEATHER_API_KEY
                    )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _searchResults.value = it.toList().map(DataMapper::toCityData)
                    }
                } else {
                    Log.e(TAG, "API response error: ${response.message()}")
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "API response exception: ${e.message}")
                _searchResults.value = emptyList()
            }

        }
    }

    @SuppressLint("MissingPermission")
    fun fetchWeatherDataWithPermissionGranted() {
        locationProvider.lastLocation.addOnSuccessListener { location ->
            fetchWeatherDataByCoordinates(location.latitude, location.longitude)
        }
    }

    fun fetchWeatherDataByCoordinates(latitude: Double, longitude: Double) {
        if (isInternetAvailable) {
            viewModelScope.launch {
                fetchAndStoreWeatherData(latitude, longitude)
            }
        } else {
            Toast.makeText(
                WeatherApplication.instance,
                WeatherApplication.instance.getString(R.string.no_internet_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun setCurrentPage(page: Int) {
        currentPage.value = page
    }

    suspend fun fetchAndStoreWeatherData(latitude: Double, longitude: Double) {
        try {
            val response = weatherService.getWeatherByLatLon(
                lat = latitude.toString(),
                lon = longitude.toString(),
                apiKey = WEATHER_API_KEY
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    val newWeatherEntity = DataMapper.toWeatherEntity(it)
                    // Update the local database with the new data
                    weatherDao.insert(newWeatherEntity)
                    currentPage.value = 0
                }
            } else {
                // handle error
                Log.e(TAG, "API response error: ${response.message()}")
                _error.value = response.message()
            }
        } catch (e: Exception) {
            Log.e(TAG, "API request exception: ${e.message}")
            _error.value = e.message
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val app = WeatherApplication.instance
                WeatherViewModel(
                    app.appContainer.weatherDao,
                    app.appContainer.weatherService,
                    app.appContainer.locationProvider
                )
            }
        }
        private const val TAG = "WeatherViewModel"
    }
}

