package com.agri.market.password.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.password")
public class PasswordSecurityProperties {

    private long expirationMinutes;
}