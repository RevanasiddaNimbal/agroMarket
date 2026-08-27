package com.agri.market.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Taluk location information")
public class TalukResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Unique identifier of the taluk",
            example = "550e8400-e29b-41d4-a716-446655440002"
    )
    private String id;

    @JsonProperty("name")
    @Schema(
            description = "Taluk name",
            example = "Indi"
    )
    private String name;

    @JsonProperty("code")
    @Schema(
            description = "Taluk code",
            example = "IND"
    )
    private String code;

    @JsonProperty("district_id")
    @Schema(
            description = "Identifier of the parent district",
            example = "550e8400-e29b-41d4-a716-446655440001"
    )
    private String districtId;
}