package com.agri.market.email.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
@EnableConfigurationProperties({
        BrevoProperties.class,
        EmailProperties.class})
public class EmailConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}