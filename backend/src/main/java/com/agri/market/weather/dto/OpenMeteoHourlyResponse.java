package com.agri.market.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenMeteoHourlyResponse {

    private double latitude;

    private double longitude;

    private String timezone;

    private OpenMeteoHourlyData hourly;

    @Getter
    @Setter
    public static class OpenMeteoHourlyData {

        private List<String> time;

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;

        @JsonProperty("apparent_temperature")
        private List<Double> apparentTemperature;

        @JsonProperty("relative_humidity_2m")
        private List<Double> relativeHumidity2m;

        @JsonProperty("precipitation_probability")
        private List<Integer> precipitationProbability;

        private List<Double> precipitation;

        private List<Double> rain;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        @JsonProperty("wind_speed_10m")
        private List<Double> windSpeed10m;

        @JsonProperty("wind_gusts_10m")
        private List<Double> windGusts10m;

        @JsonProperty("wind_direction_10m")
        private List<Double> windDirection10m;

        @JsonProperty("uv_index")
        private List<Double> uvIndex;

        @JsonProperty("et0_fao_evapotranspiration")
        private List<Double> et0FaoEvapotranspiration;

        @JsonProperty("soil_temperature_0cm")
        private List<Double> soilTemperature0cm;

        @JsonProperty("soil_moisture_0_to_1cm")
        private List<Double> soilMoisture0To1cm;
    }
}