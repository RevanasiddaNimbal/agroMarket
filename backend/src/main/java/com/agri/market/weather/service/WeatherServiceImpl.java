package com.agri.market.weather.service;

import com.agri.market.weather.dto.DailyWeatherResponseDto;
import com.agri.market.weather.dto.HourlyWeatherResponseDto;
import com.agri.market.weather.provider.WeatherProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final WeatherProvider weatherProvider;

    @Override
    public DailyWeatherResponseDto getDailyWeather(
            double latitude,
            double longitude
    ) {
        log.info(
                "Fetching daily weather forecast: latitude={}, longitude={}",
                latitude,
                longitude
        );

        DailyWeatherResponseDto response =
                weatherProvider.getDailyWeather(
                        latitude,
                        longitude
                );

        log.info(
                "Daily weather forecast retrieved successfully: latitude={}, longitude={}, forecastDays={}",
                latitude,
                longitude,
                response.getDailyForecast() != null
                        ? response.getDailyForecast().size()
                        : 0
        );

        return response;
    }

    @Override
    public HourlyWeatherResponseDto getHourlyWeather(
            double latitude,
            double longitude
    ) {
        log.info(
                "Fetching hourly weather forecast: latitude={}, longitude={}",
                latitude,
                longitude
        );

        HourlyWeatherResponseDto response =
                weatherProvider.getHourlyWeather(
                        latitude,
                        longitude
                );

        log.info(
                "Hourly weather forecast retrieved successfully: latitude={}, longitude={}, forecastHours={}",
                latitude,
                longitude,
                response.getHourlyForecast() != null
                        ? response.getHourlyForecast().size()
                        : 0
        );

        return response;
    }
}