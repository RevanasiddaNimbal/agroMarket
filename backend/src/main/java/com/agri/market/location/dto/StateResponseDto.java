package com.agri.market.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "State location information")
public class StateResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Unique identifier of the state",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String id;

    @JsonProperty("name")
    @Schema(
            description = "State name",
            example = "Karnataka"
    )
    private String name;

    @JsonProperty("code")
    @Schema(
            description = "State code",
            example = "KA"
    )
    private String code;

    @JsonProperty("country_code")
    @Schema(
            description = "ISO country code",
            example = "IN"
    )
    private String countryCode;
}