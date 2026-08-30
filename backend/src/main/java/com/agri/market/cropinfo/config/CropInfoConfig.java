package com.agri.market.cropinfo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(CropInfoProperties.class)
public class CropInfoConfig {

    @Bean
    public RestClient perenualRestClient(CropInfoProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }
}