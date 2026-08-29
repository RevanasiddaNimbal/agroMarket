package com.agri.market.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hourly weather forecast information relevant to farmers")
public class HourlyWeatherDto {

    @Schema(
            description = "Forecast date and time",
            example = "2026-08-29T14:00"
    )
    private String time;

    @Schema(
            description = "Forecast temperature in degrees Celsius",
            example = "27.8"
    )
    private double temperatureCelsius;

    @Schema(
            description = "Apparent temperature in degrees Celsius",
            example = "29.4"
    )
    private double apparentTemperatureCelsius;

    @Schema(
            description = "Relative humidity percentage",
            example = "76.0"
    )
    private double relativeHumidityPercent;

    @Schema(
            description = "Probability of precipitation percentage",
            example = "65"
    )
    private int precipitationProbabilityPercent;

    @Schema(
            description = "Precipitation amount in millimeters",
            example = "1.8"
    )
    private double precipitationMillimeters;

    @Schema(
            description = "Rain amount in millimeters",
            example = "1.5"
    )
    private double rainMillimeters;

    @Schema(
            description = "Open-Meteo weather condition code",
            example = "61"
    )
    private int weatherCode;

    @Schema(
            description = "Human-readable weather condition",
            example = "Slight rain"
    )
    private String weatherCondition;

    @Schema(
            description = "Wind speed in kilometers per hour",
            example = "15.2"
    )
    private double windSpeedKmh;

    @Schema(
            description = "Wind gust speed in kilometers per hour",
            example = "27.6"
    )
    private double windGustsKmh;

    @Schema(
            description = "Wind direction in degrees",
            example = "240"
    )
    private double windDirectionDegrees;

    @Schema(
            description = "UV index",
            example = "4.8"
    )
    private double uvIndex;

    @Schema(
            description = "Reference evapotranspiration (ET₀) in millimeters",
            example = "0.18"
    )
    private double evapotranspirationMillimeters;

    @Schema(
            description = "Soil temperature at 0 to 6 centimeters depth in degrees Celsius",
            example = "25.6"
    )
    private double soilTemperatureCelsius;

    @Schema(
            description = "Soil moisture at 0 to 1 centimeter depth",
            example = "0.32"
    )
    private double soilMoisture;
}