package com.agri.market.address.controller;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.dto.CreateAddressRequestDto;
import com.agri.market.address.dto.UpdateAddressRequestDto;
import com.agri.market.address.service.AddressService;
import com.agri.market.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Addresses",
        description = "User address management APIs"
)
public class AddressController {

    private final AddressService addressService;

    @Operation(
            summary = "Create a new address",
            description = "Creates a new address for the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid address information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDto createAddress(
            @Valid @RequestBody final CreateAddressRequestDto request,
            final Authentication authentication
    ) {
        return addressService.createAddress(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Get current user's addresses",
            description = "Returns all addresses belonging to the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Addresses retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public List<AddressResponseDto> getUserAddresses(
            final Authentication authentication
    ) {
        return addressService.getUserAddresses(
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Get an address",
            description = "Returns a specific address belonging to the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    @GetMapping("/{addressId}")
    public AddressResponseDto getAddress(
            @PathVariable final String addressId,
            final Authentication authentication
    ) {
        return addressService.getAddress(
                addressId,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Update an address",
            description = "Updates an existing address belonging to the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid address information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    @PatchMapping("/{addressId}")
    public AddressResponseDto updateAddress(
            @PathVariable final String addressId,
            @Valid @RequestBody final UpdateAddressRequestDto request,
            final Authentication authentication
    ) {
        return addressService.updateAddress(
                addressId,
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Set default address",
            description = "Sets an existing address as the default address of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Default address changed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    @PatchMapping("/{addressId}/default")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setDefaultAddress(
            @PathVariable final String addressId,
            final Authentication authentication
    ) {
        addressService.setDefaultAddress(
                addressId,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Delete an address",
            description = "Deletes an address belonging to the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Address deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @PathVariable final String addressId,
            final Authentication authentication
    ) {
        addressService.deleteAddress(
                addressId,
                getAuthenticatedUserEmail(authentication)
        );
    }

    private String getAuthenticatedUserEmail(
            final Authentication authentication
    ) {
        final User user = (User) authentication.getPrincipal();
        return user.getEmail();
    }
}