package com.example.simpleweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpleweather.ui.WeatherViewModel
import com.example.simpleweather.ui.components.HomeScreen
import com.example.simpleweather.ui.components.SearchScreen
import com.example.simpleweather.ui.theme.SimpleWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleWeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimpleWeatherApp()
                }
            }
        }
    }
}

@Composable
fun SimpleWeatherApp(
    vm: WeatherViewModel = viewModel(factory = WeatherViewModel.factory),
    navController: NavHostController = rememberNavController()
) {

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = vm,
                modifier = Modifier,
                onSearchPressedCallback = {
                    navController.navigate("Search")
                }
            )
        }

        composable("Search") {
            SearchScreen(
                viewModel = vm,
                modifier = Modifier,
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}
