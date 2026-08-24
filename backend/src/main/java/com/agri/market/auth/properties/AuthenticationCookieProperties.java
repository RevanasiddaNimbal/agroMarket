package com.agri.market.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security.cookie")
public class AuthenticationCookieProperties {

    private boolean secure;

    private String sameSite = "Lax";

    private Duration accessTokenMaxAge =
            Duration.ofMinutes(15);

    private Duration refreshTokenMaxAge =
            Duration.ofDays(7);
}