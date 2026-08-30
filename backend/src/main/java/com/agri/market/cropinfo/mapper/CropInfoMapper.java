package com.agri.market.cropinfo.mapper;

import com.agri.market.cropinfo.dto.CropInfoResponseDto;
import com.agri.market.cropinfo.dto.CropSummaryDto;
import com.agri.market.cropinfo.entity.CropInfo;
import com.agri.market.cropinfo.model.PerenualImage;
import com.agri.market.cropinfo.model.PerenualPlantResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CropInfoMapper {

    public CropInfoResponseDto toResponseDto(CropInfo entity) {
        if (entity == null) {
            return null;
        }

        return CropInfoResponseDto.builder()
                .id(entity.getId())
                .cropName(entity.getCropName())
                .scientificName(entity.getScientificName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .lifeCycle(entity.getLifeCycle())
                .growthStages(entity.getGrowthStages())
                .sowingInfo(entity.getSowingInfo())
                .growingDuration(entity.getGrowingDuration())
                .harvestingInfo(entity.getHarvestingInfo())
                .soilRequirements(entity.getSoilRequirements())
                .waterRequirements(entity.getWaterRequirements())
                .sunlightRequirements(entity.getSunlightRequirements())
                .temperatureRequirements(entity.getTemperatureRequirements())
                .commonPests(entity.getCommonPests())
                .commonDiseases(entity.getCommonDiseases())
                .uses(entity.getUses())
                .build();
    }

    public CropSummaryDto toSummaryDto(CropInfo entity) {
        if (entity == null) {
            return null;
        }

        return CropSummaryDto.builder()
                .id(entity.getId())
                .cropName(entity.getCropName())
                .scientificName(entity.getScientificName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public CropInfo toEntity(PerenualPlantResponse response) {
        if (response == null) {
            return null;
        }

        return CropInfo.builder()
                .cropName(normalize(response.getCommon_name()))
                .scientificName(getScientificName(response.getScientific_name()))
                .description(response.getDescription())
                .imageUrl(getImageUrl(response.getDefault_image()))
                .lifeCycle(response.getCycle())
                .growthStages(null)
                .sowingInfo(null)
                .growingDuration(response.getGrowth_rate())
                .harvestingInfo(buildHarvestingInfo(
                        response.getHarvest_season(),
                        response.getHarvest_method()
                ))
                .soilRequirements(getSoilRequirements(response.getSoil()))
                .waterRequirements(response.getWatering())
                .sunlightRequirements(getSunlightRequirements(response.getSunlight()))
                .temperatureRequirements(null)
                .commonPests(getPests(response.getPest_susceptibility()))
                .commonDiseases(null)
                .uses(null)
                .build();
    }

    public void updateEntity(CropInfo entity, PerenualPlantResponse response) {
        if (entity == null || response == null) {
            return;
        }

        if (hasText(response.getCommon_name())) {
            entity.setCropName(normalize(response.getCommon_name()));
        }

        String scientificName =
                getScientificName(response.getScientific_name());

        if (hasText(scientificName)) {
            entity.setScientificName(scientificName);
        }

        if (hasText(response.getDescription())) {
            entity.setDescription(response.getDescription());
        }

        String imageUrl = getImageUrl(response.getDefault_image());

        if (hasText(imageUrl)) {
            entity.setImageUrl(imageUrl);
        }

        if (hasText(response.getCycle())) {
            entity.setLifeCycle(response.getCycle());
        }

        if (hasText(response.getGrowth_rate())) {
            entity.setGrowingDuration(response.getGrowth_rate());
        }

        String harvestingInfo = buildHarvestingInfo(
                response.getHarvest_season(),
                response.getHarvest_method()
        );

        if (hasText(harvestingInfo)) {
            entity.setHarvestingInfo(harvestingInfo);
        }

        String soilRequirements =
                getSoilRequirements(response.getSoil());

        if (hasText(soilRequirements)) {
            entity.setSoilRequirements(soilRequirements);
        }

        if (hasText(response.getWatering())) {
            entity.setWaterRequirements(response.getWatering());
        }

        String sunlightRequirements =
                getSunlightRequirements(response.getSunlight());

        if (hasText(sunlightRequirements)) {
            entity.setSunlightRequirements(sunlightRequirements);
        }

        String pests =
                getPests(response.getPest_susceptibility());

        if (hasText(pests)) {
            entity.setCommonPests(pests);
        }
    }

    private String getScientificName(List<String> scientificNames) {
        if (scientificNames == null || scientificNames.isEmpty()) {
            return null;
        }

        return scientificNames.stream()
                .filter(this::hasText)
                .findFirst()
                .map(String::trim)
                .orElse(null);
    }

    private String getImageUrl(PerenualImage image) {
        if (image == null) {
            return null;
        }

        if (hasText(image.getRegular_url())) {
            return image.getRegular_url();
        }

        if (hasText(image.getMedium_url())) {
            return image.getMedium_url();
        }

        if (hasText(image.getOriginal_url())) {
            return image.getOriginal_url();
        }

        if (hasText(image.getSmall_url())) {
            return image.getSmall_url();
        }

        return image.getThumbnail();
    }

    private String getSoilRequirements(List<String> soil) {
        if (soil == null || soil.isEmpty()) {
            return null;
        }

        return soil.stream()
                .filter(this::hasText)
                .map(String::trim)
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);
    }

    private String getSunlightRequirements(List<String> sunlight) {
        if (sunlight == null || sunlight.isEmpty()) {
            return null;
        }

        return sunlight.stream()
                .filter(this::hasText)
                .map(String::trim)
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);
    }

    private String getPests(List<String> pests) {
        if (pests == null || pests.isEmpty()) {
            return null;
        }

        return pests.stream()
                .filter(this::hasText)
                .map(String::trim)
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);
    }

    private String buildHarvestingInfo(
            String harvestSeason,
            String harvestMethod
    ) {
        boolean hasSeason = hasText(harvestSeason);
        boolean hasMethod = hasText(harvestMethod);

        if (!hasSeason && !hasMethod) {
            return null;
        }

        if (hasSeason && hasMethod) {
            return "Season: " + harvestSeason.trim()
                    + ", Method: " + harvestMethod.trim();
        }

        if (hasSeason) {
            return "Season: " + harvestSeason.trim();
        }

        return "Method: " + harvestMethod.trim();
    }

    private String normalize(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}