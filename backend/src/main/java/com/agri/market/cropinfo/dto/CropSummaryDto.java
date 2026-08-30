package com.agri.market.cropinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Crop summary for crop listing")
public class CropSummaryDto {

    @Schema(description = "Unique identifier of the crop information")
    private String id;

    @Schema(description = "Common name of the crop", example = "Tomato")
    private String cropName;

    @Schema(description = "Scientific name of the crop", example = "Solanum lycopersicum")
    private String scientificName;

    @Schema(description = "Short description of the crop")
    private String description;

    @Schema(description = "Crop image URL")
    private String imageUrl;
}