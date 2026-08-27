package com.agri.market.location.mapper;

import com.agri.market.location.client.GeocodingResult;
import com.agri.market.location.dto.*;
import com.agri.market.location.entity.District;
import com.agri.market.location.entity.State;
import com.agri.market.location.entity.Taluk;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public StateResponseDto toStateResponse(State state) {
        if (state == null) {
            return null;
        }

        return StateResponseDto.builder()
                .id(state.getId())
                .name(state.getName())
                .code(state.getCode())
                .countryCode(state.getCountryCode())
                .build();
    }

    public DistrictResponseDto toDistrictResponse(District district) {
        if (district == null) {
            return null;
        }

        return DistrictResponseDto.builder()
                .id(district.getId())
                .name(district.getName())
                .code(district.getCode())
                .stateId(
                        district.getState() != null
                                ? district.getState().getId()
                                : null
                )
                .build();
    }

    public TalukResponseDto toTalukResponse(Taluk taluk) {
        if (taluk == null) {
            return null;
        }

        return TalukResponseDto.builder()
                .id(taluk.getId())
                .name(taluk.getName())
                .code(taluk.getCode())
                .districtId(
                        taluk.getDistrict() != null
                                ? taluk.getDistrict().getId()
                                : null
                )
                .build();
    }

    public LocationSearchResponseDto toStateSearchResponse(State state) {
        if (state == null) {
            return null;
        }

        return LocationSearchResponseDto.builder()
                .type("STATE")
                .id(state.getId())
                .name(state.getName())
                .state(
                        LocationSearchResponseDto.StateReferenceDto.builder()
                                .id(state.getId())
                                .name(state.getName())
                                .build()
                )
                .build();
    }

    public LocationSearchResponseDto toDistrictSearchResponse(
            District district
    ) {
        if (district == null) {
            return null;
        }

        State state = district.getState();

        return LocationSearchResponseDto.builder()
                .type("DISTRICT")
                .id(district.getId())
                .name(district.getName())
                .district(
                        LocationSearchResponseDto.DistrictReferenceDto.builder()
                                .id(district.getId())
                                .name(district.getName())
                                .build()
                )
                .state(
                        state != null
                                ? LocationSearchResponseDto.StateReferenceDto.builder()
                                .id(state.getId())
                                .name(state.getName())
                                .build()
                                : null
                )
                .build();
    }

    public LocationSearchResponseDto toTalukSearchResponse(
            Taluk taluk
    ) {
        if (taluk == null) {
            return null;
        }

        District district = taluk.getDistrict();
        State state = district != null
                ? district.getState()
                : null;

        return LocationSearchResponseDto.builder()
                .type("TALUK")
                .id(taluk.getId())
                .name(taluk.getName())
                .district(
                        district != null
                                ? LocationSearchResponseDto.DistrictReferenceDto.builder()
                                .id(district.getId())
                                .name(district.getName())
                                .build()
                                : null
                )
                .state(
                        state != null
                                ? LocationSearchResponseDto.StateReferenceDto.builder()
                                .id(state.getId())
                                .name(state.getName())
                                .build()
                                : null
                )
                .build();
    }

    public ReverseGeocodeResponseDto toReverseGeocodeResponse(
            GeocodingResult result
    ) {
        if (result == null) {
            return null;
        }

        return ReverseGeocodeResponseDto.builder()
                .latitude(result.getLatitude())
                .longitude(result.getLongitude())
                .country(result.getCountry())
                .countryCode(result.getCountryCode())
                .state(result.getState())
                .district(result.getDistrict())
                .taluk(result.getTaluk())
                .city(result.getCity())
                .pincode(result.getPincode())
                .displayName(result.getDisplayName())
                .build();
    }
}