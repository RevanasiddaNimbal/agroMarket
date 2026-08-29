package com.agri.market.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hourly weather forecast information relevant to farmers")
public class HourlyWeatherResponseDto {

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
            description = "Timezone used for the forecast",
            example = "Asia/Kolkata"
    )
    private String timezone;

    @Schema(
            description = "List of hourly weather forecast information"
    )
    private List<HourlyWeatherDto> hourlyForecast;
}