package com.agri.market.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.login-attempt")
public class LoginAttemptProperties {

    private int maxAttempts = 5;
    private int lockDurationMinutes = 15;
}