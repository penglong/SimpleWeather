@file:OptIn(ExperimentalFoundationApi::class)

package com.example.simpleweather.ui.components


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.simpleweather.R
import com.example.simpleweather.data.WeatherData
import com.example.simpleweather.ui.WeatherViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier,
    onSearchPressedCallback: () -> Unit
) {

    val weatherDataList by viewModel.weatherData.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Weather App") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onSearchPressedCallback()
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                modifier = modifier
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (weatherDataList.isEmpty()) {
            Column(
                modifier = modifier.padding(paddingValues)
            ) {
                LocationCard(viewModel = viewModel, onPermissionDenied = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Location permission is required for this feature. Please enable it in your device settings."
                        )
                    }
                })
            }

        } else {
            WeatherPagers(
                modifier = modifier.padding(paddingValues),
                viewModel = viewModel
            )
        }
    }

    LaunchedEffect(error) {
        error?.let { errorMessage ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = "Dismiss"
                )
                if (result == SnackbarResult.Dismissed) {
                    viewModel.clearError()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherPagers(
    modifier: Modifier,
    viewModel: WeatherViewModel
) {
    val weatherDataList by viewModel.weatherData.collectAsState()
    val pagerState = rememberPagerState(pageCount = {
        weatherDataList.size
    })
    val currentPage by viewModel.currentPage.collectAsState()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentPage(pagerState.currentPage)
    }

    LaunchedEffect(currentPage) {
        pagerState.scrollToPage(currentPage)
    }

    Column(
        modifier = Modifier
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
        ) { page ->
            WeatherCard(weatherData = weatherDataList[page])
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun WeatherCard(weatherData: WeatherData) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = weatherData.cityName, modifier = Modifier.padding(8.dp))
            Text(
                text = "Temperature: ${weatherData.temperature}",
                modifier = Modifier.padding(8.dp)
            )
            AsyncImage(
                model = weatherData.iconUrl,
                contentDescription = "Weather Icon",
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .align(
                        alignment = androidx.compose.ui.Alignment.CenterHorizontally
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    val weatherData = WeatherData(
        cityName = "San Francisco",
        temperature = 20.0,
        description = "Sunny",
        iconUrl = "https://openweathermap.org/img/wn/10d@2x.png",
    )
    WeatherCard(weatherData = weatherData)
}