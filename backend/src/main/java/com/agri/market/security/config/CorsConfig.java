package com.agri.market.security.config;

import com.agri.market.security.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsProperties properties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        final CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                properties.getAllowedOrigins()
        );

        configuration.setAllowedMethods(
                properties.getAllowedMethods()
        );

        configuration.setAllowedHeaders(
                properties.getAllowedHeaders()
        );

        configuration.setExposedHeaders(
                properties.getExposedHeaders()
        );

        configuration.setAllowCredentials(
                properties.isAllowCredentials()
        );

        configuration.setMaxAge(
                properties.getMaxAge()
        );

        final UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}