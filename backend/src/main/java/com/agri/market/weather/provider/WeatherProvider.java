package com.agri.market.weather.provider;

import com.agri.market.weather.dto.DailyWeatherResponseDto;
import com.agri.market.weather.dto.HourlyWeatherResponseDto;

public interface WeatherProvider {

    DailyWeatherResponseDto getDailyWeather(
            double latitude,
            double longitude
    );

    HourlyWeatherResponseDto getHourlyWeather(
            double latitude,
            double longitude
    );
}