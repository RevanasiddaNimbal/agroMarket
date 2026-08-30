package com.agri.market.cropinfo.provider;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.cropinfo.config.CropInfoProperties;
import com.agri.market.cropinfo.model.PerenualPlantResponse;
import com.agri.market.cropinfo.model.PerenualPlantSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerenualCropInfoProvider implements CropInfoProvider {

    private final RestClient perenualRestClient;
    private final CropInfoProperties properties;

    @Override
    public List<PerenualPlantResponse> searchCrops(String query) {
        log.info(
                "Searching crop information from Perenual for query: {}",
                query
        );

        try {
            ResponseEntity<PerenualPlantSearchResponse> response =
                    perenualRestClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/species-list")
                                    .queryParam("key", properties.getApiKey())
                                    .queryParam("q", query)
                                    .queryParam("page", 1)
                                    .build())
                            .retrieve()
                            .toEntity(PerenualPlantSearchResponse.class);

            PerenualPlantSearchResponse body = response.getBody();

            if (body == null
                    || body.getData() == null
                    || body.getData().isEmpty()) {

                log.warn(
                        "Perenual returned no crop information for query: {}",
                        query
                );

                return Collections.emptyList();
            }

            log.info(
                    "Perenual returned {} crop results for query: {}",
                    body.getData().size(),
                    query
            );

            return body.getData();

        } catch (Exception exception) {
            log.error(
                    "Failed to search crop information from Perenual for query: {}",
                    query,
                    exception
            );

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

    @Override
    public PerenualPlantResponse getCropDetails(Integer providerCropId) {
        log.info(
                "Fetching detailed crop information from Perenual for provider crop ID: {}",
                providerCropId
        );

        try {
            ResponseEntity<PerenualPlantResponse> response =
                    perenualRestClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/species/details/{id}")
                                    .queryParam("key", properties.getApiKey())
                                    .build(providerCropId))
                            .retrieve()
                            .toEntity(PerenualPlantResponse.class);

            PerenualPlantResponse crop = response.getBody();

            if (crop == null) {
                log.warn(
                        "Perenual returned empty crop details for provider crop ID: {}",
                        providerCropId
                );

                return null;
            }

            log.info(
                    "Successfully fetched detailed crop information from Perenual for provider crop ID: {}",
                    providerCropId
            );

            return crop;

        } catch (Exception exception) {
            log.error(
                    "Failed to fetch crop details from Perenual for provider crop ID: {}",
                    providerCropId,
                    exception
            );

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }
}