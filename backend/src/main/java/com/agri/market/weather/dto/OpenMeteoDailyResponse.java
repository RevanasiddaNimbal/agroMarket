package com.agri.market.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenMeteoDailyResponse {

    private double latitude;

    private double longitude;

    private String timezone;

    private OpenMeteoDailyData daily;

    @Getter
    @Setter
    public static class OpenMeteoDailyData {

        private List<String> time;

        @JsonProperty("temperature_2m_min")
        private List<Double> temperature2mMin;

        @JsonProperty("temperature_2m_max")
        private List<Double> temperature2mMax;

        @JsonProperty("apparent_temperature_max")
        private List<Double> apparentTemperatureMax;

        @JsonProperty("precipitation_probability_max")
        private List<Integer> precipitationProbabilityMax;

        @JsonProperty("precipitation_sum")
        private List<Double> precipitationSum;

        @JsonProperty("rain_sum")
        private List<Double> rainSum;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        @JsonProperty("wind_speed_10m_max")
        private List<Double> windSpeed10mMax;

        @JsonProperty("wind_gusts_10m_max")
        private List<Double> windGusts10mMax;

        @JsonProperty("wind_direction_10m_dominant")
        private List<Double> windDirection10mDominant;

        @JsonProperty("uv_index_max")
        private List<Double> uvIndexMax;

        @JsonProperty("et0_fao_evapotranspiration")
        private List<Double> et0FaoEvapotranspiration;

        @JsonProperty("sunshine_duration")
        private List<Double> sunshineDuration;

        private List<String> sunrise;

        private List<String> sunset;
    }
}