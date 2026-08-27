package com.agri.market.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reverse geocoded location information")
public class ReverseGeocodeResponseDto {

    @Schema(example = "16.8302")
    private Double latitude;

    @Schema(example = "75.7100")
    private Double longitude;

    @Schema(example = "India")
    private String country;

    @Schema(example = "IN")
    private String countryCode;

    @Schema(example = "Karnataka")
    private String state;

    @Schema(example = "Vijayapura")
    private String district;

    @Schema(example = "Indi")
    private String taluk;

    @Schema(example = "Vijayapura")
    private String city;

    @Schema(example = "586101")
    private String pincode;

    @Schema(example = "Angondhalli, Karnataka, India")
    private String displayName;
}