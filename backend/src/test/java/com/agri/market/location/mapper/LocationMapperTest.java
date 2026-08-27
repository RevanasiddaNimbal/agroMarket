package com.agri.market.location.mapper;

import com.agri.market.location.client.GeocodingResult;
import com.agri.market.location.dto.*;
import com.agri.market.location.entity.District;
import com.agri.market.location.entity.State;
import com.agri.market.location.entity.Taluk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationMapperTest {

    private LocationMapper locationMapper;

    @BeforeEach
    void setUp() {
        locationMapper = new LocationMapper();
    }

    @Test
    void shouldMapStateToStateResponse() {
        State state = State.builder()
                .id("state-1")
                .name("Karnataka")
                .code("KA")
                .countryCode("IN")
                .active(true)
                .build();

        StateResponseDto result =
                locationMapper.toStateResponse(state);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("state-1");
        assertThat(result.getName()).isEqualTo("Karnataka");
        assertThat(result.getCode()).isEqualTo("KA");
        assertThat(result.getCountryCode()).isEqualTo("IN");
    }

    @Test
    void shouldReturnNullWhenStateIsNull() {
        StateResponseDto result =
                locationMapper.toStateResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapDistrictToDistrictResponse() {
        State state = State.builder()
                .id("state-1")
                .name("Karnataka")
                .code("KA")
                .countryCode("IN")
                .build();

        District district = District.builder()
                .id("district-1")
                .name("Vijayapura")
                .code("VIJ")
                .state(state)
                .active(true)
                .build();

        DistrictResponseDto result =
                locationMapper.toDistrictResponse(district);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("district-1");
        assertThat(result.getName()).isEqualTo("Vijayapura");
        assertThat(result.getCode()).isEqualTo("VIJ");
        assertThat(result.getStateId()).isEqualTo("state-1");
    }

    @Test
    void shouldMapDistrictWithoutState() {
        District district = District.builder()
                .id("district-1")
                .name("Vijayapura")
                .code("VIJ")
                .state(null)
                .active(true)
                .build();

        DistrictResponseDto result =
                locationMapper.toDistrictResponse(district);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("district-1");
        assertThat(result.getName()).isEqualTo("Vijayapura");
        assertThat(result.getCode()).isEqualTo("VIJ");
        assertThat(result.getStateId()).isNull();
    }

    @Test
    void shouldReturnNullWhenDistrictIsNull() {
        DistrictResponseDto result =
                locationMapper.toDistrictResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapTalukToTalukResponse() {
        State state = State.builder()
                .id("state-1")
                .name("Karnataka")
                .code("KA")
                .countryCode("IN")
                .build();

        District district = District.builder()
                .id("district-1")
                .name("Vijayapura")
                .code("VIJ")
                .state(state)
                .build();

        Taluk taluk = Taluk.builder()
                .id("taluk-1")
                .name("Indi")
                .code("IND")
                .district(district)
                .active(true)
                .build();

        TalukResponseDto result =
                locationMapper.toTalukResponse(taluk);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("taluk-1");
        assertThat(result.getName()).isEqualTo("Indi");
        assertThat(result.getCode()).isEqualTo("IND");
        assertThat(result.getDistrictId()).isEqualTo("district-1");
    }

    @Test
    void shouldMapTalukWithoutDistrict() {
        Taluk taluk = Taluk.builder()
                .id("taluk-1")
                .name("Indi")
                .code("IND")
                .district(null)
                .active(true)
                .build();

        TalukResponseDto result =
                locationMapper.toTalukResponse(taluk);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("taluk-1");
        assertThat(result.getName()).isEqualTo("Indi");
        assertThat(result.getCode()).isEqualTo("IND");
        assertThat(result.getDistrictId()).isNull();
    }

    @Test
    void shouldReturnNullWhenTalukIsNull() {
        TalukResponseDto result =
                locationMapper.toTalukResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapStateToLocationSearchResponse() {
        State state = State.builder()
                .id("state-1")
                .name("Karnataka")
                .code("KA")
                .countryCode("IN")
                .active(true)
                .build();

        LocationSearchResponseDto result =
                locationMapper.toStateSearchResponse(state);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("STATE");
        assertThat(result.getId()).isEqualTo("state-1");
        assertThat(result.getName()).isEqualTo("Karnataka");
        assertThat(result.getState()).isNotNull();
        assertThat(result.getState().getId()).isEqualTo("state-1");
        assertThat(result.getState().getName()).isEqualTo("Karnataka");
    }

    @Test
    void shouldReturnNullWhenStateSearchEntityIsNull() {
        LocationSearchResponseDto result =
                locationMapper.toStateSearchResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapDistrictToLocationSearchResponse() {
        State state = State.builder()
                .id("state-1")
                .name("Karnataka")
                .code("KA")
                .countryCode("IN")
                .build();

        District district = District.builder()
                .id("district-1")
                .name("Vijayapura")
                .code("VIJ")
                .state(state)
                .active(true)
                .build();

        LocationSearchResponseDto result =
                locationMapper.toDistrictSearchResponse(district);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("DISTRICT");
        assertThat(result.getId()).isEqualTo("district-1");
        assertThat(result.getName()).isEqualTo("Vijayapura");

        assertThat(result.getDistrict()).isNotNull();
        assertThat(result.getDistrict().getId())
                .isEqualTo("district-1");
        assertThat(result.getDistrict().getName())
                .isEqualTo("Vijayapura");

        assertThat(result.getState()).isNotNull();
        assertThat(result.getState().getId())
                .isEqualTo("state-1");
        assertThat(result.getState().getName())
                .isEqualTo("Karnataka");
    }

    @Test
    void shouldMapDistrictWithoutStateToLocationSearchResponse() {
        District district = District.builder()
                .id("district-1")
                .name("Vijayapura")
                .code("VIJ")
                .state(null)
                .active(true)
                .build();

        LocationSearchResponseDto result =
                locationMapper.toDistrictSearchResponse(district);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("DISTRICT");
        assertThat(result.getId()).isEqualTo("district-1");
        assertThat(result.getName()).isEqualTo("Vijayapura");
        assertThat(result.getDistrict()).isNotNull();
        assertThat(result.getState()).isNull();
    }

    @Test
    void shouldReturnNullWhenDistrictSearchEntityIsNull() {
        LocationSearchResponseDto result =
                locationMapper.toDistrictSearchResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapTalukToLocationSearchResponse() {
        State state = State.builder()
                .id("state-1")
                .name("Karnataka")
                .code("KA")
                .countryCode("IN")
                .build();

        District district = District.builder()
                .id("district-1")
                .name("Vijayapura")
                .code("VIJ")
                .state(state)
                .build();

        Taluk taluk = Taluk.builder()
                .id("taluk-1")
                .name("Indi")
                .code("IND")
                .district(district)
                .active(true)
                .build();

        LocationSearchResponseDto result =
                locationMapper.toTalukSearchResponse(taluk);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("TALUK");
        assertThat(result.getId()).isEqualTo("taluk-1");
        assertThat(result.getName()).isEqualTo("Indi");

        assertThat(result.getDistrict()).isNotNull();
        assertThat(result.getDistrict().getId())
                .isEqualTo("district-1");
        assertThat(result.getDistrict().getName())
                .isEqualTo("Vijayapura");

        assertThat(result.getState()).isNotNull();
        assertThat(result.getState().getId())
                .isEqualTo("state-1");
        assertThat(result.getState().getName())
                .isEqualTo("Karnataka");
    }

    @Test
    void shouldMapTalukWithoutDistrictToLocationSearchResponse() {
        Taluk taluk = Taluk.builder()
                .id("taluk-1")
                .name("Indi")
                .code("IND")
                .district(null)
                .active(true)
                .build();

        LocationSearchResponseDto result =
                locationMapper.toTalukSearchResponse(taluk);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("TALUK");
        assertThat(result.getId()).isEqualTo("taluk-1");
        assertThat(result.getName()).isEqualTo("Indi");
        assertThat(result.getDistrict()).isNull();
        assertThat(result.getState()).isNull();
    }

    @Test
    void shouldReturnNullWhenTalukSearchEntityIsNull() {
        LocationSearchResponseDto result =
                locationMapper.toTalukSearchResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapGeocodingResultToReverseGeocodeResponse() {
        GeocodingResult result = GeocodingResult.builder()
                .latitude(16.9871578)
                .longitude(75.8854156)
                .country("India")
                .countryCode("in")
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Indi")
                .city("Atharga")
                .pincode("586112")
                .displayName(
                        "Atharga, Indi, Vijayapura, Karnataka, India"
                )
                .build();

        ReverseGeocodeResponseDto response =
                locationMapper.toReverseGeocodeResponse(result);

        assertThat(response).isNotNull();
        assertThat(response.getLatitude())
                .isEqualTo(16.9871578);
        assertThat(response.getLongitude())
                .isEqualTo(75.8854156);
        assertThat(response.getCountry())
                .isEqualTo("India");
        assertThat(response.getCountryCode())
                .isEqualTo("in");
        assertThat(response.getState())
                .isEqualTo("Karnataka");
        assertThat(response.getDistrict())
                .isEqualTo("Vijayapura");
        assertThat(response.getTaluk())
                .isEqualTo("Indi");
        assertThat(response.getCity())
                .isEqualTo("Atharga");
        assertThat(response.getPincode())
                .isEqualTo("586112");
        assertThat(response.getDisplayName())
                .isEqualTo(
                        "Atharga, Indi, Vijayapura, Karnataka, India"
                );
    }

    @Test
    void shouldMapGeocodingResultWithNullAdministrativeFields() {
        GeocodingResult result = GeocodingResult.builder()
                .latitude(16.9871578)
                .longitude(75.8854156)
                .country("India")
                .countryCode("in")
                .state("Karnataka")
                .district(null)
                .taluk(null)
                .city("Atharga")
                .pincode("586112")
                .displayName("Atharga - 586112, KA, India")
                .build();

        ReverseGeocodeResponseDto response =
                locationMapper.toReverseGeocodeResponse(result);

        assertThat(response).isNotNull();
        assertThat(response.getLatitude())
                .isEqualTo(16.9871578);
        assertThat(response.getLongitude())
                .isEqualTo(75.8854156);
        assertThat(response.getCountry())
                .isEqualTo("India");
        assertThat(response.getCountryCode())
                .isEqualTo("in");
        assertThat(response.getState())
                .isEqualTo("Karnataka");
        assertThat(response.getDistrict()).isNull();
        assertThat(response.getTaluk()).isNull();
        assertThat(response.getCity())
                .isEqualTo("Atharga");
        assertThat(response.getPincode())
                .isEqualTo("586112");
        assertThat(response.getDisplayName())
                .isEqualTo("Atharga - 586112, KA, India");
    }

    @Test
    void shouldReturnNullWhenGeocodingResultIsNull() {
        ReverseGeocodeResponseDto response =
                locationMapper.toReverseGeocodeResponse(null);

        assertThat(response).isNull();
    }
}
