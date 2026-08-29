package com.agri.market.weather.service;

import com.agri.market.weather.dto.DailyWeatherResponseDto;
import com.agri.market.weather.dto.HourlyWeatherResponseDto;

public interface WeatherService {

    DailyWeatherResponseDto getDailyWeather(
            double latitude,
            double longitude
    );

    HourlyWeatherResponseDto getHourlyWeather(
            double latitude,
            double longitude
    );
}