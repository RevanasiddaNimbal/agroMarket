package com.agri.market.location.client;

import java.util.List;

public interface GeocodingClient {

    List<GeocodingResult> search(String query);

    GeocodingResult reverseGeocode(
            double latitude,
            double longitude
    );
}