package com.agri.market.location.client;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingResult {

    private Double latitude;

    private Double longitude;

    private String country;

    private String countryCode;

    private String state;

    private String district;

    private String taluk;

    private String village;

    private String city;

    private String pincode;

    private String displayName;

    /**
     * Geoapify's county field.
     * <p>
     * Example:
     * "Indi taluku"
     */
    private String county;

    /**
     * Geoapify's state_district field.
     * <p>
     * Example:
     * "Vijayapura"
     */
    private String stateDistrict;
}