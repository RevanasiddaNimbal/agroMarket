package com.agri.market.cropinfo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "crop-info.perenual")
public class CropInfoProperties {

    private String baseUrl;

    private String apiKey;
}