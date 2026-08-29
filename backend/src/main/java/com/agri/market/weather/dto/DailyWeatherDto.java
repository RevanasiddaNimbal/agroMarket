package com.agri.market.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Daily weather forecast information relevant to farmers")
public class DailyWeatherDto {

    @Schema(
            description = "Forecast date",
            example = "2026-08-29"
    )
    private String date;

    @Schema(
            description = "Minimum temperature in degrees Celsius",
            example = "22.4"
    )
    private double minimumTemperatureCelsius;

    @Schema(
            description = "Maximum temperature in degrees Celsius",
            example = "29.8"
    )
    private double maximumTemperatureCelsius;

    @Schema(
            description = "Daytime apparent temperature in degrees Celsius",
            example = "31.2"
    )
    private double apparentTemperatureCelsius;

    @Schema(
            description = "Probability of precipitation percentage",
            example = "70.0"
    )
    private double precipitationProbabilityPercent;

    @Schema(
            description = "Total precipitation amount in millimeters",
            example = "8.4"
    )
    private double precipitationMillimeters;

    @Schema(
            description = "Total rain amount in millimeters",
            example = "7.8"
    )
    private double rainMillimeters;

    @Schema(
            description = "Open-Meteo weather condition code",
            example = "63"
    )
    private int weatherCode;

    @Schema(
            description = "Human-readable weather condition",
            example = "Moderate rain"
    )
    private String weatherCondition;

    @Schema(
            description = "Maximum wind speed in kilometers per hour",
            example = "24.6"
    )
    private double maximumWindSpeedKmh;

    @Schema(
            description = "Maximum wind gust speed in kilometers per hour",
            example = "39.2"
    )
    private double maximumWindGustsKmh;

    @Schema(
            description = "Dominant wind direction in degrees",
            example = "245"
    )
    private double dominantWindDirectionDegrees;

    @Schema(
            description = "Maximum UV index for the day",
            example = "6.7"
    )
    private double uvIndexMax;

    @Schema(
            description = "Reference evapotranspiration (ET₀) in millimeters",
            example = "4.2"
    )
    private double evapotranspirationMillimeters;

    @Schema(
            description = "Total sunshine duration in hours",
            example = "6.5"
    )
    private double sunshineDurationHours;

    @Schema(
            description = "Sunrise time",
            example = "2026-08-29T06:08"
    )
    private String sunrise;

    @Schema(
            description = "Sunset time",
            example = "2026-08-29T18:39"
    )
    private String sunset;
}