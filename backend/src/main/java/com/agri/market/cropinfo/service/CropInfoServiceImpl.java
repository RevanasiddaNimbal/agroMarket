package com.agri.market.cropinfo.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.cropinfo.dto.CropInfoResponseDto;
import com.agri.market.cropinfo.dto.CropSearchResponseDto;
import com.agri.market.cropinfo.dto.CropSummaryDto;
import com.agri.market.cropinfo.entity.CropInfo;
import com.agri.market.cropinfo.mapper.CropInfoMapper;
import com.agri.market.cropinfo.model.PerenualPlantResponse;
import com.agri.market.cropinfo.provider.CropInfoProvider;
import com.agri.market.cropinfo.repository.CropInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CropInfoServiceImpl implements CropInfoService {

    private static final List<String> DEFAULT_CROPS = List.of(
            "rice",
            "wheat",
            "tomato",
            "potato"
    );
    private final CropInfoRepository cropInfoRepository;
    private final CropInfoProvider cropInfoProvider;
    private final CropInfoMapper cropInfoMapper;

    @Override
    @Transactional
    public List<CropSummaryDto> getFeaturedCrops() {
        log.info("Fetching featured crop information");

        List<CropInfo> storedCrops = cropInfoRepository.findAll();

        if (!storedCrops.isEmpty()) {
            log.info(
                    "Found {} crop records in database",
                    storedCrops.size()
            );

            return storedCrops.stream()
                    .map(cropInfoMapper::toSummaryDto)
                    .toList();
        }

        log.info(
                "No crop information found in database. Loading default crops from Perenual"
        );

        Map<String, CropInfo> crops = new LinkedHashMap<>();

        for (String cropName : DEFAULT_CROPS) {
            CropInfo cropInfo = fetchAndStoreCrop(cropName);

            if (cropInfo != null) {
                crops.put(
                        cropInfo.getCropName().toLowerCase(Locale.ROOT),
                        cropInfo
                );
            }
        }

        return crops.values()
                .stream()
                .map(cropInfoMapper::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional
    public CropSearchResponseDto searchCrop(String query) {
        validateQuery(query);

        String normalizedQuery = normalizeQuery(query);

        log.info(
                "Crop information search request received for query: {}",
                normalizedQuery
        );

        Optional<CropInfo> storedCrop =
                findStoredCrop(normalizedQuery);

        if (storedCrop.isPresent()) {
            log.info(
                    "Crop information found in database for query: {}",
                    normalizedQuery
            );

            return buildSuccessResponse(
                    normalizedQuery,
                    storedCrop.get(),
                    "Crop information found in database"
            );
        }

        log.info(
                "Crop information not found in database. Calling Perenual for query: {}",
                normalizedQuery
        );

        CropInfo cropInfo = fetchAndStoreCrop(normalizedQuery);

        if (cropInfo == null) {
            log.warn(
                    "No crop information found for query: {}",
                    normalizedQuery
            );

            return CropSearchResponseDto.builder()
                    .found(false)
                    .query(normalizedQuery)
                    .crop(null)
                    .message(
                            "No crop information found for the requested crop"
                    )
                    .build();
        }

        return buildSuccessResponse(
                normalizedQuery,
                cropInfo,
                "Crop information found successfully"
        );
    }

    @Override
    public CropInfoResponseDto getCropDetails(String cropId) {
        validateCropId(cropId);

        log.info(
                "Fetching crop information for ID: {}",
                cropId
        );

        CropInfo cropInfo =
                cropInfoRepository.findById(cropId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Crop information not found for ID: {}",
                                    cropId
                            );

                            return new BusinessException(
                                    ErrorCode.RESOURCE_NOT_FOUND
                            );
                        });

        return cropInfoMapper.toResponseDto(cropInfo);
    }

    private CropInfo fetchAndStoreCrop(String query) {

        List<PerenualPlantResponse> searchResults =
                cropInfoProvider.searchCrops(query);

        if (searchResults == null || searchResults.isEmpty()) {
            return null;
        }

        PerenualPlantResponse matchedCrop =
                findBestMatchingProviderCrop(
                        query,
                        searchResults
                );

        if (matchedCrop == null || matchedCrop.getId() == null) {
            log.warn(
                    "No suitable Perenual crop found for query: {}",
                    query
            );

            return null;
        }

        PerenualPlantResponse detailedCrop =
                cropInfoProvider.getCropDetails(
                        matchedCrop.getId()
                );

        if (!isValidProviderCrop(detailedCrop)) {

            log.warn(
                    "Perenual returned no valid detailed information for query: {}",
                    query
            );

            return null;
        }

        String cropName =
                detailedCrop.getCommon_name().trim();

        Optional<CropInfo> existing =
                cropInfoRepository.findByCropNameIgnoreCase(cropName);

        if (existing.isPresent()) {
            CropInfo cropInfo = existing.get();

            cropInfoMapper.updateEntity(
                    cropInfo,
                    detailedCrop
            );

            CropInfo saved =
                    cropInfoRepository.save(cropInfo);

            log.info(
                    "Updated existing crop information in database: {}",
                    saved.getCropName()
            );

            return saved;
        }

        CropInfo cropInfo =
                cropInfoMapper.toEntity(detailedCrop);

        CropInfo saved =
                cropInfoRepository.save(cropInfo);

        log.info(
                "Saved complete crop information to database: {}",
                saved.getCropName()
        );

        return saved;
    }

    private PerenualPlantResponse findBestMatchingProviderCrop(
            String query,
            List<PerenualPlantResponse> results
    ) {
        String normalizedQuery =
                normalizeQuery(query);

        List<PerenualPlantResponse> validResults =
                results.stream()
                        .filter(this::isValidProviderCrop)
                        .toList();

        Optional<PerenualPlantResponse> exactCommonName =
                validResults.stream()
                        .filter(crop ->
                                normalizeQuery(crop.getCommon_name())
                                        .equals(normalizedQuery)
                        )
                        .findFirst();

        if (exactCommonName.isPresent()) {
            return exactCommonName.get();
        }

        Optional<PerenualPlantResponse> commonNameContains =
                validResults.stream()
                        .filter(crop ->
                                normalizeQuery(crop.getCommon_name())
                                        .contains(normalizedQuery)
                                        || normalizedQuery.contains(
                                        normalizeQuery(
                                                crop.getCommon_name()
                                        )
                                )
                        )
                        .findFirst();

        if (commonNameContains.isPresent()) {
            return commonNameContains.get();
        }

        Optional<PerenualPlantResponse> scientificNameMatch =
                validResults.stream()
                        .filter(crop ->
                                containsIgnoreCase(
                                        crop.getScientific_name(),
                                        normalizedQuery
                                )
                        )
                        .findFirst();

        if (scientificNameMatch.isPresent()) {
            return scientificNameMatch.get();
        }

        Optional<PerenualPlantResponse> aliasMatch =
                validResults.stream()
                        .filter(crop ->
                                containsIgnoreCase(
                                        crop.getOther_name(),
                                        normalizedQuery
                                )
                        )
                        .findFirst();

        if (aliasMatch.isPresent()) {
            return aliasMatch.get();
        }

        return null;
    }

    private Optional<CropInfo> findStoredCrop(String query) {

        Optional<CropInfo> byName =
                cropInfoRepository.findByCropNameIgnoreCase(query);

        if (byName.isPresent()) {
            return byName;
        }

        return cropInfoRepository
                .findByScientificNameIgnoreCase(query);
    }

    private CropSearchResponseDto buildSuccessResponse(
            String query,
            CropInfo cropInfo,
            String message
    ) {
        return CropSearchResponseDto.builder()
                .found(true)
                .query(query)
                .crop(cropInfoMapper.toSummaryDto(cropInfo))
                .message(message)
                .build();
    }

    private boolean containsIgnoreCase(
            List<String> values,
            String query
    ) {
        if (values == null || values.isEmpty()) {
            return false;
        }

        return values.stream()
                .filter(StringUtils::hasText)
                .map(value ->
                        value.trim().toLowerCase(Locale.ROOT)
                )
                .anyMatch(value ->
                        value.equals(query)
                                || value.contains(query)
                                || query.contains(value)
                );
    }

    private boolean isValidProviderCrop(
            PerenualPlantResponse crop
    ) {
        return crop != null
                && crop.getId() != null
                && StringUtils.hasText(crop.getCommon_name());
    }

    private String normalizeQuery(String query) {
        return query
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private void validateQuery(String query) {
        if (!StringUtils.hasText(query)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST
            );
        }

        if (query.trim().length() > 100) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST
            );
        }
    }

    private void validateCropId(String cropId) {
        if (!StringUtils.hasText(cropId)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST
            );
        }
    }
}