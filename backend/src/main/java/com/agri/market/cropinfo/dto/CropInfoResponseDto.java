package com.agri.market.cropinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed crop information")
public class CropInfoResponseDto {

    @Schema(
            description = "Unique identifier of the crop information",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec"
    )
    private String id;

    @Schema(
            description = "Common name of the crop",
            example = "Tomato"
    )
    private String cropName;

    @Schema(
            description = "Scientific name of the crop",
            example = "Solanum lycopersicum"
    )
    private String scientificName;

    @Schema(
            description = "Description of the crop",
            example = "Tomato is a widely cultivated crop grown for its edible fruits."
    )
    private String description;

    @Schema(
            description = "Image URL of the crop",
            example = "https://example.com/tomato.jpg"
    )
    private String imageUrl;

    @Schema(
            description = "Biological life cycle of the crop",
            example = "Annual"
    )
    private String lifeCycle;

    @Schema(
            description = "Major growth stages of the crop",
            example = "Seed, Germination, Seedling, Vegetative Growth, Flowering, Fruiting, Harvest"
    )
    private String growthStages;

    @Schema(
            description = "Sowing information",
            example = "Sow seeds in well-prepared soil during the recommended growing season."
    )
    private String sowingInfo;

    @Schema(
            description = "Typical growing duration",
            example = "90-120 days"
    )
    private String growingDuration;

    @Schema(
            description = "Harvesting information",
            example = "Harvest mature fruits when they reach the appropriate size and color."
    )
    private String harvestingInfo;

    @Schema(
            description = "Suitable soil requirements",
            example = "Well-drained loamy soil rich in organic matter."
    )
    private String soilRequirements;

    @Schema(
            description = "Water requirements",
            example = "Regular and consistent watering without waterlogging."
    )
    private String waterRequirements;

    @Schema(
            description = "Sunlight requirements",
            example = "Full sunlight"
    )
    private String sunlightRequirements;

    @Schema(
            description = "Temperature requirements",
            example = "20-30°C"
    )
    private String temperatureRequirements;

    @Schema(
            description = "Common pests affecting the crop",
            example = "Aphids, whiteflies, fruit flies"
    )
    private String commonPests;

    @Schema(
            description = "Common diseases affecting the crop",
            example = "Early blight, late blight, powdery mildew"
    )
    private String commonDiseases;

    @Schema(
            description = "Common uses of the crop",
            example = "Fresh consumption, cooking, sauces and processing"
    )
    private String uses;
}