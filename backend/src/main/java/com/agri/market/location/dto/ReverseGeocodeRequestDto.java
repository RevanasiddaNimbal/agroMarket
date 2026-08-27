package com.agri.market.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Coordinates used for reverse geocoding")
public class ReverseGeocodeRequestDto {

    @NotNull(message = "VALIDATION.LOCATION.LATITUDE.NOT_NULL")
    @DecimalMin(
            value = "-90.0",
            message = "VALIDATION.LOCATION.LATITUDE.INVALID"
    )
    @DecimalMax(
            value = "90.0",
            message = "VALIDATION.LOCATION.LATITUDE.INVALID"
    )
    @Schema(
            description = "Latitude coordinate",
            example = "16.8302",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double latitude;

    @NotNull(message = "VALIDATION.LOCATION.LONGITUDE.NOT_NULL")
    @DecimalMin(
            value = "-180.0",
            message = "VALIDATION.LOCATION.LONGITUDE.INVALID"
    )
    @DecimalMax(
            value = "180.0",
            message = "VALIDATION.LOCATION.LONGITUDE.INVALID"
    )
    @Schema(
            description = "Longitude coordinate",
            example = "75.7100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double longitude;
}