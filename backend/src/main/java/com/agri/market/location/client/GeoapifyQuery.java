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
public class GeoapifyQuery {

    private Double lat;

    private Double lon;

    @JsonProperty("plus_code")
    private String plusCode;
}