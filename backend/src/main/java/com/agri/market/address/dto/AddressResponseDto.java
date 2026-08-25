package com.agri.market.address.dto;

import com.agri.market.address.entity.AddressType;
import com.agri.market.address.entity.LocationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing address information")
public class AddressResponseDto {

    @Schema(
            description = "Unique identifier of the address",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String id;

    @Schema(
            description = "Primary address line",
            example = "12 Main Road"
    )
    private String addressLine1;

    @Schema(
            description = "Additional address information or landmark",
            example = "Near Government School"
    )
    private String addressLine2;

    @Schema(
            description = "Village or locality name",
            example = "Nimbal"
    )
    private String village;

    @Schema(
            description = "City or town",
            example = "Vijayapura"
    )
    private String city;

    @Schema(
            description = "District name",
            example = "Vijayapura"
    )
    private String district;

    @Schema(
            description = "State name",
            example = "Karnataka"
    )
    private String state;

    @Schema(
            description = "Postal PIN code",
            example = "586101"
    )
    private String pincode;

    @Schema(
            description = "Country name",
            example = "India"
    )
    private String country;

    @Schema(
            description = "How the address location was provided",
            example = "MAP"
    )
    private LocationType locationType;

    @Schema(
            description = "Latitude of the address location",
            example = "16.8302000"
    )
    private BigDecimal latitude;

    @Schema(
            description = "Longitude of the address location",
            example = "75.7100000"
    )
    private BigDecimal longitude;

    @Schema(
            description = "Type of address",
            example = "HOME"
    )
    private AddressType addressType;

    @Schema(
            description = "Whether this is the user's default address",
            example = "true"
    )
    private boolean defaultAddress;

    @Schema(
            description = "Date and time when the address was created"
    )
    private LocalDateTime createdDate;

    @Schema(
            description = "Date and time when the address was last modified"
    )
    private LocalDateTime lastModifiedDate;
}