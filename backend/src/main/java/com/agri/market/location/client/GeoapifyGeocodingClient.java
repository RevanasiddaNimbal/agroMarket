package com.agri.market.location.client;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

import static com.agri.market.common.exception.ErrorCode.GEOCODING_FAILED;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeoapifyGeocodingClient implements GeocodingClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${geocoding.geoapify.base-url}")
    private String baseUrl;

    @Value("${geocoding.geoapify.api-key}")
    private String apiKey;

    @Value("${geocoding.geoapify.language:en}")
    private String language;

    @Value("${geocoding.geoapify.search-limit:5}")
    private int searchLimit;

    @Override
    public List<GeocodingResult> search(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        try {
            GeoapifyResponse response = createRestClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/geocode/search")
                            .queryParam("text", query.trim())
                            .queryParam("lang", language)
                            .queryParam("limit", searchLimit)
                            .queryParam("filter", "countrycode:in")
                            .queryParam("format", "json")
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(GeoapifyResponse.class);

            if (response == null) {
                log.warn("Geoapify forward geocoding returned null response for query={}", query);
                return Collections.emptyList();
            }

            if (response.getResults() == null || response.getResults().isEmpty()) {
                log.debug("Geoapify returned no forward geocoding results for query={}", query);
                return Collections.emptyList();
            }

            return response.getResults()
                    .stream()
                    .filter(feature -> feature != null)
                    .map(this::toGeocodingResult)
                    .toList();

        } catch (Exception exception) {
            log.error("Geoapify forward geocoding request failed for query={}", query, exception);
            throw new BusinessException(GEOCODING_FAILED);
        }
    }

    @Override
    public GeocodingResult reverseGeocode(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);

        try {
            log.debug("Calling Geoapify reverse geocoding for latitude={}, longitude={}", latitude, longitude);

            GeoapifyResponse response = createRestClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/geocode/reverse")
                            .queryParam("lat", latitude)
                            .queryParam("lon", longitude)
                            .queryParam("lang", language)
                            .queryParam("limit", 1)
                            .queryParam("format", "json")
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(GeoapifyResponse.class);

            if (response == null) {
                log.warn("Geoapify reverse geocoding returned null response for latitude={}, longitude={}", latitude, longitude);
                throw new BusinessException(ErrorCode.LOCATION_RESOLUTION_FAILED);
            }

            if (response.getResults() == null || response.getResults().isEmpty()) {
                log.warn("Geoapify reverse geocoding returned no results for latitude={}, longitude={}", latitude, longitude);
                throw new BusinessException(ErrorCode.LOCATION_RESOLUTION_FAILED);
            }

            GeoapifyFeature feature = response.getResults().getFirst();

            if (feature == null) {
                log.warn("Geoapify reverse geocoding returned null feature for latitude={}, longitude={}", latitude, longitude);
                throw new BusinessException(ErrorCode.LOCATION_RESOLUTION_FAILED);
            }

            log.debug(
                    "Geoapify reverse result: formatted={}, state={}, county={}, stateDistrict={}, district={}, city={}, village={}, postcode={}",
                    feature.getFormatted(), feature.getState(), feature.getCounty(),
                    feature.getStateDistrict(), feature.getDistrict(), feature.getCity(),
                    feature.getVillage(), feature.getPostcode()
            );

            return toGeocodingResult(feature);

        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("Geoapify reverse geocoding request failed for latitude={}, longitude={}", latitude, longitude, exception);
            throw new BusinessException(GEOCODING_FAILED);
        }
    }

    private RestClient createRestClient() {
        if (baseUrl == null || baseUrl.isBlank()) {
            log.error("Geoapify base URL is missing");
            throw new BusinessException(GEOCODING_FAILED);
        }

        if (apiKey == null || apiKey.isBlank()) {
            log.error("Geoapify API key is missing");
            throw new BusinessException(GEOCODING_FAILED);
        }

        return restClientBuilder.baseUrl(baseUrl.trim()).build();
    }

    private GeocodingResult toGeocodingResult(GeoapifyFeature feature) {
        if (feature == null) {
            throw new BusinessException(ErrorCode.LOCATION_RESOLUTION_FAILED);
        }

        String city = firstNonBlank(feature.getCity());
        String village = firstNonBlank(feature.getVillage(), feature.getSuburb());
        String district = firstNonBlank(feature.getDistrict());
        String county = firstNonBlank(feature.getCounty());
        String stateDistrict = firstNonBlank(feature.getStateDistrict());

        return GeocodingResult.builder()
                .latitude(feature.getLat())
                .longitude(feature.getLon())
                .country(firstNonBlank(feature.getCountry()))
                .countryCode(firstNonBlank(feature.getCountryCode()))
                .state(firstNonBlank(feature.getState()))
                .district(district)
                .taluk(null)
                .village(village)
                .city(city)
                .pincode(firstNonBlank(feature.getPostcode()))
                .displayName(firstNonBlank(feature.getFormatted()))
                .county(county)
                .stateDistrict(stateDistrict)
                .build();
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new BusinessException(ErrorCode.LOCATION_RESOLUTION_FAILED);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new BusinessException(ErrorCode.LOCATION_RESOLUTION_FAILED);
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}