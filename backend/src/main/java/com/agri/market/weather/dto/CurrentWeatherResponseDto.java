package com.agri.market.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current weather information relevant to farmers")
public class CurrentWeatherResponseDto {

    @Schema(
            description = "Latitude of the requested location",
            example = "15.3647"
    )
    private double latitude;

    @Schema(
            description = "Longitude of the requested location",
            example = "75.1240"
    )
    private double longitude;

    @Schema(
            description = "Time of the current weather observation",
            example = "2026-08-29T12:00"
    )
    private String observationTime;

    @Schema(
            description = "Current temperature in degrees Celsius",
            example = "28.5"
    )
    private double temperatureCelsius;

    @Schema(
            description = "Feels-like temperature in degrees Celsius",
            example = "30.2"
    )
    private double apparentTemperatureCelsius;

    @Schema(
            description = "Relative humidity percentage",
            example = "72.0"
    )
    private double relativeHumidityPercent;

    @Schema(
            description = "Current precipitation amount in millimeters",
            example = "0.0"
    )
    private double precipitationMillimeters;

    @Schema(
            description = "Current rain amount in millimeters",
            example = "0.0"
    )
    private double rainMillimeters;

    @Schema(
            description = "Open-Meteo weather condition code",
            example = "3"
    )
    private int weatherCode;

    @Schema(
            description = "Human-readable weather condition",
            example = "Overcast"
    )
    private String weatherCondition;

    @Schema(
            description = "Current wind speed in kilometers per hour",
            example = "12.5"
    )
    private double windSpeedKmh;

    @Schema(
            description = "Current wind direction in degrees",
            example = "245.0"
    )
    private double windDirectionDegrees;

    @Schema(
            description = "Current wind gust speed in kilometers per hour",
            example = "22.4"
    )
    private double windGustsKmh;

    @Schema(
            description = "Current UV index",
            example = "5.4"
    )
    private double uvIndex;
}