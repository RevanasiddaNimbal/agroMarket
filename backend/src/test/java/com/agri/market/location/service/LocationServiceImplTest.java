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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private StateRepository stateRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private TalukRepository talukRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private LocationResolver locationResolver;

    @InjectMocks
    private LocationServiceImpl locationService;

    private State state;
    private District district;

    @BeforeEach
    void setUp() {
        state = mock(State.class);
        district = mock(District.class);
    }

    @Test
    void shouldReturnActiveStates() {
        StateResponseDto response = mock(StateResponseDto.class);

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(locationMapper.toStateResponse(state))
                .thenReturn(response);

        List<StateResponseDto> result =
                locationService.getActiveStates();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(response, result.getFirst());

        verify(stateRepository)
                .findAllByActiveTrueOrderByNameAsc();

        verify(locationMapper)
                .toStateResponse(state);

        verifyNoInteractions(
                districtRepository,
                talukRepository,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveStatesExist() {
        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of());

        List<StateResponseDto> result =
                locationService.getActiveStates();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stateRepository)
                .findAllByActiveTrueOrderByNameAsc();

        verifyNoInteractions(
                locationMapper,
                districtRepository,
                talukRepository,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldReturnActiveDistrictsByState() {
        String stateId = "KA";
        DistrictResponseDto response = mock(DistrictResponseDto.class);

        when(stateRepository.findById(stateId))
                .thenReturn(Optional.of(state));

        when(state.isActive())
                .thenReturn(true);

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc(stateId))
                .thenReturn(List.of(district));

        when(locationMapper.toDistrictResponse(district))
                .thenReturn(response);

        List<DistrictResponseDto> result =
                locationService.getActiveDistrictsByState(stateId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(response, result.getFirst());

        verify(stateRepository)
                .findById(stateId);

        verify(districtRepository)
                .findAllByStateIdAndActiveTrueOrderByNameAsc(stateId);

        verify(locationMapper)
                .toDistrictResponse(district);
    }

    @Test
    void shouldReturnEmptyDistrictListWhenNoActiveDistrictsExist() {
        String stateId = "KA";

        when(stateRepository.findById(stateId))
                .thenReturn(Optional.of(state));

        when(state.isActive())
                .thenReturn(true);

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc(stateId))
                .thenReturn(List.of());

        List<DistrictResponseDto> result =
                locationService.getActiveDistrictsByState(stateId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stateRepository)
                .findById(stateId);

        verify(districtRepository)
                .findAllByStateIdAndActiveTrueOrderByNameAsc(stateId);

        verifyNoInteractions(locationMapper);
    }

    @Test
    void shouldThrowStateNotFoundWhenStateIdIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveDistrictsByState(null)
        );

        assertEquals(
                ErrorCode.STATE_NOT_FOUND,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowStateNotFoundWhenStateIdIsBlank() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveDistrictsByState("   ")
        );

        assertEquals(
                ErrorCode.STATE_NOT_FOUND,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowStateNotFoundWhenStateDoesNotExist() {
        String stateId = "UNKNOWN";

        when(stateRepository.findById(stateId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveDistrictsByState(stateId)
        );

        assertEquals(
                ErrorCode.STATE_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(stateRepository)
                .findById(stateId);

        verifyNoInteractions(
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowStateNotFoundWhenStateIsInactive() {
        String stateId = "KA";

        when(stateRepository.findById(stateId))
                .thenReturn(Optional.of(state));

        when(state.isActive())
                .thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveDistrictsByState(stateId)
        );

        assertEquals(
                ErrorCode.STATE_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(stateRepository)
                .findById(stateId);

        verify(state)
                .isActive();

        verifyNoInteractions(
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldReturnActiveTaluksByDistrict() {
        String districtId = "VIJAYAPURA";

        TalukResponseDto response = mock(TalukResponseDto.class);

        when(districtRepository.findById(districtId))
                .thenReturn(Optional.of(district));

        when(district.isActive())
                .thenReturn(true);

        var taluk = mock(
                com.agri.market.location.entity.Taluk.class
        );

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(districtId))
                .thenReturn(List.of(taluk));

        when(locationMapper.toTalukResponse(taluk))
                .thenReturn(response);

        List<TalukResponseDto> result =
                locationService.getActiveTaluksByDistrict(districtId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(response, result.getFirst());

        verify(districtRepository)
                .findById(districtId);

        verify(talukRepository)
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(districtId);

        verify(locationMapper)
                .toTalukResponse(taluk);
    }

    @Test
    void shouldReturnEmptyTalukListWhenNoActiveTaluksExist() {
        String districtId = "VIJAYAPURA";

        when(districtRepository.findById(districtId))
                .thenReturn(Optional.of(district));

        when(district.isActive())
                .thenReturn(true);

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(districtId))
                .thenReturn(List.of());

        List<TalukResponseDto> result =
                locationService.getActiveTaluksByDistrict(districtId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(districtRepository)
                .findById(districtId);

        verify(talukRepository)
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(districtId);

        verifyNoInteractions(locationMapper);
    }

    @Test
    void shouldThrowDistrictNotFoundWhenDistrictIdIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveTaluksByDistrict(null)
        );

        assertEquals(
                ErrorCode.DISTRICT_NOT_FOUND,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowDistrictNotFoundWhenDistrictIdIsBlank() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveTaluksByDistrict("   ")
        );

        assertEquals(
                ErrorCode.DISTRICT_NOT_FOUND,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowDistrictNotFoundWhenDistrictDoesNotExist() {
        String districtId = "UNKNOWN";

        when(districtRepository.findById(districtId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveTaluksByDistrict(districtId)
        );

        assertEquals(
                ErrorCode.DISTRICT_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(districtRepository)
                .findById(districtId);

        verifyNoInteractions(
                stateRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowDistrictNotFoundWhenDistrictIsInactive() {
        String districtId = "VIJAYAPURA";

        when(districtRepository.findById(districtId))
                .thenReturn(Optional.of(district));

        when(district.isActive())
                .thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.getActiveTaluksByDistrict(districtId)
        );

        assertEquals(
                ErrorCode.DISTRICT_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(districtRepository)
                .findById(districtId);

        verify(district)
                .isActive();

        verifyNoInteractions(
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldSearchLocationsAcrossAllLocationTypes() {
        String query = "vijaya";

        LocationSearchResponseDto stateResponse =
                mock(LocationSearchResponseDto.class);

        LocationSearchResponseDto districtResponse =
                mock(LocationSearchResponseDto.class);

        LocationSearchResponseDto talukResponse =
                mock(LocationSearchResponseDto.class);

        var taluk =
                mock(com.agri.market.location.entity.Taluk.class);

        when(stateRepository.searchActiveStates(query))
                .thenReturn(List.of(state));

        when(districtRepository.searchActiveDistricts(query))
                .thenReturn(List.of(district));

        when(talukRepository.searchActiveTaluks(query))
                .thenReturn(List.of(taluk));

        when(locationMapper.toStateSearchResponse(state))
                .thenReturn(stateResponse);

        when(locationMapper.toDistrictSearchResponse(district))
                .thenReturn(districtResponse);

        when(locationMapper.toTalukSearchResponse(taluk))
                .thenReturn(talukResponse);

        LocationSearchRequestDto request =
                mock(LocationSearchRequestDto.class);

        when(request.getQuery())
                .thenReturn("  vijaya  ");

        List<LocationSearchResponseDto> result =
                locationService.searchLocations(request);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertSame(stateResponse, result.get(0));
        assertSame(districtResponse, result.get(1));
        assertSame(talukResponse, result.get(2));

        verify(request)
                .getQuery();

        verify(stateRepository)
                .searchActiveStates(query);

        verify(districtRepository)
                .searchActiveDistricts(query);

        verify(talukRepository)
                .searchActiveTaluks(query);

        verify(locationMapper)
                .toStateSearchResponse(state);

        verify(locationMapper)
                .toDistrictSearchResponse(district);

        verify(locationMapper)
                .toTalukSearchResponse(taluk);
    }

    @Test
    void shouldReturnEmptySearchResultsWhenRepositoriesReturnEmptyLists() {
        String query = "unknown";

        LocationSearchRequestDto request =
                mock(LocationSearchRequestDto.class);

        when(request.getQuery())
                .thenReturn("  unknown  ");

        when(stateRepository.searchActiveStates(query))
                .thenReturn(List.of());

        when(districtRepository.searchActiveDistricts(query))
                .thenReturn(List.of());

        when(talukRepository.searchActiveTaluks(query))
                .thenReturn(List.of());

        List<LocationSearchResponseDto> result =
                locationService.searchLocations(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stateRepository)
                .searchActiveStates(query);

        verify(districtRepository)
                .searchActiveDistricts(query);

        verify(talukRepository)
                .searchActiveTaluks(query);

        verifyNoInteractions(locationMapper);
    }

    @Test
    void shouldSearchExternalLocations() {
        String query = "Atharga";

        GeocodingResult geocodingResult =
                mock(GeocodingResult.class);

        ReverseGeocodeResponseDto response =
                mock(ReverseGeocodeResponseDto.class);

        when(geocodingClient.search(query))
                .thenReturn(List.of(geocodingResult));

        when(locationMapper.toReverseGeocodeResponse(geocodingResult))
                .thenReturn(response);

        List<ReverseGeocodeResponseDto> result =
                locationService.searchExternalLocations(
                        "  Atharga  "
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(response, result.getFirst());

        verify(geocodingClient)
                .search(query);

        verify(locationMapper)
                .toReverseGeocodeResponse(geocodingResult);
    }

    @Test
    void shouldReturnEmptyListWhenExternalSearchQueryIsNull() {
        List<ReverseGeocodeResponseDto> result =
                locationService.searchExternalLocations(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(
                geocodingClient,
                locationMapper
        );
    }

    @Test
    void shouldReturnEmptyListWhenExternalSearchQueryIsBlank() {
        List<ReverseGeocodeResponseDto> result =
                locationService.searchExternalLocations("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(
                geocodingClient,
                locationMapper
        );
    }

    @Test
    void shouldReverseGeocodeSuccessfully() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        GeocodingResult externalResult =
                mock(GeocodingResult.class);

        GeocodingResult resolvedResult =
                mock(GeocodingResult.class);

        ReverseGeocodeResponseDto response =
                mock(ReverseGeocodeResponseDto.class);

        when(request.getLatitude())
                .thenReturn(16.998412);

        when(request.getLongitude())
                .thenReturn(75.8547213);

        when(geocodingClient.reverseGeocode(
                16.998412,
                75.8547213
        )).thenReturn(externalResult);

        when(locationResolver.resolve(externalResult))
                .thenReturn(resolvedResult);

        when(locationMapper.toReverseGeocodeResponse(resolvedResult))
                .thenReturn(response);

        ReverseGeocodeResponseDto result =
                locationService.reverseGeocode(request);

        assertNotNull(result);
        assertSame(response, result);

        verify(request)
                .getLatitude();

        verify(request)
                .getLongitude();

        verify(geocodingClient)
                .reverseGeocode(
                        16.998412,
                        75.8547213
                );

        verify(locationResolver)
                .resolve(externalResult);

        verify(locationMapper)
                .toReverseGeocodeResponse(resolvedResult);
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenReverseGeocodeRequestIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(null)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository,
                locationMapper,
                geocodingClient,
                locationResolver
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLatitudeIsTooHigh() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(91.0);

        when(request.getLongitude())
                .thenReturn(75.0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verify(request)
                .getLatitude();

        verify(request)
                .getLongitude();

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLatitudeIsTooLow() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(-91.0);

        when(request.getLongitude())
                .thenReturn(75.0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLongitudeIsTooHigh() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(16.0);

        when(request.getLongitude())
                .thenReturn(181.0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLongitudeIsTooLow() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(16.0);

        when(request.getLongitude())
                .thenReturn(-181.0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLatitudeIsNaN() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(Double.NaN);

        when(request.getLongitude())
                .thenReturn(75.0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLongitudeIsNaN() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(16.0);

        when(request.getLongitude())
                .thenReturn(Double.NaN);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLatitudeIsInfinite() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(Double.POSITIVE_INFINITY);

        when(request.getLongitude())
                .thenReturn(75.0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowInvalidCoordinatesWhenLongitudeIsInfinite() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(16.0);

        when(request.getLongitude())
                .thenReturn(Double.NEGATIVE_INFINITY);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.INVALID_COORDINATES,
                exception.getErrorCode()
        );

        verifyNoInteractions(
                geocodingClient,
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowLocationResolutionFailedWhenGeocodingClientReturnsNull() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        when(request.getLatitude())
                .thenReturn(16.998412);

        when(request.getLongitude())
                .thenReturn(75.8547213);

        when(geocodingClient.reverseGeocode(
                16.998412,
                75.8547213
        )).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.LOCATION_RESOLUTION_FAILED,
                exception.getErrorCode()
        );

        verify(geocodingClient)
                .reverseGeocode(
                        16.998412,
                        75.8547213
                );

        verifyNoInteractions(
                locationResolver,
                locationMapper
        );
    }

    @Test
    void shouldThrowLocationResolutionFailedWhenResolverReturnsNull() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        GeocodingResult externalResult =
                mock(GeocodingResult.class);

        when(request.getLatitude())
                .thenReturn(16.998412);

        when(request.getLongitude())
                .thenReturn(75.8547213);

        when(geocodingClient.reverseGeocode(
                16.998412,
                75.8547213
        )).thenReturn(externalResult);

        when(locationResolver.resolve(externalResult))
                .thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> locationService.reverseGeocode(request)
        );

        assertEquals(
                ErrorCode.LOCATION_RESOLUTION_FAILED,
                exception.getErrorCode()
        );

        verify(geocodingClient)
                .reverseGeocode(
                        16.998412,
                        75.8547213
                );

        verify(locationResolver)
                .resolve(externalResult);

        verifyNoInteractions(locationMapper);
    }

    @Test
    void shouldAcceptBoundaryCoordinates() {
        ReverseGeocodeRequestDto request =
                mock(ReverseGeocodeRequestDto.class);

        GeocodingResult externalResult =
                mock(GeocodingResult.class);

        GeocodingResult resolvedResult =
                mock(GeocodingResult.class);

        ReverseGeocodeResponseDto response =
                mock(ReverseGeocodeResponseDto.class);

        when(request.getLatitude())
                .thenReturn(90.0);

        when(request.getLongitude())
                .thenReturn(180.0);

        when(geocodingClient.reverseGeocode(90.0, 180.0))
                .thenReturn(externalResult);

        when(locationResolver.resolve(externalResult))
                .thenReturn(resolvedResult);

        when(locationMapper.toReverseGeocodeResponse(resolvedResult))
                .thenReturn(response);

        ReverseGeocodeResponseDto result =
                locationService.reverseGeocode(request);

        assertSame(response, result);

        verify(geocodingClient)
                .reverseGeocode(90.0, 180.0);

        verify(locationResolver)
                .resolve(externalResult);

        verify(locationMapper)
                .toReverseGeocodeResponse(resolvedResult);
    }
}