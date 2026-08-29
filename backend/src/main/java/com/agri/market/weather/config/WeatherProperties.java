package com.agri.market.weather.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "weather.open-meteo")
public class WeatherProperties {

    private String baseUrl = "https://api.open-meteo.com/v1/forecast";

    private int forecastDays = 7;

    private int connectTimeoutSeconds = 5;

    private int readTimeoutSeconds = 10;
}