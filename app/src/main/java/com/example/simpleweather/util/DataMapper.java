package com.example.simpleweather.util;

import com.example.simpleweather.data.CityData;
import com.example.simpleweather.data.Location;
import com.example.simpleweather.data.WeatherData;
import com.example.simpleweather.data.WeatherResponse;
import com.example.simpleweather.database.WeatherEntity;

public class DataMapper {

    public static CityData toCityData(Location location) {
        return new CityData(
                location.getName(),
                location.getState(),
                location.getCountry(),
                Double.parseDouble(location.getLat()),
                Double.parseDouble(location.getLon())
        );
    }

    public static WeatherEntity toWeatherEntity(WeatherResponse weatherResponse) {
        String iconUrl = Constants.WEATHER_ICON_BASE_URL + weatherResponse.getWeather().get(0).getIcon() + "@4x.png";
        return new WeatherEntity(
                weatherResponse.getName(),
                weatherResponse.getCoord().getLat(),
                weatherResponse.getCoord().getLon(),
                weatherResponse.getMain().getTemp(),
                weatherResponse.getWeather().get(0).getDescription(),
                iconUrl,
                System.currentTimeMillis()
        );
    }

    public static WeatherData toWeatherData(WeatherEntity weatherEntity) {
        return new WeatherData(
                weatherEntity.getCityName(),
                weatherEntity.getTemperature(),
                weatherEntity.getDescription(),
                weatherEntity.getIconUrl()
        );
    }

}
