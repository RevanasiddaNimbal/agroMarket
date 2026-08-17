package com.agri.market.email.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {

    private String frontendUrl;

    private int passwordResetTokenExpirationMinutes;
}