package com.agri.market.location.service;

import com.agri.market.location.dto.*;

import java.util.List;

public interface LocationService {

    List<StateResponseDto> getActiveStates();

    List<DistrictResponseDto> getActiveDistrictsByState(
            String stateId
    );

    List<TalukResponseDto> getActiveTaluksByDistrict(
            String districtId
    );

    List<LocationSearchResponseDto> searchLocations(
            LocationSearchRequestDto request
    );

    List<ReverseGeocodeResponseDto> searchExternalLocations(
            String query
    );

    ReverseGeocodeResponseDto reverseGeocode(
            ReverseGeocodeRequestDto request
    );
}