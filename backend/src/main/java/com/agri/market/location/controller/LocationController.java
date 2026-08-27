package com.agri.market.location.controller;

import com.agri.market.location.dto.*;
import com.agri.market.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Location",
        description = "APIs for state, district, taluk and location lookup"
)
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/states")
    @Operation(
            summary = "Get active states",
            description = "Returns all active states ordered alphabetically"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "States retrieved successfully"
            )
    })
    public List<StateResponseDto> getStates() {
        return locationService.getActiveStates();
    }

    @GetMapping("/states/{stateId}/districts")
    @Operation(
            summary = "Get districts by state",
            description = "Returns active districts belonging to the specified state"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Districts retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "State not found"
            )
    })
    public List<DistrictResponseDto> getDistricts(
            @Parameter(
                    description = "Unique state identifier",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable
            @NotBlank(message = "VALIDATION.LOCATION.STATE_ID.NOT_BLANK")
            String stateId
    ) {
        return locationService.getActiveDistrictsByState(stateId);
    }

    @GetMapping("/districts/{districtId}/taluks")
    @Operation(
            summary = "Get taluks by district",
            description = "Returns active taluks belonging to the specified district"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Taluks retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "District not found"
            )
    })
    public List<TalukResponseDto> getTaluks(
            @Parameter(
                    description = "Unique district identifier",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable
            @NotBlank(message = "VALIDATION.LOCATION.DISTRICT_ID.NOT_BLANK")
            String districtId
    ) {
        return locationService.getActiveTaluksByDistrict(districtId);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search locations",
            description = "Searches active states, districts and taluks by name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Location search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search query"
            )
    })
    public List<LocationSearchResponseDto> searchLocations(
            @Valid @ModelAttribute LocationSearchRequestDto request
    ) {
        return locationService.searchLocations(request);
    }

    @GetMapping("/geocode")
    @Operation(
            summary = "Search external locations",
            description = "Searches locations using the configured external geocoding provider"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "External locations retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search query"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Geocoding service unavailable"
            )
    })
    public List<ReverseGeocodeResponseDto> searchExternalLocations(
            @RequestParam
            @NotBlank(message = "VALIDATION.LOCATION.QUERY.NOT_BLANK")
            String query
    ) {
        return locationService.searchExternalLocations(query);
    }

    @GetMapping("/reverse-geocode")
    @Operation(
            summary = "Reverse geocode coordinates",
            description = "Resolves latitude and longitude into address information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Location resolved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid coordinates"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Geocoding service unavailable"
            )
    })
    public ReverseGeocodeResponseDto reverseGeocode(
            @Valid @ModelAttribute ReverseGeocodeRequestDto request
    ) {
        return locationService.reverseGeocode(request);
    }
}