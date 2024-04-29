package com.example.simpleweather.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simpleweather.R
import com.example.simpleweather.data.CityData
import com.example.simpleweather.ui.WeatherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier,
    navigateUp: () -> Unit = {},
) {

    val searchQuery by viewModel.searchString.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val error by viewModel.error.collectAsState()

    val locationPermissionDeniedMessage = stringResource(id = R.string.location_permission_denied)
    val locationPermissionRequest =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.fetchWeatherDataWithPermissionGranted()
                navigateUp()
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(locationPermissionDeniedMessage)
                }
            }
        }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.my_cities)) },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),

                    navigationIcon = {
                        IconButton(onClick = {
                            navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )

                        }
                    },
                    actions = {
                        IconButton(onClick = { locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = stringResource(id = R.string.location)
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.updateSearchQuery(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.city_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.updateSearchQuery("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.clear)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues)
        ) {
            CityList(cities = searchResults) {
                viewModel.fetchWeatherDataByCoordinates(it.lat, it.lon)
                navigateUp()
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
}

@Composable
fun CityList(cities: List<CityData>, onItemClicked: (CityData) -> Unit) {
    LazyColumn {
        items(cities) { cityData ->
            CityItem(cityData = cityData, onItemClicked = onItemClicked)
        }
    }
}

@Composable
fun CityItem(cityData: CityData, onItemClicked: (CityData) -> Unit) {
    Box(modifier = Modifier
        .clickable { onItemClicked(cityData) }
        .padding(16.dp)) {
        Text(text = cityData.cityName + ", " + cityData.state + ", " + cityData.country)
    }
}
