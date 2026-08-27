package com.agri.market.location.controller;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.common.handler.ApplicationExceptionHandler;
import com.agri.market.location.dto.*;
import com.agri.market.location.service.LocationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationController")
class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.setConstraintValidatorFactory(
                new ConstraintValidatorFactory() {

                    @Override
                    public <T extends ConstraintValidator<?, ?>> T getInstance(
                            Class<T> key) {

                        try {
                            return key.getDeclaredConstructor()
                                    .newInstance();
                        } catch (Exception exception) {
                            throw new IllegalStateException(exception);
                        }
                    }

                    @Override
                    public void releaseInstance(
                            ConstraintValidator<?, ?> instance) {
                    }
                }
        );

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(locationController)
                .setControllerAdvice(
                        new ApplicationExceptionHandler()
                )
                .setValidator(validator)
                .build();
    }

    // ============================================================
    // GET STATES
    // ============================================================

    @Nested
    @DisplayName("getStates")
    class GetStatesTests {

        @Test
        void shouldGetActiveStates() throws Exception {

            when(locationService.getActiveStates())
                    .thenReturn(List.of());

            mockMvc.perform(
                            get("/api/v1/locations/states")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(locationService)
                    .getActiveStates();

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldReturnStates() throws Exception {

            StateResponseDto state =
                    new StateResponseDto();

            when(locationService.getActiveStates())
                    .thenReturn(List.of(state));

            mockMvc.perform(
                            get("/api/v1/locations/states")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(locationService)
                    .getActiveStates();

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldHandleStateServiceFailure() throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.STATE_NOT_FOUND
                    )
            ).when(locationService)
                    .getActiveStates();

            mockMvc.perform(
                            get("/api/v1/locations/states")
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("STATE_NOT_FOUND")
                    );

            verify(locationService)
                    .getActiveStates();

            verifyNoMoreInteractions(locationService);
        }
    }

    // ============================================================
    // GET DISTRICTS
    // ============================================================

    @Nested
    @DisplayName("getDistricts")
    class GetDistrictsTests {

        @Test
        void shouldGetDistricts() throws Exception {

            when(locationService
                    .getActiveDistrictsByState("KA"))
                    .thenReturn(List.of());

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/states/{stateId}/districts",
                                    "KA"
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(locationService)
                    .getActiveDistrictsByState("KA");

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldReturnDistricts() throws Exception {

            DistrictResponseDto district =
                    new DistrictResponseDto();

            when(locationService
                    .getActiveDistrictsByState("KA"))
                    .thenReturn(List.of(district));

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/states/{stateId}/districts",
                                    "KA"
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(locationService)
                    .getActiveDistrictsByState("KA");

            verifyNoMoreInteractions(locationService);
        }


        @Test
        void shouldHandleStateNotFound() throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.STATE_NOT_FOUND
                    )
            ).when(locationService)
                    .getActiveDistrictsByState("INVALID");

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/states/{stateId}/districts",
                                    "INVALID"
                            )
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("STATE_NOT_FOUND")
                    );

            verify(locationService)
                    .getActiveDistrictsByState("INVALID");

            verifyNoMoreInteractions(locationService);
        }
    }

    // ============================================================
    // GET TALUKS
    // ============================================================

    @Nested
    @DisplayName("getTaluks")
    class GetTaluksTests {

        @Test
        void shouldGetTaluks() throws Exception {

            when(locationService
                    .getActiveTaluksByDistrict("DISTRICT-1"))
                    .thenReturn(List.of());

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/districts/{districtId}/taluks",
                                    "DISTRICT-1"
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(locationService)
                    .getActiveTaluksByDistrict("DISTRICT-1");

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldReturnTaluks() throws Exception {

            TalukResponseDto taluk =
                    new TalukResponseDto();

            when(locationService
                    .getActiveTaluksByDistrict("DISTRICT-1"))
                    .thenReturn(List.of(taluk));

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/districts/{districtId}/taluks",
                                    "DISTRICT-1"
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(locationService)
                    .getActiveTaluksByDistrict("DISTRICT-1");

            verifyNoMoreInteractions(locationService);
        }


        @Test
        void shouldHandleDistrictNotFound() throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.DISTRICT_NOT_FOUND
                    )
            ).when(locationService)
                    .getActiveTaluksByDistrict("INVALID");

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/districts/{districtId}/taluks",
                                    "INVALID"
                            )
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("DISTRICT_NOT_FOUND")
                    );

            verify(locationService)
                    .getActiveTaluksByDistrict("INVALID");

            verifyNoMoreInteractions(locationService);
        }
    }

    // ============================================================
    // POST SEARCH LOCATIONS
    // ============================================================

    @Nested
    @DisplayName("searchLocations")
    class SearchLocationsTests {

        @Test
        void shouldSearchLocations() throws Exception {

            when(locationService.searchLocations(any()))
                    .thenReturn(List.of());

            mockMvc.perform(
                            post("/api/v1/locations/search")
                                    .param("query", "Vijayapura")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(locationService)
                    .searchLocations(any());

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldReturnSearchResults() throws Exception {

            LocationSearchResponseDto response =
                    new LocationSearchResponseDto();

            when(locationService.searchLocations(any()))
                    .thenReturn(List.of(response));

            mockMvc.perform(
                            post("/api/v1/locations/search")
                                    .param("query", "Vijayapura")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(locationService)
                    .searchLocations(any());

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldRejectMissingSearchQuery() throws Exception {

            mockMvc.perform(
                            post("/api/v1/locations/search")
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(locationService);
        }

        @Test
        void shouldRejectBlankSearchQuery() throws Exception {

            mockMvc.perform(
                            post("/api/v1/locations/search")
                                    .param("query", " ")
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(locationService);
        }
    }

    // ============================================================
    // EXTERNAL GEOCODING SEARCH
    // ============================================================

    @Nested
    @DisplayName("searchExternalLocations")
    class SearchExternalLocationsTests {

        @Test
        void shouldSearchExternalLocations() throws Exception {

            when(locationService
                    .searchExternalLocations("Vijayapura"))
                    .thenReturn(List.of());

            mockMvc.perform(
                            get("/api/v1/locations/geocode")
                                    .param(
                                            "query",
                                            "Vijayapura"
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(locationService)
                    .searchExternalLocations("Vijayapura");

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldReturnExternalLocations() throws Exception {

            ReverseGeocodeResponseDto response =
                    new ReverseGeocodeResponseDto();

            when(locationService
                    .searchExternalLocations("Vijayapura"))
                    .thenReturn(List.of(response));

            mockMvc.perform(
                            get("/api/v1/locations/geocode")
                                    .param(
                                            "query",
                                            "Vijayapura"
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(locationService)
                    .searchExternalLocations("Vijayapura");

            verifyNoMoreInteractions(locationService);
        }


    }

    // ============================================================
    // REVERSE GEOCODE
    // ============================================================

    @Nested
    @DisplayName("reverseGeocode")
    class ReverseGeocodeTests {

        @Test
        void shouldReverseGeocode() throws Exception {

            when(locationService.reverseGeocode(any()))
                    .thenReturn(
                            new ReverseGeocodeResponseDto()
                    );

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/reverse-geocode"
                            )
                                    .param(
                                            "latitude",
                                            "16.998412"
                                    )
                                    .param(
                                            "longitude",
                                            "75.8547213"
                                    )
                    )
                    .andExpect(status().isOk());

            verify(locationService)
                    .reverseGeocode(any());

            verifyNoMoreInteractions(locationService);
        }

        @Test
        void shouldRejectMissingLatitude() throws Exception {

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/reverse-geocode"
                            )
                                    .param(
                                            "longitude",
                                            "75.8547213"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(locationService);
        }

        @Test
        void shouldRejectMissingLongitude() throws Exception {

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/reverse-geocode"
                            )
                                    .param(
                                            "latitude",
                                            "16.998412"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(locationService);
        }

        @Test
        void shouldRejectInvalidLatitude() throws Exception {

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/reverse-geocode"
                            )
                                    .param(
                                            "latitude",
                                            "100"
                                    )
                                    .param(
                                            "longitude",
                                            "75.8547213"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(locationService);
        }

        @Test
        void shouldRejectInvalidLongitude() throws Exception {

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/reverse-geocode"
                            )
                                    .param(
                                            "latitude",
                                            "16.998412"
                                    )
                                    .param(
                                            "longitude",
                                            "200"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(locationService);
        }

        @Test
        void shouldHandleInvalidCoordinates()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.INVALID_COORDINATES
                    )
            ).when(locationService)
                    .reverseGeocode(any());

            mockMvc.perform(
                            get(
                                    "/api/v1/locations/reverse-geocode"
                            )
                                    .param(
                                            "latitude",
                                            "16.998412"
                                    )
                                    .param(
                                            "longitude",
                                            "75.8547213"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("INVALID_COORDINATES")
                    );

            verify(locationService)
                    .reverseGeocode(any());

            verifyNoMoreInteractions(locationService);
        }


    }
}