import com.example.simpleweather.data.Coord
import com.example.simpleweather.data.Main
import com.example.simpleweather.data.Weather
import com.example.simpleweather.data.WeatherResponse
import com.example.simpleweather.database.WeatherDao
import com.example.simpleweather.database.WeatherEntity
import com.example.simpleweather.network.WeatherService
import com.example.simpleweather.ui.WeatherViewModel
import com.example.simpleweather.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    @Mock
    private lateinit var mockWeatherService: WeatherService

    @Mock
    private lateinit var mockLocationProvider: FusedLocationProviderClient

    @Mock
    private lateinit var mockWeatherDao: WeatherDao

    private lateinit var viewModel: WeatherViewModel

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(mockWeatherDao, mockWeatherService, mockLocationProvider)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFetchAndStoreWeatherData() = runBlocking {
        // Given
        val latitude = 1.0
        val longitude = 2.0
        val name = "Test City"
        val iconName = "testIcon"
        val temperature = 20.0
        val description = "Test Description"
        val coord = Coord(lat = latitude, lon = longitude)
        // To refactor for testability
        val apiKey = Constants.WEATHER_API_KEY
        val iconUrl = Constants.WEATHER_ICON_BASE_URL + iconName + "@4x.png"

        val weather = Weather(
            id = 1,
            main = "Test Main",
            description = description,
            icon = iconName
        )
        val main = Main(temp = temperature)
        val weatherResponse = WeatherResponse(
            coord = coord,
            weather = listOf(weather),
            main = main,
            name = name
        )

        Mockito.`when`(
            mockWeatherService.getWeatherByLatLon(
                latitude.toString(),
                longitude.toString(),
                apiKey = apiKey
            )
        ).thenReturn(Response.success(weatherResponse))

        // When
        viewModel.fetchAndStoreWeatherData(latitude, longitude)

        // Then
        Mockito.verify(mockWeatherService)
            .getWeatherByLatLon(
                latitude.toString(),
                longitude.toString(),
                apiKey = apiKey
            )
        Mockito.verify(mockWeatherDao)
            .insert(argThat<WeatherEntity> {
                this.cityName == name &&
                        this.temperature == temperature &&
                        this.description == description &&
                        this.iconUrl == iconUrl &&
                        this.lat == latitude &&
                        this.lon == longitude
            })
    }

    @Test
    fun testErrorHandlingWhenFetchingData() = runBlocking {
        // Given
        val latitude = 1.0
        val longitude = 2.0
        val apiKey = Constants.WEATHER_API_KEY
        val errorMessage = "API request failed"

        Mockito.`when`(
            mockWeatherService.getWeatherByLatLon(
                latitude.toString(),
                longitude.toString(),
                apiKey = apiKey
            )
        ).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.fetchAndStoreWeatherData(latitude, longitude)

        // Then
        assertEquals(errorMessage, viewModel.error.value)
    }
}

