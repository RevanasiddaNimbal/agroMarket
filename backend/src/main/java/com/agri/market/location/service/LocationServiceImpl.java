package com.agri.market.location.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.location.client.GeocodingClient;
import com.agri.market.location.client.GeocodingResult;
import com.agri.market.location.dto.*;
import com.agri.market.location.entity.District;
import com.agri.market.location.entity.State;
import com.agri.market.location.mapper.LocationMapper;
import com.agri.market.location.repository.DistrictRepository;
import com.agri.market.location.repository.StateRepository;
import com.agri.market.location.repository.TalukRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final StateRepository stateRepository;

    private final DistrictRepository districtRepository;

    private final TalukRepository talukRepository;

    private final LocationMapper locationMapper;

    private final GeocodingClient geocodingClient;

    private final LocationResolver locationResolver;


    @Override
    public List<StateResponseDto> getActiveStates() {

        log.debug(
                "Fetching active states"
        );

        return stateRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(locationMapper::toStateResponse)
                .toList();
    }


    @Override
    public List<DistrictResponseDto> getActiveDistrictsByState(
            String stateId
    ) {

        log.debug(
                "Fetching active districts for stateId={}",
                stateId
        );

        validateStateExists(stateId);

        return districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc(
                        stateId
                )
                .stream()
                .map(locationMapper::toDistrictResponse)
                .toList();
    }


    @Override
    public List<TalukResponseDto> getActiveTaluksByDistrict(
            String districtId
    ) {

        log.debug(
                "Fetching active taluks for districtId={}",
                districtId
        );

        validateDistrictExists(districtId);

        return talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(
                        districtId
                )
                .stream()
                .map(locationMapper::toTalukResponse)
                .toList();
    }


    @Override
    public List<LocationSearchResponseDto> searchLocations(
            LocationSearchRequestDto request
    ) {

        String query =
                request.getQuery().trim();

        log.debug(
                "Searching active locations with query={}",
                query
        );

        List<LocationSearchResponseDto> results =
                new ArrayList<>();


        // States
        stateRepository
                .searchActiveStates(query)
                .stream()
                .map(locationMapper::toStateSearchResponse)
                .forEach(results::add);


        // Districts
        districtRepository
                .searchActiveDistricts(query)
                .stream()
                .map(locationMapper::toDistrictSearchResponse)
                .forEach(results::add);


        // Taluks
        talukRepository
                .searchActiveTaluks(query)
                .stream()
                .map(locationMapper::toTalukSearchResponse)
                .forEach(results::add);


        return results;
    }


    @Override
    public List<ReverseGeocodeResponseDto> searchExternalLocations(
            String query
    ) {

        if (query == null || query.isBlank()) {

            return List.of();
        }

        String normalizedQuery =
                query.trim();

        log.debug(
                "Searching external locations with query={}",
                normalizedQuery
        );

        return geocodingClient
                .search(normalizedQuery)
                .stream()
                .map(locationMapper::toReverseGeocodeResponse)
                .toList();
    }

    @Override
    public ReverseGeocodeResponseDto reverseGeocode(
            ReverseGeocodeRequestDto request
    ) {

        if (request == null) {

            throw new BusinessException(
                    ErrorCode.INVALID_COORDINATES
            );
        }

        double latitude =
                request.getLatitude();

        double longitude =
                request.getLongitude();


        validateCoordinates(
                latitude,
                longitude
        );


        log.debug(
                "Reverse geocoding latitude={}, longitude={}",
                latitude,
                longitude
        );


        GeocodingResult externalResult =
                geocodingClient.reverseGeocode(
                        latitude,
                        longitude
                );


        if (externalResult == null) {

            log.error(
                    "Geoapify returned null GeocodingResult for latitude={}, longitude={}",
                    latitude,
                    longitude
            );

            throw new BusinessException(
                    ErrorCode.LOCATION_RESOLUTION_FAILED
            );
        }


        log.debug(
                """
                        External geocoding result:
                        latitude={}
                        longitude={}
                        country={}
                        countryCode={}
                        state={}
                        district={}
                        stateDistrict={}
                        county={}
                        taluk={}
                        city={}
                        pincode={}
                        displayName={}
                        """,
                externalResult.getLatitude(),
                externalResult.getLongitude(),
                externalResult.getCountry(),
                externalResult.getCountryCode(),
                externalResult.getState(),
                externalResult.getDistrict(),
                externalResult.getStateDistrict(),
                externalResult.getCounty(),
                externalResult.getTaluk(),
                externalResult.getCity(),
                externalResult.getPincode(),
                externalResult.getDisplayName()
        );


        GeocodingResult resolvedResult =
                locationResolver.resolve(
                        externalResult
                );


        if (resolvedResult == null) {

            log.error(
                    "LocationResolver returned null for latitude={}, longitude={}",
                    latitude,
                    longitude
            );

            throw new BusinessException(
                    ErrorCode.LOCATION_RESOLUTION_FAILED
            );
        }


        log.debug(
                """
                        Resolved location:
                        state={}
                        district={}
                        taluk={}
                        city={}
                        pincode={}
                        """,
                resolvedResult.getState(),
                resolvedResult.getDistrict(),
                resolvedResult.getTaluk(),
                resolvedResult.getCity(),
                resolvedResult.getPincode()
        );


        return locationMapper.toReverseGeocodeResponse(
                resolvedResult
        );
    }


    private void validateStateExists(
            String stateId
    ) {

        if (stateId == null || stateId.isBlank()) {

            throw new BusinessException(
                    ErrorCode.STATE_NOT_FOUND
            );
        }

        State state =
                stateRepository
                        .findById(stateId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.STATE_NOT_FOUND
                                )
                        );

        if (!state.isActive()) {

            throw new BusinessException(
                    ErrorCode.STATE_NOT_FOUND
            );
        }
    }


    private void validateDistrictExists(
            String districtId
    ) {

        if (districtId == null || districtId.isBlank()) {

            throw new BusinessException(
                    ErrorCode.DISTRICT_NOT_FOUND
            );
        }

        District district =
                districtRepository
                        .findById(districtId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.DISTRICT_NOT_FOUND
                                )
                        );

        if (!district.isActive()) {

            throw new BusinessException(
                    ErrorCode.DISTRICT_NOT_FOUND
            );
        }
    }


    private void validateCoordinates(
            double latitude,
            double longitude
    ) {

        if (Double.isNaN(latitude)
                || Double.isInfinite(latitude)
                || Double.isNaN(longitude)
                || Double.isInfinite(longitude)) {

            throw new BusinessException(
                    ErrorCode.INVALID_COORDINATES
            );
        }


        if (latitude < -90.0
                || latitude > 90.0) {

            throw new BusinessException(
                    ErrorCode.INVALID_COORDINATES
            );
        }


        if (longitude < -180.0
                || longitude > 180.0) {

            throw new BusinessException(
                    ErrorCode.INVALID_COORDINATES
            );
        }
    }
}