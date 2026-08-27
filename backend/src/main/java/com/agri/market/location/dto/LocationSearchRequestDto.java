package com.agri.market.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request parameters used to search locations")
public class LocationSearchRequestDto {

    @NotBlank(message = "VALIDATION.LOCATION.SEARCH.QUERY.NOT_BLANK")
    @Size(
            min = 2,
            max = 100,
            message = "VALIDATION.LOCATION.SEARCH.QUERY.SIZE"
    )
    @Schema(
            description = "Location name or keyword to search",
            example = "Vijayapura",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String query;
}