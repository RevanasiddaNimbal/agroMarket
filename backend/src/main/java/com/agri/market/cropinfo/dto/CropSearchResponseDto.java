package com.agri.market.cropinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Crop search response")
public class CropSearchResponseDto {

    @Schema(
            description = "Whether a matching crop was found",
            example = "true"
    )
    private boolean found;

    @Schema(
            description = "Original search query",
            example = "tomato"
    )
    private String query;

    @Schema(description = "Matching crop information")
    private CropSummaryDto crop;

    @Schema(
            description = "Response message",
            example = "Crop information found successfully"
    )
    private String message;
}