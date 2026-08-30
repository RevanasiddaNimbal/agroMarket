package com.agri.market.cropinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Crop lifecycle information")
public class CropLifecycleDto {

    @Schema(
            description = "Biological life cycle",
            example = "Annual"
    )
    private String type;

    @Schema(
            description = "Growth stages of the crop"
    )
    private List<CropGrowthStageDto> growthStages;
}