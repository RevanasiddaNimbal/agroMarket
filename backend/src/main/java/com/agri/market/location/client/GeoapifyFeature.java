package com.agri.market.location.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyFeature {

    private String name;

    private String ref;

    private String country;

    @JsonProperty("country_code")
    private String countryCode;

    private String state;

    private String county;

    @JsonProperty("state_district")
    private String stateDistrict;

    private String district;

    private String city;

    private String village;

    private String suburb;

    private String street;

    @JsonProperty("postcode")
    private String postcode;

    private Double lat;

    private Double lon;

    private String formatted;

    @JsonProperty("address_line1")
    private String addressLine1;

    @JsonProperty("address_line2")
    private String addressLine2;

    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("result_type")
    private String resultType;

    @JsonProperty("plus_code")
    private String plusCode;
}