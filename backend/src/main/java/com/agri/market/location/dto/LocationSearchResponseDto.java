package com.agri.market.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Location search result")
public class LocationSearchResponseDto {

    @JsonProperty("type")
    @Schema(
            description = "Type of location",
            example = "TALUK",
            allowableValues = {
                    "STATE",
                    "DISTRICT",
                    "TALUK"
            }
    )
    private String type;

    @JsonProperty("id")
    @Schema(
            description = "Unique identifier of the location",
            example = "550e8400-e29b-41d4-a716-446655440002"
    )
    private String id;

    @JsonProperty("name")
    @Schema(
            description = "Location name",
            example = "Indi"
    )
    private String name;

    @JsonProperty("district")
    @Schema(description = "Parent district information")
    private DistrictReferenceDto district;

    @JsonProperty("state")
    @Schema(description = "Parent state information")
    private StateReferenceDto state;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "District reference")
    public static class DistrictReferenceDto {

        @JsonProperty("id")
        @Schema(
                description = "District identifier",
                example = "550e8400-e29b-41d4-a716-446655440001"
        )
        private String id;

        @JsonProperty("name")
        @Schema(
                description = "District name",
                example = "Vijayapura"
        )
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "State reference")
    public static class StateReferenceDto {

        @JsonProperty("id")
        @Schema(
                description = "State identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        private String id;

        @JsonProperty("name")
        @Schema(
                description = "State name",
                example = "Karnataka"
        )
        private String name;
    }
}