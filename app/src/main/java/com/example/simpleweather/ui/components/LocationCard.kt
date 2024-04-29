package com.example.simpleweather.ui.components

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simpleweather.R
import com.example.simpleweather.ui.WeatherViewModel

@Composable
fun LocationCard(
    viewModel: WeatherViewModel,
    onPermissionDenied: () -> Unit
) {
    val locationPermissionRequest =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.fetchWeatherDataWithPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(300.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.location_card_text),
                modifier = Modifier.padding(8.dp)
            )

            IconButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { locationPermissionRequest.launch(ACCESS_COARSE_LOCATION) }
            ) {
                Icon(
                    Icons.Default.LocationOn, contentDescription = "Search",
                    Modifier
                        .size(32.dp)
                        .align(
                            Alignment.CenterHorizontally
                        )
                )
            }

            TextButton(
                onClick = { locationPermissionRequest.launch(ACCESS_COARSE_LOCATION) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.enable_location))
            }
        }
    }
}
