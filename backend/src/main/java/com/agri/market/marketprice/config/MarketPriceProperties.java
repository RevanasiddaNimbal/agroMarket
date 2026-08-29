package com.agri.market.marketprice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "market-price")
public class MarketPriceProperties {

    private String primaryProvider = "farmer-in";
    private String fallbackProvider = "mandi-price";

    private FarmerIn farmerIn = new FarmerIn();
    private MandiPrice mandiPrice = new MandiPrice();

    @Getter
    @Setter
    public static class FarmerIn {

        private String baseUrl = "https://farmer.in";
    }

    @Getter
    @Setter
    public static class MandiPrice {

        private String baseUrl = "https://mandi-api.onrender.com";
    }
}