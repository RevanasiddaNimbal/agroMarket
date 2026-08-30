package com.agri.market.cropinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Crop growth stage information")
public class CropGrowthStageDto {

    @Schema(
            description = "Name of the growth stage",
            example = "Germination"
    )
    private String stage;

    @Schema(
            description = "Description of the growth stage"
    )
    private String description;

    @Schema(
            description = "Duration of the growth stage",
            example = "7-14 days"
    )
    private String duration;
}