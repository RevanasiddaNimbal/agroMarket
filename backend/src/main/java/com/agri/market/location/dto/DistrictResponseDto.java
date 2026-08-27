package com.agri.market.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "District location information")
public class DistrictResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Unique identifier of the district",
            example = "550e8400-e29b-41d4-a716-446655440001"
    )
    private String id;

    @JsonProperty("name")
    @Schema(
            description = "District name",
            example = "Vijayapura"
    )
    private String name;

    @JsonProperty("code")
    @Schema(
            description = "District code",
            example = "VJP"
    )
    private String code;

    @JsonProperty("state_id")
    @Schema(
            description = "Identifier of the parent state",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String stateId;
}