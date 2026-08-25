package com.agri.market.address.dto;

import com.agri.market.address.entity.AddressType;
import com.agri.market.address.entity.LocationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating an existing address")
public class UpdateAddressRequestDto {

    @Size(
            max = 200,
            message = "VALIDATION.ADDRESS.ADDRESS_LINE1.SIZE"
    )
    @Schema(
            description = "Primary address line",
            example = "12 Main Road",
            maxLength = 200
    )
    private String addressLine1;

    @Size(
            max = 200,
            message = "VALIDATION.ADDRESS.ADDRESS_LINE2.SIZE"
    )
    @Schema(
            description = "Additional address information or landmark",
            example = "Near Government School",
            maxLength = 200
    )
    private String addressLine2;

    @Size(
            max = 100,
            message = "VALIDATION.ADDRESS.VILLAGE.SIZE"
    )
    @Schema(
            description = "Village or locality name",
            example = "Nimbal",
            maxLength = 100
    )
    private String village;

    @Size(
            max = 100,
            message = "VALIDATION.ADDRESS.CITY.SIZE"
    )
    @Schema(
            description = "City or town",
            example = "Vijayapura",
            maxLength = 100
    )
    private String city;

    @Size(
            max = 100,
            message = "VALIDATION.ADDRESS.DISTRICT.SIZE"
    )
    @Schema(
            description = "District name",
            example = "Vijayapura",
            maxLength = 100
    )
    private String district;

    @Size(
            max = 100,
            message = "VALIDATION.ADDRESS.STATE.SIZE"
    )
    @Schema(
            description = "State name",
            example = "Karnataka",
            maxLength = 100
    )
    private String state;

    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "VALIDATION.ADDRESS.PINCODE.PATTERN"
    )
    @Schema(
            description = "Six-digit Indian postal PIN code",
            example = "586101",
            minLength = 6,
            maxLength = 6
    )
    private String pincode;

    @Size(
            max = 100,
            message = "VALIDATION.ADDRESS.COUNTRY.SIZE"
    )
    @Schema(
            description = "Country name",
            example = "India",
            maxLength = 100
    )
    private String country;

    @Schema(
            description = "How the address location was provided",
            example = "MAP",
            allowableValues = {
                    "MANUAL",
                    "MAP"
            }
    )
    private LocationType locationType;

    @DecimalMin(
            value = "-90.0",
            message = "VALIDATION.ADDRESS.LATITUDE.RANGE"
    )
    @DecimalMax(
            value = "90.0",
            message = "VALIDATION.ADDRESS.LATITUDE.RANGE"
    )
    @Digits(
            integer = 3,
            fraction = 7,
            message = "VALIDATION.ADDRESS.LATITUDE.FORMAT"
    )
    @Schema(
            description = "Latitude. Required when locationType is MAP.",
            example = "16.8302000"
    )
    private BigDecimal latitude;

    @DecimalMin(
            value = "-180.0",
            message = "VALIDATION.ADDRESS.LONGITUDE.RANGE"
    )
    @DecimalMax(
            value = "180.0",
            message = "VALIDATION.ADDRESS.LONGITUDE.RANGE"
    )
    @Digits(
            integer = 3,
            fraction = 7,
            message = "VALIDATION.ADDRESS.LONGITUDE.FORMAT"
    )
    @Schema(
            description = "Longitude. Required when locationType is MAP.",
            example = "75.7100000"
    )
    private BigDecimal longitude;

    @Schema(
            description = "Type of address",
            example = "HOME",
            allowableValues = {
                    "HOME",
                    "FARM",
                    "OTHER"
            }
    )
    private AddressType addressType;

    @Schema(
            description = "Whether this address should be the default address",
            example = "true"
    )
    private Boolean defaultAddress;
}